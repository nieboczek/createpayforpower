package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.logistics.filter.FilterItem;
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
        if (slotId != 0 || clickType == ClickType.QUICK_MOVE) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (clickType == ClickType.THROW) return;
        ItemStack held = getCarried();
        ItemStack current = contentHolder.inventory.getStackInSlot(0);

        if (clickType == ClickType.CLONE) {
            if (player.isCreative() && held.isEmpty()) {
                ItemStack stack = current.copy();
                stack.setCount(stack.getMaxStackSize());
                setCarried(stack);
            }
            return;
        }

        ItemStack inserted;

        if (held.isEmpty()) {
            if (!current.isEmpty() && current.getItem() instanceof FilterItem)
                setCarried(current.copy());

            inserted = ItemStack.EMPTY;
        } else if (held.getItem() instanceof FilterItem) {
            inserted = held.split(1);
        } else {
            inserted = held.copy();
            inserted.setCount(1);
        }

        contentHolder.inventory.setStackInSlot(0, inserted);
        getSlot(0).setChanged();
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = contentHolder.inventory.getStackInSlot(0);

        if (index == 0) {
            if (stack.getItem() instanceof FilterItem) {
                ItemStack filterStack = contentHolder.inventory.extractItem(0, 1, false);
                getSlot(0).setChanged();
                playerInventory.add(filterStack);
                return filterStack;
            } else {
                contentHolder.inventory.extractItem(0, 1, false);
                getSlot(0).setChanged();
            }
        } else if (stack.isEmpty()) {
            ItemStack stackToInsert = playerInventory.getItem(index - 1);

            if (stackToInsert.getItem() instanceof FilterItem) {
                playerInventory.removeItem(stackToInsert);
                contentHolder.inventory.insertItem(0, stackToInsert, false);
                getSlot(0).setChanged();
            } else {
                ItemStack copy = stackToInsert.copy();
                copy.setCount(1);
                contentHolder.inventory.insertItem(0, copy, false);
                getSlot(0).setChanged();
            }
        }
        return ItemStack.EMPTY;
    }
}
