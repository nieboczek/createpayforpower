package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import nieboczek.createpayforpower.CreatePayForPower;
import nieboczek.createpayforpower.ModMenus;

public class PowerMeterMenu extends MenuBase<PowerMeterBlockEntity> {
    public PowerMeterMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public PowerMeterMenu(MenuType<?> type, int id, Inventory inv, PowerMeterBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static PowerMeterMenu create(int id, Inventory inv, PowerMeterBlockEntity entity) {
        return new PowerMeterMenu(ModMenus.POWER_METER.get(), id, inv, entity);
    }

    @Override
    protected PowerMeterBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockEntity entity = level.getBlockEntity(extraData.readBlockPos());
        if (entity instanceof PowerMeterBlockEntity meterEntity) {
            meterEntity.readClient(extraData.readNbt(), extraData.registryAccess());
            return meterEntity;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(PowerMeterBlockEntity contentHolder) {}

    @Override
    protected void addSlots() {
        addSlot(new SlotItemHandler(contentHolder.inventory, 0, 11, 24));
        addPlayerSlots(11, 183);
    }

    @Override
    protected void saveData(PowerMeterBlockEntity contentHolder) {}

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId != 0) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (clickType == ClickType.THROW) return;

        ItemStack held = getCarried();
        int slot = slotId;

        if (clickType == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stack = contentHolder.inventory.getStackInSlot(slot).copy();
                stack.setCount(stack.getMaxStackSize());
                setCarried(stack);
            }
            return;
        }

        ItemStack inserted;
        if (held.isEmpty()) {
            inserted = ItemStack.EMPTY;
        } else {
            inserted = held.copy();
            inserted.setCount(1);
        }
        contentHolder.inventory.setStackInSlot(slot, inserted);
        getSlot(slot).setChanged();
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index == 0) {
            contentHolder.inventory.extractItem(index - 36, 1, false);
            getSlot(index).setChanged();
        } else {
            ItemStack stackToInsert = playerInventory.getItem(index - 1);
            ItemStack stack = contentHolder.inventory.getStackInSlot(0);

            if (stack.isEmpty()) {
                ItemStack copy = stackToInsert.copy();
                copy.setCount(1);
                contentHolder.inventory.insertItem(0, copy, false);
                getSlot(0).setChanged();
            }
        }
        return ItemStack.EMPTY;
    }
}
