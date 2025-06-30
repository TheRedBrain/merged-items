package com.github.theredbrain.mergeditems.network.packet;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SplitMergedItemStacksPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<SplitMergedItemStacksPacket> {
	@Override
	public void receive(SplitMergedItemStacksPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity player = context.player();

		ScreenHandler screenHandler = player.currentScreenHandler;

		if (screenHandler instanceof ItemMergingScreenHandler itemMergingScreenHandler) {
			ItemStack containerItemStack = itemMergingScreenHandler.inventory.getStack(2).copy();
			ItemStack itemCostItemStack = itemMergingScreenHandler.inventory.getStack(4).copy();

			if (containerItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_splitting.container_stack_empty"));
				return;
			}

			MergedItemsComponent mergedItemsComponent = containerItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			if (mergedItemsComponent == null) {
				player.sendMessage(Text.translatable("hud.message.item_splitting.no_merged_items", containerItemStack.getName()));
			} else {

				if (mergedItemsComponent.isEmpty()) {
					player.sendMessage(Text.translatable("hud.message.item_splitting.no_merged_items", containerItemStack.getName()));
					return;
				}

				if (!itemMergingScreenHandler.inventory.getStack(3).isEmpty()) {
					player.sendMessage(Text.translatable("hud.message.item_splitting.extract_slot_not_empty"));
					return;
				}

				int merge_cost_amount = (int) Math.floor((mergedItemsComponent.merging_cost() >= 0 ? mergedItemsComponent.merging_cost() : itemMergingScreenHandler.getDefaultItemCostAmount()) * itemMergingScreenHandler.getSplittingItemCostMultiplier());
				Item itemCost = Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get());
				if (merge_cost_amount > 0 && itemCost != Items.AIR && !player.isCreative()) {
					if ((!itemCostItemStack.isOf(itemCost) || itemCostItemStack.getCount() < merge_cost_amount)) {
						player.sendMessage(Text.translatable("hud.message.item_splitting.missing_item_cost", itemCost.getName()));
						return;
					}
					itemCostItemStack.setCount(itemCostItemStack.getCount() - merge_cost_amount);
				}
				MergedItemsComponent.Builder builder = new MergedItemsComponent.Builder(mergedItemsComponent);

				ItemStack extractedItemStack = builder.removeLast();

				int extracted_item_cost = 0;
				MergedItemsComponent mergedItemsComponentOfExtractedItemStack = extractedItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

				if (mergedItemsComponentOfExtractedItemStack != null) {
					extracted_item_cost = mergedItemsComponentOfExtractedItemStack.merging_cost() >= 0 ? mergedItemsComponentOfExtractedItemStack.merging_cost() : itemMergingScreenHandler.getDefaultItemCostAmount();
				}
				builder.withMergingCost(mergedItemsComponent.merging_cost() - extracted_item_cost);

				containerItemStack.set(MergedItems.MERGED_ITEMS_COMPONENT_TYPE, builder.build());
				itemMergingScreenHandler.inventory.setStack(2, containerItemStack);
				itemMergingScreenHandler.inventory.setStack(3, extractedItemStack);
				itemMergingScreenHandler.inventory.markDirty();
			}
		}
	}
}