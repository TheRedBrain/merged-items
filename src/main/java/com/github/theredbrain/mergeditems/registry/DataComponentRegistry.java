package com.github.theredbrain.mergeditems.registry;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.predicate.item.MergedItemsPredicate;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.ContainerComponentModifiers;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.stream.Stream;

public class DataComponentRegistry {
	static {
		MergedItems.MERGED_ITEMS_COMPONENT_TYPE = Registry.register(
				Registries.DATA_COMPONENT_TYPE,
				MergedItems.identifier("merged_items"),
				ComponentType.<MergedItemsComponent>builder().codec(MergedItemsComponent.CODEC).packetCodec(MergedItemsComponent.PACKET_CODEC).build()
		);
		MergedItems.MERGED_ITEMS_SUB_PREDICATE = Registry.register(
				Registries.ITEM_SUB_PREDICATE_TYPE,
				"merged_items",
				new ItemSubPredicate.Type<>(MergedItemsPredicate.CODEC)
		);
		MergedItems.MERGED_ITEMS_CONTAINER_COMPONENT_MODIFIER = new ContainerComponentModifier<MergedItemsComponent>() {
			@Override
			public ComponentType<MergedItemsComponent> getComponentType() {
				return MergedItems.MERGED_ITEMS_COMPONENT_TYPE;
			}

			public MergedItemsComponent getDefault() {
				return MergedItemsComponent.DEFAULT;
			}

			public Stream<ItemStack> stream(MergedItemsComponent customBundleContentsComponent) {
				return customBundleContentsComponent.stream();
			}

			public MergedItemsComponent create(MergedItemsComponent customBundleContentsComponent, Stream<ItemStack> stream) {
				MergedItemsComponent.Builder builder = new MergedItemsComponent.Builder(customBundleContentsComponent).clear();
				stream.forEach(builder::add);
				return builder.build();
			}
		};
		ContainerComponentModifiers.TYPE_TO_MODIFIER.put(MergedItems.MERGED_ITEMS_COMPONENT_TYPE, MergedItems.MERGED_ITEMS_CONTAINER_COMPONENT_MODIFIER);
	}

	public static void init() {
	}
}
