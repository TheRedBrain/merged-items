package com.github.theredbrain.mergeditems.network.packet;

import com.github.theredbrain.mergeditems.MergedItems;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record OpenItemMergingScreenPacket(
		int defaultItemCostAmount,
		double mergingItemCostMultiplier,
		double splittingItemCostMultiplier,
		int mergedItemsAmountMaximum,
		String title,
		List<String> list
) implements CustomPayload {
	public static final CustomPayload.Id<OpenItemMergingScreenPacket> PACKET_ID = new CustomPayload.Id<>(MergedItems.identifier("open_item_merging_screen"));
	public static final PacketCodec<RegistryByteBuf, OpenItemMergingScreenPacket> PACKET_CODEC = PacketCodec.of(OpenItemMergingScreenPacket::write, OpenItemMergingScreenPacket::new);

	public OpenItemMergingScreenPacket(PacketByteBuf buf) {
		this(
				buf.readInt(),
				buf.readDouble(),
				buf.readDouble(),
				buf.readInt(),
				buf.readString(),
				buf.readList(PacketCodecs.STRING)
		);
	}
	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeInt(this.defaultItemCostAmount);
		registryByteBuf.writeDouble(this.mergingItemCostMultiplier);
		registryByteBuf.writeDouble(this.splittingItemCostMultiplier);
		registryByteBuf.writeInt(this.mergedItemsAmountMaximum);
		registryByteBuf.writeString(this.title);
		registryByteBuf.writeCollection(this.list, PacketCodecs.STRING);
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}