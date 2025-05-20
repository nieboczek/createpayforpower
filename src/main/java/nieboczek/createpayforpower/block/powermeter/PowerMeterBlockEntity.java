package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nieboczek.createpayforpower.CPFPLang;
import nieboczek.createpayforpower.CreatePayForPower;
import org.jetbrains.annotations.Nullable;

public class PowerMeterBlockEntity extends KineticBlockEntity implements MenuProvider {
    // TODO: like the NetworkStressLimiter (NSL) we will have to create a separate network, so...
    // TODO: yeah kinda fucked, at least I can spend 8 hours on trying to make UI.

    // Note to self: 1 suh = 1su for 1 hour :: 1 ksuh = 1000su for 1 hour
    public PowerMeterInventory inventory;
    public boolean itemMode = true;
    public float sus = 0;  // I fucking promise this means "stress unit seconds" like "Ws" being "Watt seconds"
    public long ksuh = 0;  // kilo stress unit hours
    int increaseSusIn = 0;  // I really fucking need to name these variables better. LFMAO

    public PowerMeterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.inventory = new PowerMeterInventory();
    }

    @Override
    public void tick() {
        if (isVirtual()) return;

        increaseSusIn++;

        // 20 ticks being a second
        if (increaseSusIn >= 20) {
            // There is 1 imposter among us...
            // Go place your comment about this situation above this one.
            increaseSusIn = 0;
            sus += stress;

            // 3_600_000 being 1000su * 60 minutes * 60 seconds
            if (sus >= 3_600_000) {
                sus -= 3_600_000;
                ksuh++;
            }
        }
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
