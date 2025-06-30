package com.github.theredbrain.mergeditems.screen;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import com.github.theredbrain.slotcustomizationapi.api.SlotCustomization;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class ItemMergingScreenHandler extends ScreenHandler {

	private static final Identifier ITEM_COST_SLOT_BACKGROUND = MergedItems.identifier("item/empty_slot_item_cost");
	private final int defaultItemCostAmount;
	private final double mergingItemCostMultiplier;
	private final double splittingItemCostMultiplier;
	private final int mergedItemsAmountMaximum;
	private final List<String> mergeableItemTags;
	private final int activeHotbarSize;
	private final int activeInventorySize;
	private final boolean showItemCostSlot;

	Runnable contentsChangedListener = () -> {
	};
	public final Inventory inventory = new SimpleInventory(5) {
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
		this(syncId, playerInventory, data.defaultItemCostAmount, data.mergingItemCostMultiplier, data.splittingItemCostMultiplier, data.mergedItemsAmountMaximum, data.mergableItemTags);
	}

	public ItemMergingScreenHandler(int syncId, PlayerInventory playerInventory, int defaultItemCostAmount, double mergingItemCostMultiplier, double splittingItemCostMultiplier, int mergedItemsAmountMaximum, List<String> mergeableItemTags) {
		super(ScreenHandlerTypesRegistry.MERGING_ITEMS_SCREEN_HANDLER, syncId);
		this.defaultItemCostAmount = defaultItemCostAmount;
		this.mergingItemCostMultiplier = mergingItemCostMultiplier;
		this.splittingItemCostMultiplier = splittingItemCostMultiplier;
		this.mergedItemsAmountMaximum = mergedItemsAmountMaximum;
		this.mergeableItemTags = mergeableItemTags;
		this.activeHotbarSize = MergedItems.getActiveHotbarSize(playerInventory.player);
		this.activeInventorySize = MergedItems.getActiveInventorySize(playerInventory.player);
		this.showItemCostSlot = Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get()) != Items.AIR;
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
		this.addSlot(new Slot(inventory, 0, 8, 27 + 28));
		// 37
		this.addSlot(new Slot(inventory, 1, 54, 27 + 28));
		// 38
		this.addSlot(new Slot(inventory, 2, 106, 27 + 28));
		// 39
		this.addSlot(new Slot(inventory, 3, 152, 27 + 28));
		// 40
		this.addSlot(new Slot(inventory, 4, 80, 27 + 28) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.isOf(Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get()));
			}

			@Override
			public boolean isEnabled() {
				return ItemMergingScreenHandler.this.showItemCostSlot;
			}

			@Override
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ITEM_COST_SLOT_BACKGROUND);
			}
		});

		for (i = 0; i < 9; i++) {
			((SlotCustomization) this.slots.get(i)).slotcustomizationapi$setDisabledOverride(i >= this.activeHotbarSize);
		}
		for (i = 9; i < 36; i++) {
			((SlotCustomization) this.slots.get(i)).slotcustomizationapi$setDisabledOverride(i >= 9 + this.activeInventorySize);
		}
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

	public int getDefaultItemCostAmount() {
		return defaultItemCostAmount;
	}

	public double getMergingItemCostMultiplier() {
		return mergingItemCostMultiplier;
	}

	public double getSplittingItemCostMultiplier() {
		return splittingItemCostMultiplier;
	}

	public int getMergedItemsAmountMaximum() {
		return this.mergedItemsAmountMaximum;
	}

	public int getActiveHotbarSize() {
		return this.activeHotbarSize;
	}

	public int getActiveInventorySize() {
		return this.activeInventorySize;
	}

	public boolean getShowItemCostSlot() {
		return this.showItemCostSlot;
	}

	public record ItemMergingData(
			int defaultItemCostAmount,
			double mergingItemCostMultiplier,
			double splittingItemCostMultiplier,
			int mergedItemsAmountMaximum,
			List<String> mergableItemTags
	) {

		public static final PacketCodec<RegistryByteBuf, ItemMergingData> PACKET_CODEC = PacketCodec.of(ItemMergingData::write, ItemMergingData::new);

		public ItemMergingData(RegistryByteBuf registryByteBuf) {
			this(
					registryByteBuf.readInt(),
					registryByteBuf.readDouble(),
					registryByteBuf.readDouble(),
					registryByteBuf.readInt(),
					registryByteBuf.readList(PacketCodecs.STRING)
			);
		}

		private void write(RegistryByteBuf registryByteBuf) {
			registryByteBuf.writeInt(this.defaultItemCostAmount);
			registryByteBuf.writeDouble(this.mergingItemCostMultiplier);
			registryByteBuf.writeDouble(this.splittingItemCostMultiplier);
			registryByteBuf.writeInt(this.mergedItemsAmountMaximum);
			registryByteBuf.writeCollection(this.mergableItemTags, PacketCodecs.STRING);
		}
	}
}
