package com.github.theredbrain.mergeditems;

import com.github.theredbrain.inventorysizeattributes.InventorySizeAttributes;
import com.github.theredbrain.mergeditems.component.type.ItemMergingUtilityComponent;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.config.ServerConfig;
import com.github.theredbrain.mergeditems.registry.BlockRegistry;
import com.github.theredbrain.mergeditems.registry.EntityRegistry;
import com.github.theredbrain.mergeditems.registry.ItemComponentRegistry;
import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import com.github.theredbrain.mergeditems.registry.ServerPacketRegistry;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergedItems implements ModInitializer {
	public static final String MOD_ID = "mergeditems";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;

	public static ComponentType<MergedItemsComponent> MERGED_ITEMS_COMPONENT_TYPE;
	public static ComponentType<ItemMergingUtilityComponent> ITEM_MERGING_UTILITY_COMPONENT_TYPE;

	public static final boolean isInventorySizeAttributesLoaded = FabricLoader.getInstance().isModLoaded("inventorysizeattributes");
	public static final boolean isTrinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

	public static int getActiveInventorySize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? InventorySizeAttributes.getActiveInventorySlotAmount(player) : 27;
	}

	public static int getActiveHotbarSize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? InventorySizeAttributes.getActiveHotbarSlotAmount(player) : 9;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Merging your items since 2024!");
		SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);

		BlockRegistry.init();
		EntityRegistry.init();
		ItemComponentRegistry.init();
		ScreenHandlerTypesRegistry.registerAll();
		ServerPacketRegistry.init();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}