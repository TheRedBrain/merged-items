package com.github.theredbrain.mergeditems.network.packet;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.ItemMergingUtilityComponent;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
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
			ItemStack containerItemStack = itemMergingScreenHandler.inventory.getStack(1);
			ItemStack mergedItemStack = itemMergingScreenHandler.inventory.getStack(0);

			if (containerItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.container_stack_empty"));
				return;
			}

			if (mergedItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.merged_stack_empty"));
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

			if (mergedItemsComponentOfMeldedItemStack != null && !mergedItemsComponentOfMeldedItemStack.isEmpty()) {
				player.sendMessage(Text.translatable("hud.message.item_merging.merged_item_stack_has_items_merged", mergedItemStack.getName()));
			}

			MergedItemsComponent mergedItemsComponentOfContainerItemStack = containerItemStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			if (mergedItemsComponentOfContainerItemStack == null) {
				player.sendMessage(Text.translatable("hud.message.item_merging.items_can_not_be_merged_into", containerItemStack.getName()));
			} else {

				if (mergedItemsComponentOfContainerItemStack.size() >= itemMergingScreenHandler.getMaxMergedItemsAmount()) {
					player.sendMessage(Text.translatable("hud.message.item_merging.item_has_reached_current_max_amount_of_merged_items", containerItemStack.getName()));
					return;
				}

				ItemMergingUtilityComponent itemMergingUtilityComponent = containerItemStack.get(MergedItems.ITEM_MERGING_UTILITY_COMPONENT_TYPE);
				if (itemMergingUtilityComponent != null) {
					String possible_merging_items = itemMergingUtilityComponent.possible_merging_items();
					if (!possible_merging_items.isEmpty() && !mergedItemStack.isIn(TagKey.of(Registries.ITEM.getKey(), Identifier.of(possible_merging_items)))) {
						player.sendMessage(Text.translatable("hud.message.item_merging.item_can_not_be_merged_into_item", mergedItemStack.getName(), containerItemStack.getName()));
						return;
					}
				}

				MergedItemsComponent.Builder builder = new MergedItemsComponent.Builder(mergedItemsComponentOfContainerItemStack);
				builder.add(mergedItemStack);
				containerItemStack.set(MergedItems.MERGED_ITEMS_COMPONENT_TYPE, builder.build());

				itemMergingScreenHandler.inventory.setStack(0, ItemStack.EMPTY);
			}
		}
	}
}