package com.github.theredbrain.mergeditems;

import com.github.theredbrain.mergeditems.compatibility.InventorySizeAttributesCompat;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.config.ServerConfig;
import com.github.theredbrain.mergeditems.predicate.item.MergedItemsPredicate;
import com.github.theredbrain.mergeditems.registry.BlockRegistry;
import com.github.theredbrain.mergeditems.registry.DataComponentRegistry;
import com.github.theredbrain.mergeditems.registry.EntityRegistry;
import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import com.github.theredbrain.mergeditems.registry.ServerPacketRegistry;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergedItems implements ModInitializer {
	public static final String MOD_ID = "mergeditems";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;

	public static ComponentType<MergedItemsComponent> MERGED_ITEMS_COMPONENT_TYPE;
	public static ItemSubPredicate.Type<MergedItemsPredicate> MERGED_ITEMS_SUB_PREDICATE;
	public static ContainerComponentModifier<MergedItemsComponent> MERGED_ITEMS_CONTAINER_COMPONENT_MODIFIER;

	public static final boolean isInventorySizeAttributesLoaded = FabricLoader.getInstance().isModLoaded("inventorysizeattributes");
	public static final boolean isTrinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

	public static int getActiveInventorySize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? InventorySizeAttributesCompat.getActiveInventorySize(player) : 27;
	}

	public static int getActiveHotbarSize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? InventorySizeAttributesCompat.getActiveHotbarSize(player) : 9;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Merging your items since 2024!");
		SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);

		BlockRegistry.init();
		EntityRegistry.init();
		DataComponentRegistry.init();
		ScreenHandlerTypesRegistry.registerAll();
		ServerPacketRegistry.init();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static void info(String message) {
		LOGGER.info("[" + MOD_ID + "] [info]: " + message);
	}

}