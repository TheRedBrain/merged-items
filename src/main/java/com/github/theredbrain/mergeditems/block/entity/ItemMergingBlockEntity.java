package com.github.theredbrain.mergeditems.block.entity;

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
	private static final int DEFAULT_MAX_MERGED_ITEMS_AMOUNT = 1;

	private int maxMergedItemsAmount = DEFAULT_MAX_MERGED_ITEMS_AMOUNT;
	private String title = DEFAULT_TITLE;
	private List<String> list = new ArrayList<>();

	public ItemMergingBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.ITEM_MERGING_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		if (this.maxMergedItemsAmount != DEFAULT_MAX_MERGED_ITEMS_AMOUNT) {
			nbt.putInt("maxMergedItemsAmount", this.maxMergedItemsAmount);
		}

		if (!this.title.equals(DEFAULT_TITLE)) {
			nbt.putString("title", this.title);
		}

		if (!this.list.isEmpty()) {

			nbt.putInt("listSize", list.size());
			for (int i = 0; i < this.list.size(); i++) {
				nbt.putString("listEntry_" + i, this.list.get(i));
			}

		}

		super.writeNbt(nbt, registryLookup);

	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		if (nbt.contains("maxMergedItemsAmount")) {
			this.maxMergedItemsAmount = nbt.getInt("maxMergedItemsAmount");
		} else {
			this.maxMergedItemsAmount = DEFAULT_MAX_MERGED_ITEMS_AMOUNT;
		}
		this.title = nbt.getString("title");

		int listSize = nbt.getInt("listSize");
		this.list = new ArrayList<>(List.of());
		for (int i = 0; i < listSize; i++) {
			if (nbt.contains("listEntry_" + i)) {
				this.list.add(nbt.getString("listEntry_" + i));
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

	public int getMaxMergedItemsAmount() {
		return this.maxMergedItemsAmount;
	}

	public void setMaxMergedItemsAmount(int maxMergedItemsAmount) {
		this.maxMergedItemsAmount = maxMergedItemsAmount;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getList() {
		return this.list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
