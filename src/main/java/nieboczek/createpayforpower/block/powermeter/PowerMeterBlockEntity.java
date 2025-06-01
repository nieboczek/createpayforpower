package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import nieboczek.createpayforpower.CPFPLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PowerMeterBlockEntity extends SplitShaftBlockEntity implements MenuProvider {
    // TODO: visuals don't update properly when time/ksuh ran out
    // Note to self: 1 ksuh = 1000su for 1 hour
    public ItemStackHandler inventory;
    public boolean hourMeasurement = true;
    public boolean itemMode = true;
    public boolean unlocked = true;
    public int hoursUsed = 0;
    public int increaseBy = 1;
    public int unitsLeft = 0;
    public float sut = 0;  // stress unit ticks
    public long ksuh = 0;  // kilo stress unit hours
    public UUID owner;

    private int ticksPassed = 0;

    public PowerMeterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.inventory = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            compound.put("Inventory", inventory.serializeNBT(registries));
        }

        compound.putBoolean("hourMeasurement", hourMeasurement);
        compound.putBoolean("itemMode", itemMode);
        compound.putBoolean("unlocked", unlocked);
        compound.putInt("hoursUsed", hoursUsed);
        compound.putInt("increaseBy", increaseBy);
        compound.putInt("unitsLeft", unitsLeft);
        compound.putFloat("sut", sut);
        compound.putLong("ksuh", ksuh);
        compound.putInt("ticksPassed", ticksPassed);

        if (owner != null)
            compound.putUUID("owner", owner);

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        }

        hourMeasurement = compound.getBoolean("hourMeasurement");
        itemMode = compound.getBoolean("itemMode");
        unlocked = compound.getBoolean("unlocked");
        hoursUsed = compound.getInt("hoursUsed");
        increaseBy = compound.getInt("increaseBy");
        unitsLeft = compound.getInt("unitsLeft");
        sut = compound.getFloat("sut");
        ksuh = compound.getLong("ksuh");
        ticksPassed = compound.getInt("ticksPassed");

        if (compound.hasUUID("owner"))
            owner = compound.getUUID("owner");

        super.read(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (isVirtual()) return;
        if (unitsLeft <= 0) return;

        ticksPassed++;
        sut += overStressed ? 0 : stress;

        // 72_000 being 20 ticks * 60 seconds * 60 minutes (AKA an hour)
        if (ticksPassed >= 72_200) {
            ticksPassed -= 72_200;
            hoursUsed++;

            if (hourMeasurement) {
                unitsLeft -= 1;
                checkUnits();
            }
        }

        // 72_000_000 being 1000su/t * 20 ticks * 60 seconds * 60 minutes (AKA ksuh)
        if (sut >= 72_000_000) {
            sut -= 72_000_000;
            ksuh++;

            if (!hourMeasurement) {
                unitsLeft -= 1;
                checkUnits();
            }
        }
    }

    public boolean canOpen(Player player) {
        return unlocked || isOwner(player);
    }

    public boolean isOwner(Player player) {
        return owner.equals(player.getUUID());
    }

    public Item getItemFilter() {
        return inventory.getStackInSlot(0).getItem();
    }

    public void increaseUnits() {
        unitsLeft += increaseBy;
        attachKinetics();
    }

    public void checkUnits() {
        if (unitsLeft <= 0) {
            ticksPassed = 0;
            detachKinetics();
        }
    }

    public String getTimeLeft() {
        if (hourMeasurement)
            return getTimeLeftFromTicks((unitsLeft * 72_000L - ticksPassed));

        // don't divide by zero
        if (stress == 0) return "∞";

        float totalSutLeft = (unitsLeft * 72_000_000L) - sut;
        long ticksLeft = (long)(totalSutLeft / stress);
        return getTimeLeftFromTicks(ticksLeft);
    }

    private String getTimeLeftFromTicks(long ticksLeft) {
        if (ticksLeft <= 0) return "0s";

        long hours = ticksLeft / 72_000;
        long minutes = (ticksLeft % 72_000) / 1200;
        long seconds = (ticksLeft % 1200) / 20;

        if (hours > 0) {
            return hours + "h " + minutes + "min";
        } else if (minutes > 0) {
            return minutes + "min " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource() && (face == getSourceFacing() || unitsLeft > 0))
            return 1;
        return 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CPFPLang.block("power_meter").component();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return PowerMeterMenu.create(i, inv, this);
    }
}
