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

			MergedItemsComponent mergedItemsComponentOfContainerItemStack = containerItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			if (mergedItemsComponentOfContainerItemStack == null) {
				player.sendMessage(Text.translatable("hud.message.item_splitting.no_merged_items", containerItemStack.getName()));
			} else {

				if (mergedItemsComponentOfContainerItemStack.isEmpty()) {
					player.sendMessage(Text.translatable("hud.message.item_splitting.no_merged_items", containerItemStack.getName()));
					return;
				}

				if (!itemMergingScreenHandler.inventory.getStack(3).isEmpty()) {
					player.sendMessage(Text.translatable("hud.message.item_splitting.extract_slot_not_empty"));
					return;
				}

				int exp_cost_amount = (int) Math.floor((mergedItemsComponentOfContainerItemStack.merging_exp_cost() >= 0 ? mergedItemsComponentOfContainerItemStack.merging_exp_cost() : itemMergingScreenHandler.getDefaultExpCostAmount()) * itemMergingScreenHandler.getSplittingExpCostMultiplier());
				if (exp_cost_amount > 0 && !player.isCreative()) {
					if (player.experienceLevel < exp_cost_amount) {
						player.sendMessage(Text.translatable("hud.message.item_splitting.missing_exp_cost"));
						return;
					}
					player.applyEnchantmentCosts(containerItemStack, exp_cost_amount);
				}

				int item_cost_amount = (int) Math.floor((mergedItemsComponentOfContainerItemStack.merging_item_cost() >= 0 ? mergedItemsComponentOfContainerItemStack.merging_item_cost() : itemMergingScreenHandler.getDefaultItemCostAmount()) * itemMergingScreenHandler.getSplittingItemCostMultiplier());
				Item itemCost = Registries.ITEM.get(MergedItems.SERVER_CONFIG.splitting_item_cost.get());
				if (item_cost_amount > 0 && itemCost != Items.AIR && !player.isCreative()) {
					if ((!itemCostItemStack.isOf(itemCost) || itemCostItemStack.getCount() < item_cost_amount)) {
						player.sendMessage(Text.translatable("hud.message.item_splitting.missing_item_cost", itemCost.getName()));
						return;
					}
					itemCostItemStack.setCount(itemCostItemStack.getCount() - item_cost_amount);
				}
				MergedItemsComponent.Builder builder = new MergedItemsComponent.Builder(mergedItemsComponentOfContainerItemStack);

				ItemStack extractedItemStack = builder.removeLast();

				int extracted_item_cost;
				int extracted_exp_cost;
				MergedItemsComponent mergedItemsComponentOfExtractedItemStack = extractedItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

				if (mergedItemsComponentOfExtractedItemStack != null) {
					extracted_item_cost = mergedItemsComponentOfExtractedItemStack.merging_item_cost() >= 0 ? mergedItemsComponentOfExtractedItemStack.merging_item_cost() : itemMergingScreenHandler.getDefaultItemCostAmount();
					extracted_exp_cost = mergedItemsComponentOfExtractedItemStack.merging_exp_cost() >= 0 ? mergedItemsComponentOfExtractedItemStack.merging_exp_cost() : itemMergingScreenHandler.getDefaultExpCostAmount();
				} else {
					extracted_item_cost = itemMergingScreenHandler.getDefaultItemCostAmount();
					extracted_exp_cost = itemMergingScreenHandler.getDefaultExpCostAmount();
				}
				builder.withMergingItemCost(mergedItemsComponentOfContainerItemStack.merging_item_cost() - extracted_item_cost);
				builder.withMergingExpCost(mergedItemsComponentOfContainerItemStack.merging_exp_cost() - extracted_exp_cost);

				containerItemStack.set(MergedItems.MERGED_ITEMS_COMPONENT_TYPE, builder.build());
				itemMergingScreenHandler.inventory.setStack(2, containerItemStack);
				itemMergingScreenHandler.inventory.setStack(3, extractedItemStack);
				if (itemCostItemStack.getCount() < 1) {
					itemMergingScreenHandler.inventory.setStack(4, ItemStack.EMPTY);
				} else {
					itemMergingScreenHandler.inventory.setStack(4, itemCostItemStack);
				}
				itemMergingScreenHandler.inventory.markDirty();
			}
		}
	}
}