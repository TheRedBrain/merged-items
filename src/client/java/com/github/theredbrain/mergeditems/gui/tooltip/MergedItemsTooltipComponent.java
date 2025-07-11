package com.github.theredbrain.mergeditems.gui.tooltip;

import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MergedItemsTooltipComponent implements TooltipComponent {
	private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/background");
	private final MergedItemsComponent mergedItemsComponent;

	public MergedItemsTooltipComponent(MergedItemsComponent mergedItemsComponent) {
		this.mergedItemsComponent = mergedItemsComponent;
	}

	@Override
	public int getHeight() {
		return !this.mergedItemsComponent.isEmpty() ? this.getRowsHeight() + 14 : 0;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return !this.mergedItemsComponent.isEmpty() ? this.getColumnsWidth() : 0;
	}

	private int getColumnsWidth() {
		return this.getColumns() * 18 + 2;
	}

	private int getRowsHeight() {
		return this.getRows() * 18 + 2;
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
		if (!this.mergedItemsComponent.isEmpty()) {
			textRenderer.draw(Text.translatable("item.mergeditems.merged_items_component.tooltip.head_line"), (float) x, (float) y, Colors.LIGHT_GRAY, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		if (!this.mergedItemsComponent.isEmpty()) {
			int i = this.getColumns();
			int j = this.getRows();
			context.drawGuiTexture(BACKGROUND_TEXTURE, x + 4, y + 10, this.getColumnsWidth(), this.getRowsHeight());

			int k = 0;

			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					int n = x + m * 18 + 5;
					int o = y + l * 18 + 11;
					this.drawSlot(n, o, k++, context, textRenderer);
				}
			}
		}
	}

	private void drawSlot(int x, int y, int index, DrawContext context, TextRenderer textRenderer) {
		if (index >= this.mergedItemsComponent.size()) {
			this.draw(context, x, y, MergedItemsTooltipComponent.SlotSprite.SLOT);
		} else {
			ItemStack itemStack = this.mergedItemsComponent.get(index);
			this.draw(context, x, y, MergedItemsTooltipComponent.SlotSprite.SLOT);
			context.drawItem(itemStack, x + 1, y + 1, index);
			context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
		}
	}

	private void draw(DrawContext context, int x, int y, MergedItemsTooltipComponent.SlotSprite sprite) {
		context.drawGuiTexture(sprite.texture, x, y, 0, sprite.width, sprite.height);
	}

	private int getColumns() {
		return (int) Math.ceil(Math.sqrt(this.mergedItemsComponent.size()));
	}

	private int getRows() {
		return (int) Math.ceil(((double) this.mergedItemsComponent.size()) / (double) this.getColumns());
	}

	@Environment(EnvType.CLIENT)
	static enum SlotSprite {
//		BLOCKED_SLOT(Identifier.ofVanilla("container/bundle/blocked_slot"), 18, 20),
		SLOT(Identifier.ofVanilla("container/slot"), 18, 18);

		public final Identifier texture;
		public final int width;
		public final int height;

		private SlotSprite(final Identifier texture, final int width, final int height) {
			this.texture = texture;
			this.width = width;
			this.height = height;
		}
	}
}
