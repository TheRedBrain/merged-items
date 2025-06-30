package com.github.theredbrain.mergeditems.block.entity;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.registry.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ItemMergingBlockEntity extends BlockEntity {
	private static final String DEFAULT_TITLE = "gui.item_merging.title";
	private static final int DEFAULT_DEFAULT_ITEM_COST_AMOUNT = 0;
	private static final double DEFAULT_MERGING_ITEM_COST_MULTIPLIER = 1.0;
	private static final double DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER = 1.0;
	private static final int DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM = -1;

	private int defaultItemCostAmount = DEFAULT_DEFAULT_ITEM_COST_AMOUNT;
	private double mergingItemCostMultiplier = DEFAULT_MERGING_ITEM_COST_MULTIPLIER;
	private double splittingItemCostMultiplier = DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER;
	private int mergedItemsAmountMaximum = DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM;
	private String title = DEFAULT_TITLE;
	private List<String> mergableItemTags = new ArrayList<>();

	public ItemMergingBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.ITEM_MERGING_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		if (this.defaultItemCostAmount != DEFAULT_DEFAULT_ITEM_COST_AMOUNT) {
			nbt.putInt("defaultItemCostAmount", this.defaultItemCostAmount);
		}

		if (this.mergingItemCostMultiplier != DEFAULT_MERGING_ITEM_COST_MULTIPLIER) {
			nbt.putDouble("mergingItemCostMultiplier", this.mergingItemCostMultiplier);
		}

		if (this.splittingItemCostMultiplier != DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER) {
			nbt.putDouble("splittingItemCostMultiplier", this.splittingItemCostMultiplier);
		}

		if (this.mergedItemsAmountMaximum != DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM) {
			nbt.putInt("mergedItemsAmountMaximum", this.mergedItemsAmountMaximum);
		}

		if (!this.title.equals(DEFAULT_TITLE)) {
			nbt.putString("title", this.title);
		}

		if (!this.mergableItemTags.isEmpty()) {

			nbt.putInt("listSize", mergableItemTags.size());
			for (int i = 0; i < this.mergableItemTags.size(); i++) {
				nbt.putString("listEntry_" + i, this.mergableItemTags.get(i));
			}

		}

		super.writeNbt(nbt, registryLookup);

	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		if (nbt.contains("defaultItemCostAmount")) {
			this.defaultItemCostAmount = nbt.getInt("defaultItemCostAmount");
		} else {
			this.defaultItemCostAmount = DEFAULT_DEFAULT_ITEM_COST_AMOUNT;
		}

		if (nbt.contains("mergingItemCostMultiplier")) {
			this.mergingItemCostMultiplier = nbt.getDouble("mergingItemCostMultiplier");
		} else {
			this.mergingItemCostMultiplier = DEFAULT_MERGING_ITEM_COST_MULTIPLIER;
		}

		if (nbt.contains("splittingItemCostMultiplier")) {
			this.splittingItemCostMultiplier = nbt.getDouble("splittingItemCostMultiplier");
		} else {
			this.splittingItemCostMultiplier = DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER;
		}

		if (nbt.contains("mergedItemsAmountMaximum")) {
			this.mergedItemsAmountMaximum = nbt.getInt("mergedItemsAmountMaximum");
		} else {
			this.mergedItemsAmountMaximum = DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM;
		}

		if (nbt.contains("title")) {
			this.title = nbt.getString("title");
		} else {
			this.title = DEFAULT_TITLE;
		}

		int listSize = nbt.getInt("listSize");
		this.mergableItemTags = new ArrayList<>(List.of());
		for (int i = 0; i < listSize; i++) {
			if (nbt.contains("listEntry_" + i)) {
				this.mergableItemTags.add(nbt.getString("listEntry_" + i));
			}
		}

		super.readNbt(nbt, registryLookup);

	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createComponentlessNbt(registryLookup);
	}

	public int getDefaultItemCostAmount() {
		return this.defaultItemCostAmount > -1 ? this.defaultItemCostAmount : MergedItems.SERVER_CONFIG.default_item_cost_amount.get();
	}

	public double getMergingItemCostMultiplier() {
		return this.mergingItemCostMultiplier != 1 ? this.mergingItemCostMultiplier : MergedItems.SERVER_CONFIG.default_merging_item_cost_multiplier.get();
	}

	public double getSplittingItemCostMultiplier() {
		return this.splittingItemCostMultiplier != 1 ? this.splittingItemCostMultiplier : MergedItems.SERVER_CONFIG.default_splitting_item_cost_multiplier.get();
	}

	public int getMergedItemsAmountMaximum() {
		return this.mergedItemsAmountMaximum > -1 ? this.mergedItemsAmountMaximum : MergedItems.SERVER_CONFIG.default_merged_items_amount_maximum.get();
	}

	public void setMergedItemsAmountMaximum(int mergedItemsAmountMaximum) {
		this.mergedItemsAmountMaximum = mergedItemsAmountMaximum;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getMergableItemTags() {
		return this.mergableItemTags;
	}

	public void setMergableItemTags(List<String> list) {
		this.mergableItemTags = list;
	}

}
