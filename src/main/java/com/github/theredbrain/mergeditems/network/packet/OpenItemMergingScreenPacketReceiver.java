package com.github.theredbrain.mergeditems.network.packet;

import com.github.theredbrain.mergeditems.screen.ItemMergingScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpenItemMergingScreenPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<OpenItemMergingScreenPacket> {

	@Override
	public void receive(OpenItemMergingScreenPacket payload, ServerPlayNetworking.Context context) {

		int maxMergedItemsAmount = payload.maxMergedItemsAmount();
		String title = payload.title();
		List<String> list = payload.list();

		context.player().openHandledScreen(new ExtendedScreenHandlerFactory<>() {
			@Override
			public ItemMergingScreenHandler.ItemMergingData getScreenOpeningData(ServerPlayerEntity player) {
				return new ItemMergingScreenHandler.ItemMergingData(maxMergedItemsAmount, list);
			}

			@Override
			public Text getDisplayName() {
				return Text.translatable(title);
			}

			@Nullable
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
				return new ItemMergingScreenHandler(syncId, playerInventory, maxMergedItemsAmount, list);
			}
		});
	}
}
