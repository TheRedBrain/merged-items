package com.github.theredbrain.mergeditems.gui.screen.ingame;

import com.github.theredbrain.mergeditems.MergedItems;
import com.github.theredbrain.mergeditems.MergedItemsClient;
import com.github.theredbrain.mergeditems.network.packet.MergeItemStacksPacket;
import com.github.theredbrain.mergeditems.network.packet.SplitMergedItemStacksPacket;
import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemMergingScreen extends HandledScreen<ItemMergingScreenHandler> {
	private static final Text MELD_BUTTON_LABEL_TEXT = Text.translatable("gui.item_merging.meld_button_label");
	private static final Text SPLIT_BUTTON_LABEL_TEXT = Text.translatable("gui.item_merging.split_button_label");
	private static final Text CAN_MERGE_LABEL_TEXT = Text.translatable("gui.item_merging.can_merge_label");
	public static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("textures/gui/sprites/container/slot.png");
	public static final Identifier MERGING_BACKGROUND_TEXTURE = MergedItems.identifier("textures/gui/container/merging_background.png");

	private ButtonWidget mergeButton;
	private ButtonWidget splitButton;

	private final int hotbarSize;
	private final int inventorySize;
	private final PlayerEntity playerEntity;
	private final List<String> meldableItemTags;

	public ItemMergingScreen(ItemMergingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.playerEntity = inventory.player;
		this.hotbarSize = MergedItems.getActiveHotbarSize(inventory.player);
		this.inventorySize = MergedItems.getActiveInventorySize(inventory.player);
		this.meldableItemTags = handler.getMergeableItemTags();
	}

	@Override
	protected void init() {

		this.backgroundHeight = 194;
		this.playerInventoryTitleY = this.backgroundHeight - 94;

		super.init();

		this.mergeButton = this.addDrawableChild(ButtonWidget.builder(MELD_BUTTON_LABEL_TEXT, button -> this.merge()).dimensions(this.x + 15, this.y + 48 + 28, 64, 20).build());

		this.splitButton = this.addDrawableChild(ButtonWidget.builder(SPLIT_BUTTON_LABEL_TEXT, button -> this.split()).dimensions(this.x + 97, this.y + 48 + 28, 64, 20).build());

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
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);

		// text is 8 px high

		MutableText text = Text.empty();
		for (String string : this.meldableItemTags) {
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
		boolean showInactiveSlots = MergedItemsClient.CLIENT_CONFIG.show_inactive_inventory_slots.get();

		context.drawTexture(MERGING_BACKGROUND_TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

		for (k = 0; k < (showInactiveSlots ? 27 : Math.min(this.inventorySize, 27)); ++k) {
			m = (k / 9);
			context.drawTexture(SLOT_TEXTURE, x + 7 + (k - (m * 9)) * 18, y + 83 + 28 + (m * 18), 0, 0, 18, 18, 18, 18);
		}
		for (k = 0; k < (showInactiveSlots ? 9 : Math.min(this.hotbarSize, 9)); ++k) {
			context.drawTexture(SLOT_TEXTURE, x + 7 + k * 18, y + 141 + 28, 0, 0, 18, 18, 18, 18);
		}

	}

}
