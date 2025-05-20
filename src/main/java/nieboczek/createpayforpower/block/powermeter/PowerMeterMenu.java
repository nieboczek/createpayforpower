package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.foundation.gui.menu.IClearableMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import nieboczek.createpayforpower.ModMenus;

public class PowerMeterMenu extends MenuBase<PowerMeterBlockEntity> implements IClearableMenu {
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

        addPlayerSlots(37, 161);
    }

    @Override
    protected void saveData(PowerMeterBlockEntity contentHolder) {}

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        Slot clickedSlot = getSlot(i);

        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();

        // TODO: probably add stuff here

        return ItemStack.EMPTY;
    }

    @Override
    public void clearContents() {
        contentHolder.inventory.setStackInSlot(0, ItemStack.EMPTY);
    }
}
