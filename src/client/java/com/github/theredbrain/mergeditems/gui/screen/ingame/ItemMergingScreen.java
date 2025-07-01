package com.github.theredbrain.mergeditems.gui.screen.ingame;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.MergedItemsClient;
import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import com.github.theredbrain.mergeditems.network.packet.MergeItemStacksPacket;
import com.github.theredbrain.mergeditems.network.packet.SplitMergedItemStacksPacket;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemMergingScreen extends HandledScreen<ItemMergingScreenHandler> {
	private static final Text MELD_BUTTON_LABEL_TEXT = Text.translatable("gui.item_merging.meld_button_label");
	private static final Text SPLIT_BUTTON_LABEL_TEXT = Text.translatable("gui.item_merging.split_button_label");
	private static final Text CAN_MERGE_LABEL_TEXT = Text.translatable("gui.item_merging.can_merge_label");
	public static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("textures/gui/sprites/container/slot.png");
	public static final Identifier MERGING_BACKGROUND_TEXTURE = MergedItems.identifier("textures/gui/container/merging_background.png");

	public ItemMergingScreen(ItemMergingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {

		this.backgroundHeight = 194;
		this.playerInventoryTitleY = this.backgroundHeight - 94;

		super.init();

		this.addDrawableChild(ButtonWidget.builder(MELD_BUTTON_LABEL_TEXT, button -> this.merge()).dimensions(this.x + 7, this.y + 48 + 28, 64, 20).build());

		this.addDrawableChild(ButtonWidget.builder(SPLIT_BUTTON_LABEL_TEXT, button -> this.split()).dimensions(this.x + 105, this.y + 48 + 28, 64, 20).build());

	}

	private void merge() {
		ClientPlayNetworking.send(new MergeItemStacksPacket());
	}

	private void split() {
		ClientPlayNetworking.send(new SplitMergedItemStacksPacket());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		ItemStack mergedStack = this.handler.slots.get(36).getStack().copy();
		ItemStack mergingContainerStack = this.handler.slots.get(37).getStack().copy();
		ItemStack splittingContainerStack = this.handler.slots.get(38).getStack().copy();
		ItemStack itemCostStack = this.handler.slots.get(40).getStack().copy();
		boolean playerIsCreative = false;

		if (this.client != null && this.client.player != null) {
			playerIsCreative = this.client.player.getAbilities().creativeMode;
		}
		int experienceLevel = this.handler.player.experienceLevel;

		if (this.isPointWithinBounds(7, 48 + 28, 64, 20, mouseX, mouseY) && !mergedStack.isEmpty() && !mergingContainerStack.isEmpty()) {
			List<Text> list = new ArrayList<>();
			list.add(Text.translatable("gui.item_merging.button_tooltip", mergedStack.getName(), mergingContainerStack.getName()));

			if (!playerIsCreative) {
				int experience_cost_amount = this.handler.merging_exp_cost.get();
				int item_cost_amount = this.handler.merging_item_cost.get();
				int existing_item_count = itemCostStack.isOf(Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get())) ? itemCostStack.getCount() : 0;

				if (item_cost_amount > 0 || experience_cost_amount > 0) {
					list.add(ScreenTexts.EMPTY);
				}

				if (item_cost_amount > 0) {
					MutableText mutableText = Text.literal(item_cost_amount + " ").append(Registries.ITEM.get(MergedItems.SERVER_CONFIG.merging_item_cost.get()).asItem().getName());
					list.add(mutableText.formatted(existing_item_count >= item_cost_amount ? Formatting.GRAY : Formatting.RED));
				}

				if (experience_cost_amount > 0) {
					MutableText mutableText2;

					if (experience_cost_amount == 1) {
						mutableText2 = Text.translatable("container.enchant.level.one");
					} else {
						mutableText2 = Text.translatable("container.enchant.level.many", new Object[]{experience_cost_amount});
					}
					list.add(mutableText2.formatted(experienceLevel >= experience_cost_amount ? Formatting.GRAY : Formatting.RED));
				}
			}
			context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
			return;
		}

		if (this.isPointWithinBounds(105, 48 + 28, 64, 20, mouseX, mouseY) && !splittingContainerStack.isEmpty()) {
			MergedItemsComponent mergedItemsComponent = splittingContainerStack.get(MergedItems.MERGED_ITEMS_COMPONENT_TYPE);

			if (mergedItemsComponent != null) {
				ItemStack splitStack = mergedItemsComponent.getLast();

				if (!splitStack.isEmpty()) {
					List<Text> list = new ArrayList<>();
					list.add(Text.translatable("gui.item_splitting.button_tooltip", splitStack.getName(), splittingContainerStack.getName()));

					if (!playerIsCreative) {
						int experience_cost_amount = this.handler.splitting_exp_cost.get();
						int item_cost_amount = this.handler.splitting_item_cost.get();
						int existing_item_count = itemCostStack.isOf(Registries.ITEM.get(MergedItems.SERVER_CONFIG.splitting_item_cost.get())) ? itemCostStack.getCount() : 0;

						if (item_cost_amount > 0 || experience_cost_amount > 0) {
							list.add(ScreenTexts.EMPTY);
						}

						if (item_cost_amount > 0) {
							MutableText mutableText = Text.literal(item_cost_amount + " ").append(Registries.ITEM.get(MergedItems.SERVER_CONFIG.splitting_item_cost.get()).asItem().getName());
							list.add(mutableText.formatted(existing_item_count >= item_cost_amount ? Formatting.GRAY : Formatting.RED));
						}

						if (experience_cost_amount > 0) {
							MutableText mutableText2;

							if (experience_cost_amount == 1) {
								mutableText2 = Text.translatable("container.enchant.level.one");
							} else {
								mutableText2 = Text.translatable("container.enchant.level.many", new Object[]{experience_cost_amount});
							}
							list.add(mutableText2.formatted(experienceLevel >= experience_cost_amount ? Formatting.GRAY : Formatting.RED));
						}
					}
					context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
				}
			}
			return;
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);

		// text is 8 px high

		MutableText text = Text.empty();
		for (String string : this.handler.getMergeableItemTags()) {
			if (!string.isEmpty()) {
				String[] stringArray = string.split(":");
				if (!stringArray[1].isEmpty()) {
					if (!text.equals(Text.empty())) {
						text.append(", ");
					}
					text.append(Text.translatable("tag.item." + (stringArray[0].isEmpty() ? "minecraft" : stringArray[0]) + "." + stringArray[1].replaceAll("/", ".")));
				}
			}
		}
		List<OrderedText> list = this.textRenderer.wrapLines(text, 160);
		if (!list.isEmpty()) {
			context.drawText(this.textRenderer, CAN_MERGE_LABEL_TEXT, 8, 18, 4210752, false);
			context.drawText(this.textRenderer, list.getFirst(), 8, 30, 4210752, false);
			if (list.size() > 1) {
				context.drawText(this.textRenderer, list.get(1), 8, 42, 4210752, false);
			}
		}
	}

	@Override
	public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int x = this.x;
		int y = this.y;
		int k;
		int m;

		boolean showInactiveSlots = MergedItemsClient.showInactiveInventorySlots();

		context.drawTexture(MERGING_BACKGROUND_TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

		for (k = 0; k < (showInactiveSlots ? 27 : this.handler.getActiveInventorySize()); ++k) {
			m = (k / 9);
			context.drawTexture(SLOT_TEXTURE, x + 7 + (k - (m * 9)) * 18, y + 83 + 28 + (m * 18), 0, 0, 18, 18, 18, 18);
		}
		for (k = 0; k < (showInactiveSlots ? 9 : this.handler.getActiveHotbarSize()); ++k) {
			context.drawTexture(SLOT_TEXTURE, x + 7 + k * 18, y + 141 + 28, 0, 0, 18, 18, 18, 18);
		}

		if (this.handler.getShowItemCostSlot()) {
			context.drawTexture(SLOT_TEXTURE, x + 79, y + 54, 0, 0, 18, 18, 18, 18);
		}
	}

}
