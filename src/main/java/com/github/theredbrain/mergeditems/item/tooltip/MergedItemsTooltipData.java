package com.github.theredbrain.mergeditems.item.tooltip;

import com.github.theredbrain.mergeditems.component.type.MergedItemsComponent;
import net.minecraft.item.tooltip.TooltipData;

public record MergedItemsTooltipData(MergedItemsComponent contents) implements TooltipData {
}
