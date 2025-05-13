package com.github.theredbrain.mergeditems.registry;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.block.entity.ItemMergingBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityRegistry {

	public static final BlockEntityType<ItemMergingBlockEntity> ITEM_MERGING_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
			MergedItems.identifier("item_merging_block"),
			FabricBlockEntityTypeBuilder.create(ItemMergingBlockEntity::new, BlockRegistry.ITEM_MERGING_BLOCK).build());

	public static void init() {
	}
}
