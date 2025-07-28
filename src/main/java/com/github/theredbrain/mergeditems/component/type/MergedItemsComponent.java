package com.github.theredbrain.mergeditems.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record MergedItemsComponent(
		Content content/*,
		Optional<RegistryEntryList<Item>> tag  TODO replace string in 1.21.4*/,
		String possible_merging_items,
//		int merged_items_max_amount,
		byte merge_content_flags,
		int merging_item_cost,
		int merging_exp_cost
) {
	public static final byte SPELL_MERGING_ENABLED_FLAG = 1;
	public static final byte SPELL_MERGING_ALLOWED_FLAG = 2;
//	public static final byte EQUIPMENT_SET_MERGING_ENABLED_FLAG = 3;
//	public static final byte EQUIPMENT_SET_MERGING_ALLOWED_FLAG = 4;
	public static final MergedItemsComponent DEFAULT = new MergedItemsComponent();
	public static final Codec<MergedItemsComponent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							MergedItemsComponent.Content.CODEC.fieldOf("content").forGetter(component -> component.content),
//							RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("tag").forGetter(component -> component.tag),
							Codec.STRING.optionalFieldOf("possible_merging_items", "").forGetter(component -> component.possible_merging_items),
							Codec.BYTE.optionalFieldOf("merge_content_flags", (byte)0).forGetter(component -> component.merge_content_flags),
							Codec.INT.optionalFieldOf("merging_item_cost", -1).forGetter(component -> component.merging_item_cost),
							Codec.INT.optionalFieldOf("merging_exp_cost", -1).forGetter(component -> component.merging_item_cost)
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
			PacketCodecs.BYTE,
			component -> component.merge_content_flags,
			PacketCodecs.INTEGER,
			component -> component.merging_item_cost,
			PacketCodecs.INTEGER,
			component -> component.merging_exp_cost,
			MergedItemsComponent::new
	);
//	final List<ItemStack> stacks;

	public MergedItemsComponent() {
		this(new Content(List.of()), "", (byte) 0, -1, -1);
	}

	public MergedItemsComponent(
			List<ItemStack> stacks,
			String possible_merging_items,
			byte merge_content_flags,
			int merging_item_cost,
			int merging_exp_cost
	) {
		this(
				new Content(stacks),
				possible_merging_items,
				merge_content_flags,
				merging_item_cost,
				merging_exp_cost
		);
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

	public ItemStack getLast() {
		if (this.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			return (ItemStack) this.content.stacks.getLast();
		}
	}

	public boolean isSpellMergingEnabled() {
		return (this.merge_content_flags & SPELL_MERGING_ENABLED_FLAG) != 0;
	}

	public boolean isSpellMergingAllowed() {
		return (this.merge_content_flags & SPELL_MERGING_ALLOWED_FLAG) != 0;
	}

//	public boolean isEquipmentSetMergingEnabled() {
//		return (this.merge_content_flags & EQUIPMENT_SET_MERGING_ENABLED_FLAG) != 0;
//	}
//
//	public boolean isEquipmentSetMergingAllowed() {
//		return (this.merge_content_flags & EQUIPMENT_SET_MERGING_ALLOWED_FLAG) != 0;
//	}

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
		private byte merge_content_flags;
		private int merging_item_cost;
		private int merging_exp_cost;

		public Builder(MergedItemsComponent base) {
			this.content = new MergedItemsComponent.Content(base.content.stacks);
			this.string = base.possible_merging_items;
//			this.tag = base.tag;
			this.merge_content_flags = base.merge_content_flags;
			this.merging_item_cost = base.merging_item_cost;
			this.merging_exp_cost = base.merging_exp_cost;
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

		public MergedItemsComponent.Builder withMergedContent(
				boolean enableSpellMerging,
				boolean allowSpellMerging/*,
				boolean enableEquipmentSetMerging,
				boolean allowEquipmentSetMerging*/
		) {
			byte flags = (byte) 0;
			flags = enableSpellMerging ? (byte) (flags | SPELL_MERGING_ENABLED_FLAG) : flags;
			flags = allowSpellMerging ? (byte) (flags | SPELL_MERGING_ALLOWED_FLAG) : flags;
//			flags = enableEquipmentSetMerging ? (byte) (flags | EQUIPMENT_SET_MERGING_ENABLED_FLAG) : flags;
//			flags = allowEquipmentSetMerging ? (byte) (flags | EQUIPMENT_SET_MERGING_ALLOWED_FLAG) : flags;
			this.merge_content_flags = flags;
			return this;
		}

		public MergedItemsComponent.Builder withMergingItemCost(int newCost) {
			this.merging_item_cost = newCost;
			return this;
		}

		public MergedItemsComponent.Builder withMergingExpCost(int newCost) {
			this.merging_exp_cost = newCost;
			return this;
		}

		public ItemStack removeLast() {
			if (this.content.stacks.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				return ((ItemStack) this.content.stacks.removeLast()).copy();
			}
		}

		public MergedItemsComponent build() {
			return new MergedItemsComponent(List.copyOf(this.content.stacks), this.string, this.merge_content_flags, this.merging_item_cost, this.merging_exp_cost);
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
