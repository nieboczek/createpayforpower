package nieboczek.createpayforpower.block.powermeter;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PowerMeterInventory extends ItemStackHandler {
    public PowerMeterInventory() {
        super(1);
    }

    // TODO: don't consume the item, only copy it like the filters

//    @Override
//    public boolean isItemValid(int i, ItemStack stack) {
//        return switch (i) {
//            case 0 -> stack.isEmpty();
//            default -> super.isItemValid(i, stack);
//        };
//    }
}
