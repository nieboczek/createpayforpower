package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import nieboczek.createpayforpower.CPFPLang;
import nieboczek.createpayforpower.CreatePayForPower;
import org.jetbrains.annotations.Nullable;

public class PowerMeterBlockEntity extends KineticBlockEntity implements MenuProvider {
    // TODO: Like the Network Stress Limiter we will have to create a separate kinetic network, kinda fucked.

    // Note to self: 1 ksuh = 1000su for 1 hour
    public ItemStackHandler inventory;
    public boolean hourMeasurement = true;
    public boolean itemMode = true;
    public int hoursUsed = 0;
    public int increaseBy = 1;  // increase `thingsLeft` on item get / receive redstone
    public int thingsLeft = 0;  // ksuh/hours left
    public float sus = 0;  // I fucking promise this means "stress unit seconds" like "Ws" being "Watt seconds"
    public long ksuh = 0;  // kilo stress unit hours
    int ticksPassed = 0;

    public PowerMeterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.inventory = new ItemStackHandler(1);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            compound.put("Inventory", inventory.serializeNBT(registries));
        }

        compound.put("hourMeasurement", ByteTag.valueOf(hourMeasurement));
        compound.put("itemMode", ByteTag.valueOf(itemMode));
        compound.put("increaseBy", IntTag.valueOf(increaseBy));
        compound.put("thingsLeft", IntTag.valueOf(thingsLeft));
        compound.put("ksuh", LongTag.valueOf(ksuh));
        compound.put("sus", FloatTag.valueOf(sus));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        }

        hourMeasurement = compound.getBoolean("hourMeasurement");
        itemMode = compound.getBoolean("itemMode");
        increaseBy = compound.getInt("increaseBy");
        thingsLeft = compound.getInt("thingsLeft");
        ksuh = compound.getLong("ksuh");
        sus = compound.getFloat("sus");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        if (isVirtual()) return;
        if (thingsLeft <= 0) return;

        ticksPassed++;

        // 72_000 being 20 ticks * 60 seconds * 60 minutes (AKA an hour)
        if (ticksPassed >= 72_200) {
            ticksPassed -= 72_200;
            hoursUsed++;

            if (hourMeasurement) {
                thingsLeft -= 1;
                checkStatus();
            }
        }

        // 20 ticks being a second
        if (ticksPassed % 20 == 0) {
            // There is 1 imposter among us...
            // Go place your comment about this situation above this one.
            sus += stress;

            // 3_600_000 being 1000su * 60 minutes * 60 seconds
            if (sus >= 3_600_000) {
                sus -= 3_600_000;
                ksuh++;

                if (!hourMeasurement) {
                    thingsLeft -= 1;
                    checkStatus();
                }
            }
        }
    }

    private void checkStatus() {
        if (thingsLeft <= 0) {
            CreatePayForPower.LOGGER.warn("PowerMeter has ran out of things!");
        }
    }

    public String getTimeLeft() {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return CPFPLang.block("power_meter").component();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return PowerMeterMenu.create(i, inv, this);
    }
}
