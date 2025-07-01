package com.github.theredbrain.mergeditems.config;

import com.github.theredbrain.mergeditems.MergedItems;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ServerConfig extends Config {

	public ServerConfig() {
		super(MergedItems.identifier("server"));
	}

	@Comment("""
				When set to 'false', merging items will add up all their modifiers.
				
				When set to 'true', similar modifiers (same id, same slot, same operation) will be averaged.
				""")
	public ValidatedBoolean merging_averages_similar_modifiers = new ValidatedBoolean(true);
	public ValidatedInt default_item_cost_amount = new ValidatedInt(0, 64, 0);
	public ValidatedDouble default_merging_item_cost_multiplier = new ValidatedDouble(1.0);
	public ValidatedDouble default_splitting_item_cost_multiplier = new ValidatedDouble(1.0);
	public ValidatedInt default_exp_cost_amount = new ValidatedInt(0, Integer.MAX_VALUE, 0);
	public ValidatedDouble default_merging_exp_cost_multiplier = new ValidatedDouble(1.0);
	public ValidatedDouble default_splitting_exp_cost_multiplier = new ValidatedDouble(1.0);
	public ValidatedInt default_merged_items_amount_maximum = new ValidatedInt(1);
	public ValidatedIdentifier merging_item_cost = ValidatedIdentifier.ofRegistry(Identifier.of("minecraft:lapis_lazuli"), Registries.ITEM);
	public ValidatedIdentifier splitting_item_cost = ValidatedIdentifier.ofRegistry(Identifier.of("minecraft:lapis_lazuli"), Registries.ITEM);

}
