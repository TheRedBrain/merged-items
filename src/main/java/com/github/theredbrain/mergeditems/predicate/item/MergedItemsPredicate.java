package com.github.theredbrain.mergeditems.predicate.item;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.item.ComponentSubPredicate;
import net.minecraft.predicate.item.ItemPredicate;

import java.util.Optional;

public record MergedItemsPredicate(
		Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements ComponentSubPredicate<MergedItemsComponent> {
	public static final Codec<MergedItemsPredicate> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(CollectionPredicate.createCodec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(MergedItemsPredicate::items))
					.apply(instance, MergedItemsPredicate::new)
	);

	@Override
	public ComponentType<MergedItemsComponent> getComponentType() {
		return MergedItems.MERGED_ITEMS_COMPONENT_TYPE;
	}

	public boolean test(ItemStack itemStack, MergedItemsComponent mergedItemsComponent) {
		return !this.items.isPresent() || ((CollectionPredicate) this.items.get()).test(mergedItemsComponent.iterate());
	}
}
