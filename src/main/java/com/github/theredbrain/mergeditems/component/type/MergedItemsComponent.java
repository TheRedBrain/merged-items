package com.github.theredbrain.mergeditems.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record MergedItemsComponent(Content content/*, Optional<RegistryEntryList<Item>> tag  TODO replace string in 1.21.4*/, String possible_merging_items) {
	public static final MergedItemsComponent DEFAULT = new MergedItemsComponent();
	public static final Codec<MergedItemsComponent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							MergedItemsComponent.Content.CODEC.fieldOf("content").forGetter(component -> component.content),
//							RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("tag").forGetter(component -> component.tag),
							Codec.STRING.fieldOf("possible_merging_items").forGetter(component -> component.possible_merging_items)
					)
					.apply(instance, MergedItemsComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, MergedItemsComponent> PACKET_CODEC = PacketCodec.tuple(
			MergedItemsComponent.Content.PACKET_CODEC,
			component -> component.content,
//			PacketCodecs.optional(PacketCodecs.registryEntryList(RegistryKeys.ITEM)),
//			component -> component.tag,
			PacketCodecs.STRING,
			component -> component.possible_merging_items,
			MergedItemsComponent::new
	);
//	final List<ItemStack> stacks;

	public MergedItemsComponent() {
		this(new Content(List.of()), "");
	}

	public MergedItemsComponent(List<ItemStack> stacks) {
		this(new Content(stacks), "");
	}

	public MergedItemsComponent(List<ItemStack> stacks, String possible_merging_items) {
		this(new Content(stacks), possible_merging_items);
	}

//	public MergedItemsComponent(, Optional<RegistryEntryList<Item>> tag) {
//		this(new Content(List.of()), tag);
//	}

	public static MergedItemsComponent.Builder builder() {
		return new MergedItemsComponent.Builder(DEFAULT);
	}

	public ItemStack get(int index) {
		return (ItemStack) this.content.stacks.get(index);
	}

	public Stream<ItemStack> stream() {
		return this.content.stacks.stream().map(ItemStack::copy);
	}

	public Iterable<ItemStack> iterate() {
		return this.content.stacks;
	}

	public Iterable<ItemStack> iterateCopy() {
		return Lists.transform(this.content.stacks, ItemStack::copy);
	}

	public int size() {
		return this.content.stacks.size();
	}

	public boolean isEmpty() {
		return this.content.stacks.isEmpty();
	}

	public static class Builder {
		private MergedItemsComponent.Content content;
		private final String string;
		//		private Optional<RegistryEntryList<Item>> tag;

		public Builder(MergedItemsComponent base) {
			this.content = new MergedItemsComponent.Content(base.content.stacks);
			this.string = base.possible_merging_items;
//			this.tag = base.tag;
		}

		public MergedItemsComponent.Builder clear() {
			this.content.stacks.clear();
			return this;
		}

		private int addInternal(ItemStack stack) {
			if (!stack.isStackable()) {
				for (ItemStack itemStack : this.content.stacks) {
					if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
						return -1;
					}
				}
				return this.content.stacks.size();
			}
			return -1;
		}

		public void add(ItemStack stack) {
			if (!stack.isEmpty() && !stack.isStackable()) {
				int j = this.addInternal(stack);
				if (j != -1) {
					this.content.stacks.add(j, stack);
				}
			}
		}

		@Nullable
		public ItemStack removeLast() {
			if (this.content.stacks.isEmpty()) {
				return null;
			} else {
				return ((ItemStack) this.content.stacks.removeLast()).copy();
			}
		}

		public MergedItemsComponent build() {
			return new MergedItemsComponent(List.copyOf(this.content.stacks), this.string);
		}
	}

	public record Content(List<ItemStack> stacks) {
		public static final Content DEFAULT = new Content(List.of());
		public static final Codec<Content> CODEC = ItemStack.CODEC.listOf().xmap(Content::new, component -> component.stacks);
		public static final PacketCodec<RegistryByteBuf, Content> PACKET_CODEC = ItemStack.PACKET_CODEC
				.collect(PacketCodecs.toList())
				.xmap(Content::new, content -> content.stacks);

		public Content(List<ItemStack> stacks) {
			this.stacks = new ArrayList<ItemStack>(stacks);
		}
	}
}
