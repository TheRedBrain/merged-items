package com.github.theredbrain.mergeditems.network.packet;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class MergeItemStacksPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<MergeItemStacksPacket> {
	@Override
	public void receive(MergeItemStacksPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity player = context.player();

		ScreenHandler screenHandler = player.currentScreenHandler;

		if (screenHandler instanceof ItemMergingScreenHandler itemMergingScreenHandler) {
			ItemStack mergedItemStack = itemMergingScreenHandler.inventory.getStack(0).copy();
			ItemStack containerItemStack = itemMergingScreenHandler.inventory.getStack(1).copy();
			ItemStack itemCostItemStack = itemMergingScreenHandler.inventory.getStack(4).copy();

			if (containerItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.container_stack_empty"));
				return;
			}

			if (mergedItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.merged_stack_empty"));
				return;
			}

			if (containerItemStack.getItem() == mergedItemStack.getItem()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.no_merging_of_identical_items"));
				return;
			}

			List<String> stringList = itemMergingScreenHandler.getMergeableItemTags();

			// if no lists are provided, merging all items is possible
			boolean bl = stringList.isEmpty();
			if (!bl) {
				for (String string : stringList) {
					if (!string.isEmpty()) {
						bl = containerItemStack.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of(string)));
					}
				}
			}
			if (!bl) {
				player.sendMessage(Text.translatable("hud.message.item_merging.not_allowed_to_merge_items_into", containerItemStack.getName()));
				return;
			}

			if (mergedItemStack.getMaxCount() > 1) {
				player.sendMessage(Text.translatable("hud.message.item_merging.item_can_not_be_merged_into_other_items", mergedItemStack.getName()));
				return;
			}

			MergedItemsComponent mergedItemsComponentOfMeldedItemStack = mergedItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			int merged_item_cost = 0;
			if (mergedItemsComponentOfMeldedItemStack != null && !mergedItemsComponentOfMeldedItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.merged_item_stack_has_items_merged", mergedItemStack.getName()));
				merged_item_cost = mergedItemsComponentOfMeldedItemStack.merging_cost() >= 0 ? mergedItemsComponentOfMeldedItemStack.merging_cost() : itemMergingScreenHandler.getDefaultItemCostAmount();
			}

			MergedItemsComponent mergedItemsComponentOfContainerItemStack = containerItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			if (mergedItemsComponentOfContainerItemStack == null) {
				player.sendMessage(Text.translatable("hud.message.item_merging.items_can_not_be_merged_into", containerItemStack.getName()));
			} else {

				if (mergedItemsComponentOfContainerItemStack.size() >= itemMergingScreenHandler.getMergedItemsAmountMaximum()) {
					player.sendMessage(Text.translatable("hud.message.item_merging.item_has_reached_current_max_amount_of_merged_items", containerItemStack.getName()));
					return;
				}

				String possible_merging_items = mergedItemsComponentOfContainerItemStack.possible_merging_items();
				if (!possible_merging_items.isEmpty() && !mergedItemStack.isIn(TagKey.of(Registries.ITEM.getKey(), Identifier.of(possible_merging_items)))) {
					player.sendMessage(Text.translatable("hud.message.item_merging.item_can_not_be_merged_into_item", mergedItemStack.getName(), containerItemStack.getName()));
					return;
				}

				int container_item_cost = mergedItemsComponentOfContainerItemStack.merging_cost() >= 0 ? mergedItemsComponentOfContainerItemStack.merging_cost() : itemMergingScreenHandler.getDefaultItemCostAmount();
				int merge_cost_amount = (int) Math.floor((container_item_cost + merged_item_cost) * itemMergingScreenHandler.getMergingItemCostMultiplier());
				Item itemCost = Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get());
				if (merge_cost_amount > 0 && itemCost != Items.AIR && !player.isCreative()) {
					if ((!itemCostItemStack.isOf(itemCost) || itemCostItemStack.getCount() < merge_cost_amount)) {
						player.sendMessage(Text.translatable("hud.message.item_merging.missing_item_cost", itemCost.getName()));
						return;
					}
					itemCostItemStack.setCount(itemCostItemStack.getCount() - merge_cost_amount);
				}

				MergedItemsComponent.Builder builder = new MergedItemsComponent.Builder(mergedItemsComponentOfContainerItemStack);
				builder.add(mergedItemStack);
				builder.withMergingCost(container_item_cost + merged_item_cost);
				containerItemStack.set(MergedItems.MERGED_ITEMS_COMPONENT_TYPE, builder.build());

				itemMergingScreenHandler.inventory.setStack(0, ItemStack.EMPTY);
				itemMergingScreenHandler.inventory.setStack(1, containerItemStack);
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