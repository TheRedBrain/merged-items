package com.github.theredbrain.mergeditems.compatibility;

import com.github.theredbrain.inventorysizeattributes.InventorySizeAttributesClient;
import com.github.theredbrain.inventorysizeattributes.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.mergeditems.MergedItems;
import net.minecraft.entity.player.PlayerEntity;

public class InventorySizeAttributesClientCompat {

	public static boolean showInactiveInventorySlots() {
		return InventorySizeAttributesClient.CLIENT_CONFIG.show_inactive_inventory_slots.get();
	}

}
