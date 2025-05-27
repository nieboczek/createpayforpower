package nieboczek.createpayforpower.block.powermeter;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import nieboczek.createpayforpower.ModPacket;

public record PowerMeterConfigurePacket(Option option, int increaseBy) implements ServerboundPacketPayload {
    // This looks overcomplicated, but it's basically a HashMap.
    public static final StreamCodec<ByteBuf, PowerMeterConfigurePacket> STREAM_CODEC = StreamCodec.composite(
            Option.STREAM_CODEC, PowerMeterConfigurePacket::option,
            ByteBufCodecs.INT, PowerMeterConfigurePacket::increaseBy,
            PowerMeterConfigurePacket::new
    );

    public static void executeOptionResult(PowerMeterMenu menu, Option option) {
        switch (option) {
            case DISABLE_ITEM_MODE -> menu.contentHolder.itemMode = false;
            case ENABLE_ITEM_MODE -> menu.contentHolder.itemMode = true;
            case DISABLE_HOUR_MEASUREMENT -> menu.contentHolder.hourMeasurement = false;
            case ENABLE_HOUR_MEASUREMENT -> menu.contentHolder.hourMeasurement = true;
            case LOCK -> menu.contentHolder.unlocked = false;
            case UNLOCK -> menu.contentHolder.unlocked = true;
            case RESET -> {
                menu.contentHolder.unitsLeft = 0;
                menu.contentHolder.checkUnits();
            }
        }
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player.containerMenu instanceof PowerMeterMenu menu) {
            if (option == Option.UPDATE_INCREASE_BY)
                menu.contentHolder.increaseBy = increaseBy;
            else
                executeOptionResult(menu, option);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPacket.CONFIGURE_POWER_METER;
    }

    public enum Option {
        DISABLE_ITEM_MODE,
        ENABLE_ITEM_MODE,
        DISABLE_HOUR_MEASUREMENT,
        ENABLE_HOUR_MEASUREMENT,
        LOCK,
        UNLOCK,
        RESET,
        UPDATE_INCREASE_BY;

        public static final StreamCodec<ByteBuf, Option> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Option.class);
    }
}
