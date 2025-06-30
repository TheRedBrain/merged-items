package com.github.theredbrain.mergeditems;

import com.github.theredbrain.inventorysizeattributes.InventorySizeAttributesClient;
import com.github.theredbrain.mergeditems.gui.screen.ingame.ItemMergingScreen;
import com.github.theredbrain.mergeditems.network.packet.OpenItemMergingScreenPacket;
import com.github.theredbrain.mergeditems.registry.ClientEventsRegistry;
import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import java.util.List;

public class MergedItemsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ScreenHandlerTypesRegistry.MERGING_ITEMS_SCREEN_HANDLER, ItemMergingScreen::new);

		ClientEventsRegistry.initializeClientEvents();
	}

	public static boolean showInactiveInventorySlots() {
		return MergedItems.isInventorySizeAttributesLoaded ? InventorySizeAttributesClient.CLIENT_CONFIG.show_inactive_inventory_slots.get() : true;
	}

	public static void openItemMergingScreen(MinecraftClient client, int defaultItemCostAmount, double mergingItemCostMultiplier, double splittingItemCostMultiplier, int mergedItemsAmountMaximum, String title, List<String> list) {
		if (client.player != null) {
			ClientPlayNetworking.send(new OpenItemMergingScreenPacket(defaultItemCostAmount, mergingItemCostMultiplier, splittingItemCostMultiplier, mergedItemsAmountMaximum, title, list));
		}
	}

}