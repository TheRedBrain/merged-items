package com.github.theredbrain.mergeditems.registry;

import com.github.theredbrain.mergeditems.gui.tooltip.MergedItemsTooltipComponent;
import com.github.theredbrain.mergeditems.item.tooltip.MergedItemsTooltipData;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class ClientEventsRegistry {

	public static void initializeClientEvents() {
		TooltipComponentCallback.EVENT.register((data) -> {
			if (data instanceof MergedItemsTooltipData mergedItemsTooltipData) {
				return new MergedItemsTooltipComponent(mergedItemsTooltipData.contents());
			}
			return null;
		});
	}
}
