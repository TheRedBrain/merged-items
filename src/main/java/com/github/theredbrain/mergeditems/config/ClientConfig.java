package com.github.theredbrain.mergeditems.config;

import com.github.theredbrain.mergeditems.MergedItems;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;

public class ClientConfig extends Config {

	public ClientConfig() {
		super(MergedItems.identifier("client"));
	}

	public ValidatedBoolean enable_texture_cycling_for_item_cost_slot = new ValidatedBoolean(false);

}

