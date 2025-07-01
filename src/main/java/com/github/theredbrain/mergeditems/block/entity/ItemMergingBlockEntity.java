package com.github.theredbrain.mergeditems.block.entity;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.registry.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemMergingBlockEntity extends BlockEntity implements Nameable {
	private static final int DEFAULT_DEFAULT_ITEM_COST_AMOUNT = -1;
	private static final double DEFAULT_MERGING_ITEM_COST_MULTIPLIER = 1.0;
	private static final double DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER = 1.0;
	private static final int DEFAULT_DEFAULT_EXP_COST_AMOUNT = -1;
	private static final double DEFAULT_MERGING_EXP_COST_MULTIPLIER = 1.0;
	private static final double DEFAULT_SPLITTING_EXP_COST_MULTIPLIER = 1.0;
	private static final int DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM = -1;

	private int defaultItemCostAmount = DEFAULT_DEFAULT_ITEM_COST_AMOUNT;
	private double mergingItemCostMultiplier = DEFAULT_MERGING_ITEM_COST_MULTIPLIER;
	private double splittingItemCostMultiplier = DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER;
	private int defaultExpCostAmount = DEFAULT_DEFAULT_EXP_COST_AMOUNT;
	private double mergingExpCostMultiplier = DEFAULT_MERGING_EXP_COST_MULTIPLIER;
	private double splittingExpCostMultiplier = DEFAULT_SPLITTING_EXP_COST_MULTIPLIER;
	private int mergedItemsAmountMaximum = DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM;
	@Nullable
	private Text customName;
	private List<String> mergableItemTags = new ArrayList<>();

	public ItemMergingBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.ITEM_MERGING_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.writeNbt(nbt, registryLookup);

		if (this.hasCustomName()) {
			nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registryLookup));
		}

		if (this.defaultItemCostAmount != DEFAULT_DEFAULT_ITEM_COST_AMOUNT) {
			nbt.putInt("defaultItemCostAmount", this.defaultItemCostAmount);
		}

		if (this.mergingItemCostMultiplier != DEFAULT_MERGING_ITEM_COST_MULTIPLIER) {
			nbt.putDouble("mergingItemCostMultiplier", this.mergingItemCostMultiplier);
		}

		if (this.splittingItemCostMultiplier != DEFAULT_SPLITTING_ITEM_COST_MULTIPLIER) {
			nbt.putDouble("splittingItemCostMultiplier", this.splittingItemCostMultiplier);
		}

		if (this.defaultExpCostAmount != DEFAULT_DEFAULT_EXP_COST_AMOUNT) {
			nbt.putInt("defaultExpCostAmount", this.defaultExpCostAmount);
		}

		if (this.mergingExpCostMultiplier != DEFAULT_MERGING_EXP_COST_MULTIPLIER) {
			nbt.putDouble("mergingExpCostMultiplier", this.mergingExpCostMultiplier);
		}

		if (this.splittingExpCostMultiplier != DEFAULT_SPLITTING_EXP_COST_MULTIPLIER) {
			nbt.putDouble("splittingExpCostMultiplier", this.splittingExpCostMultiplier);
		}

		if (this.mergedItemsAmountMaximum != DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM) {
			nbt.putInt("mergedItemsAmountMaximum", this.mergedItemsAmountMaximum);
		}

		if (!this.mergableItemTags.isEmpty()) {

			nbt.putInt("listSize", mergableItemTags.size());
			for (int i = 0; i < this.mergableItemTags.size(); i++) {
				nbt.putString("listEntry_" + i, this.mergableItemTags.get(i));
			}

		}

	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.readNbt(nbt, registryLookup);

		if (nbt.contains("CustomName", 8)) {
			this.customName = tryParseCustomName(nbt.getString("CustomName"), registryLookup);
		}

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

		if (nbt.contains("defaultExpCostAmount")) {
			this.defaultExpCostAmount = nbt.getInt("defaultExpCostAmount");
		} else {
			this.defaultExpCostAmount = DEFAULT_DEFAULT_EXP_COST_AMOUNT;
		}

		if (nbt.contains("mergingExpCostMultiplier")) {
			this.mergingExpCostMultiplier = nbt.getDouble("mergingExpCostMultiplier");
		} else {
			this.mergingExpCostMultiplier = DEFAULT_MERGING_EXP_COST_MULTIPLIER;
		}

		if (nbt.contains("splittingExpCostMultiplier")) {
			this.splittingExpCostMultiplier = nbt.getDouble("splittingExpCostMultiplier");
		} else {
			this.splittingExpCostMultiplier = DEFAULT_SPLITTING_EXP_COST_MULTIPLIER;
		}

		if (nbt.contains("mergedItemsAmountMaximum")) {
			this.mergedItemsAmountMaximum = nbt.getInt("mergedItemsAmountMaximum");
		} else {
			this.mergedItemsAmountMaximum = DEFAULT_MERGED_ITEMS_AMOUNT_MAXIMUM;
		}

		int listSize = nbt.getInt("listSize");
		this.mergableItemTags = new ArrayList<>(List.of());
		for (int i = 0; i < listSize; i++) {
			if (nbt.contains("listEntry_" + i)) {
				this.mergableItemTags.add(nbt.getString("listEntry_" + i));
			}
		}

	}

	@Override
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

	public int getDefaultExpCostAmount() {
		return this.defaultExpCostAmount > -1 ? this.defaultExpCostAmount : MergedItems.SERVER_CONFIG.default_exp_cost_amount.get();
	}

	public double getMergingExpCostMultiplier() {
		return this.mergingExpCostMultiplier != 1 ? this.mergingExpCostMultiplier : MergedItems.SERVER_CONFIG.default_merging_exp_cost_multiplier.get();
	}

	public double getSplittingExpCostMultiplier() {
		return this.splittingExpCostMultiplier != 1 ? this.splittingExpCostMultiplier : MergedItems.SERVER_CONFIG.default_splitting_exp_cost_multiplier.get();
	}

	public int getMergedItemsAmountMaximum() {
		return this.mergedItemsAmountMaximum > -1 ? this.mergedItemsAmountMaximum : MergedItems.SERVER_CONFIG.default_merged_items_amount_maximum.get();
	}

	public void setMergedItemsAmountMaximum(int mergedItemsAmountMaximum) {
		this.mergedItemsAmountMaximum = mergedItemsAmountMaximum;
	}

	public List<String> getMergableItemTags() {
		return this.mergableItemTags;
	}

	public void setMergableItemTags(List<String> list) {
		this.mergableItemTags = list;
	}

	@Override
	public Text getName() {
		return (Text) (this.customName != null ? this.customName : Text.translatable("gui.item_merging.title"));
	}

	public void setCustomName(@Nullable Text customName) {
		this.customName = customName;
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		this.customName = (Text) components.get(DataComponentTypes.CUSTOM_NAME);
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("CustomName");
	}
}
