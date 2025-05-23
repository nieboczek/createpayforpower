package nieboczek.createpayforpower;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nieboczek.createpayforpower.block.powermeter.PowerMeterConfigurePacket;

import java.util.Locale;

public enum ModPacket implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_POWER_METER(PowerMeterConfigurePacket.class, PowerMeterConfigurePacket.STREAM_CODEC),

    ; // This semicolon is also very important to me.

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPacket(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreatePayForPower.id(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreatePayForPower.MOD_ID, 1);
        for (ModPacket packet : ModPacket.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
