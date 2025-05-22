package nieboczek.createpayforpower.block.powermeter;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import nieboczek.createpayforpower.ModPacket;

public record PowerMeterScreenPacket(boolean itemMode, int increaseBy, boolean hourMeasurement) implements ServerboundPacketPayload {
    // This looks overcomplicated, but it's basically a HashMap.
    public static final StreamCodec<ByteBuf, PowerMeterScreenPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PowerMeterScreenPacket::itemMode,
            ByteBufCodecs.INT, PowerMeterScreenPacket::increaseBy,
            ByteBufCodecs.BOOL, PowerMeterScreenPacket::hourMeasurement,
            PowerMeterScreenPacket::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player.containerMenu instanceof PowerMeterMenu menu) {
            menu.contentHolder.itemMode = itemMode;
            menu.contentHolder.increaseBy = increaseBy;
            menu.contentHolder.hourMeasurement = hourMeasurement;
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPacket.CONFIGURE_POWER_METER;
    }
}
