package com.github.theredbrain.mergeditems.screen;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.registry.ScreenHandlerTypesRegistry;
import com.github.theredbrain.slotcustomizationapi.api.SlotCustomization;
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
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ItemMergingScreenHandler extends ScreenHandler {

	private static final Identifier ITEM_COST_SLOT_BACKGROUND = MergedItems.identifier("item/empty_slot_item_cost");
	private final int defaultItemCostAmount;
	private final double mergingItemCostMultiplier;
	private final double splittingItemCostMultiplier;
	private final int defaultExpCostAmount;
	private final double mergingExpCostMultiplier;
	private final double splittingExpCostMultiplier;
	private final int mergedItemsAmountMaximum;
	private final List<String> mergeableItemTags;
	private final int activeHotbarSize;
	private final int activeInventorySize;
	private final boolean showItemCostSlot;
	public final PlayerEntity player;

	public final Property merging_item_cost = Property.create();
	public final Property splitting_item_cost = Property.create();
	public final Property merging_exp_cost = Property.create();
	public final Property splitting_exp_cost = Property.create();

	public final Inventory inventory = new SimpleInventory(5) {
		@Override
		public int getMaxCountPerStack() {
			return 999;
		}

		@Override
		public void markDirty() {
			super.markDirty();
			ItemMergingScreenHandler.this.onContentChanged(this);
		}
	};

	public ItemMergingScreenHandler(int syncId, PlayerInventory playerInventory, ItemMergingData data) {
		this(syncId, playerInventory, data.defaultItemCostAmount, data.mergingItemCostMultiplier, data.splittingItemCostMultiplier, data.defaultExpCostAmount, data.mergingExpCostMultiplier, data.splittingExpCostMultiplier, data.mergedItemsAmountMaximum, data.mergableItemTags);
	}

	public ItemMergingScreenHandler(int syncId, PlayerInventory playerInventory, int defaultItemCostAmount, double mergingItemCostMultiplier, double splittingItemCostMultiplier, int defaultExpCostAmount, double mergingExpCostMultiplier, double splittingExpCostMultiplier, int mergedItemsAmountMaximum, List<String> mergeableItemTags) {
		super(ScreenHandlerTypesRegistry.MERGING_ITEMS_SCREEN_HANDLER, syncId);
		this.player = playerInventory.player;
		this.defaultItemCostAmount = defaultItemCostAmount;
		this.mergingItemCostMultiplier = mergingItemCostMultiplier;
		this.splittingItemCostMultiplier = splittingItemCostMultiplier;
		this.defaultExpCostAmount = defaultExpCostAmount;
		this.mergingExpCostMultiplier = mergingExpCostMultiplier;
		this.splittingExpCostMultiplier = splittingExpCostMultiplier;
		this.mergedItemsAmountMaximum = mergedItemsAmountMaximum;
		this.mergeableItemTags = mergeableItemTags;
		this.activeHotbarSize = MergedItems.getActiveHotbarSize(playerInventory.player);
		this.activeInventorySize = MergedItems.getActiveInventorySize(playerInventory.player);
		this.showItemCostSlot = (Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get()) != Items.AIR) || (Registries.ITEM.get(MergedItems.SERVER_CONFIG.splitting_item_cost.get()) != Items.AIR);
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
				return stack.isOf(Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get())) || stack.isOf(Registries.ITEM.get(MergedItems.SERVER_CONFIG.splitting_item_cost.get()));
			}

			@Override
			public boolean isEnabled() {
				return ItemMergingScreenHandler.this.showItemCostSlot;
			}
		});

		for (i = 0; i < 9; i++) {
			((SlotCustomization) this.slots.get(i)).slotcustomizationapi$setDisabledOverride(i >= this.activeHotbarSize);
		}
		for (i = 9; i < 36; i++) {
			((SlotCustomization) this.slots.get(i)).slotcustomizationapi$setDisabledOverride(i >= 9 + this.activeInventorySize);
		}
		((SlotCustomization) this.slots.get(36)).slotcustomizationapi$setSlotTooltipText(List.of(Text.translatable("gui.slot_tooltip.item_merging.merged_item_slot.line_1")));
		((SlotCustomization) this.slots.get(37)).slotcustomizationapi$setSlotTooltipText(List.of(Text.translatable("gui.slot_tooltip.item_merging.container_slot.line_1")));
		this.addProperty(this.merging_item_cost).set(0);
		this.addProperty(this.splitting_item_cost).set(0);
		this.addProperty(this.merging_exp_cost).set(0);
		this.addProperty(this.splitting_exp_cost).set(0);
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

	@Override
	public void onContentChanged(Inventory inventory) {
		if (inventory == this.inventory) {
			ItemStack mergedStack = inventory.getStack(0);
			ItemStack mergingContainerStack = inventory.getStack(1);
			ItemStack splittingContainerStack = inventory.getStack(2);

			if (!mergedStack.isEmpty() && !mergingContainerStack.isEmpty()) {
				MergedItemsComponent mergedItemsComponentOfContainerItemStack = mergingContainerStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);
				int itemCost = 0;
				int expCost = 0;

				if (mergedItemsComponentOfContainerItemStack != null) {
					int merged_item_cost;
					int merged_exp_cost;
					MergedItemsComponent mergedItemsComponentOfMergedItemStack = mergedStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

					if (mergedItemsComponentOfMergedItemStack != null) {
						merged_item_cost = mergedItemsComponentOfMergedItemStack.merging_item_cost() >= 0 ? mergedItemsComponentOfMergedItemStack.merging_item_cost() : this.getDefaultItemCostAmount();
						merged_exp_cost = mergedItemsComponentOfMergedItemStack.merging_exp_cost() >= 0 ? mergedItemsComponentOfMergedItemStack.merging_exp_cost() : this.getDefaultExpCostAmount();
					} else {
						merged_item_cost = this.getDefaultItemCostAmount();
						merged_exp_cost = this.getDefaultExpCostAmount();
					}

					int container_item_cost = mergedItemsComponentOfContainerItemStack.merging_item_cost() >= 0 ? mergedItemsComponentOfContainerItemStack.merging_item_cost() : this.getDefaultItemCostAmount();
					int container_exp_cost = mergedItemsComponentOfContainerItemStack.merging_exp_cost() >= 0 ? mergedItemsComponentOfContainerItemStack.merging_exp_cost() : this.getDefaultExpCostAmount();

					itemCost = (int) Math.floor((container_item_cost + merged_item_cost) * this.getMergingItemCostMultiplier());
					expCost = (int) Math.floor((container_exp_cost + merged_exp_cost) * this.getMergingExpCostMultiplier());
				}
				this.merging_item_cost.set(itemCost);
				this.merging_exp_cost.set(expCost);
			} else {
				this.merging_item_cost.set(0);
				this.merging_exp_cost.set(0);
			}
			if (!splittingContainerStack.isEmpty()) {
				int itemCost = 0;
				int expCost = 0;
				MergedItemsComponent mergedItemsComponent = splittingContainerStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);
				if (mergedItemsComponent != null) {
					itemCost = (int) Math.floor(mergedItemsComponent.merging_item_cost() * this.getSplittingItemCostMultiplier());
					expCost = (int) Math.floor(mergedItemsComponent.merging_exp_cost() * this.getSplittingExpCostMultiplier());
				}
				this.splitting_item_cost.set(itemCost);
				this.splitting_exp_cost.set(expCost);
			} else {
				this.splitting_item_cost.set(0);
				this.splitting_exp_cost.set(0);
			}
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

	public int getDefaultExpCostAmount() {
		return defaultExpCostAmount;
	}

	public double getMergingExpCostMultiplier() {
		return mergingExpCostMultiplier;
	}

	public double getSplittingExpCostMultiplier() {
		return splittingExpCostMultiplier;
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
			int defaultExpCostAmount,
			double mergingExpCostMultiplier,
			double splittingExpCostMultiplier,
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
			registryByteBuf.writeInt(this.defaultExpCostAmount);
			registryByteBuf.writeDouble(this.mergingExpCostMultiplier);
			registryByteBuf.writeDouble(this.splittingExpCostMultiplier);
			registryByteBuf.writeInt(this.mergedItemsAmountMaximum);
			registryByteBuf.writeCollection(this.mergableItemTags, PacketCodecs.STRING);
		}
	}
}
