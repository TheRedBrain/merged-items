package com.github.theredbrain.mergeditems.screen;

import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class ItemMergingScreenHandler extends ScreenHandler {

	private final PlayerInventory playerInventory;
	private final World world;
	private final int maxMergedItemsAmount;
	private final List<String> mergeableItemTags;
	Runnable contentsChangedListener = () -> {
	};
	public final Inventory inventory = new SimpleInventory(4) {
		@Override
		public int getMaxCountPerStack() {
			return 999;
		}

		@Override
		public void markDirty() {
			super.markDirty();
			ItemMergingScreenHandler.this.onContentChanged(this);
			ItemMergingScreenHandler.this.contentsChangedListener.run();
		}
	};

	public ItemMergingScreenHandler(int syncId, PlayerInventory playerInventory, ItemMergingData data) {
		this(syncId, playerInventory, data.maxMergedItemsAmount, data.mergableItemTags);
	}

	public ItemMergingScreenHandler(int syncId, PlayerInventory playerInventory, int maxMergedItemsAmount, List<String> mergeableItemTags) {
		super(ScreenHandlerTypesRegistry.MERGING_ITEMS_SCREEN_HANDLER, syncId);
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.maxMergedItemsAmount = maxMergedItemsAmount;
		this.mergeableItemTags = mergeableItemTags;
		int i;
		// hotbar 0 - 8
		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + 28));
		}
		// main inventory 9 - 35
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + 28 + i * 18));
			}
		}
		// 36
		this.addSlot(new Slot(inventory, 0, 16, 27 + 28));
		// 37
		this.addSlot(new Slot(inventory, 1, 62, 27 + 28));
		// 38
		this.addSlot(new Slot(inventory, 2, 98, 27 + 28));
		// 39
		this.addSlot(new Slot(inventory, 3, 144, 27 + 28));

	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		if (player instanceof ServerPlayerEntity) {
			this.dropInventory(player, this.inventory);
		}
	}

	public List<String> getMergeableItemTags() {
		return this.mergeableItemTags;
	}

	public int getMaxMergedItemsAmount() {
		return this.maxMergedItemsAmount;
	}

	public record ItemMergingData(
			int maxMergedItemsAmount,
			List<String> mergableItemTags
	) {

		public static final PacketCodec<RegistryByteBuf, ItemMergingData> PACKET_CODEC = PacketCodec.of(ItemMergingData::write, ItemMergingData::new);

		public ItemMergingData(RegistryByteBuf registryByteBuf) {
			this(
					registryByteBuf.readInt(),
					registryByteBuf.readList(PacketCodecs.STRING)
			);
		}

		private void write(RegistryByteBuf registryByteBuf) {
			registryByteBuf.writeInt(this.maxMergedItemsAmount);
			registryByteBuf.writeCollection(this.mergableItemTags, PacketCodecs.STRING);
		}
	}
}
