package nieboczek.createpayforpower;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import nieboczek.createpayforpower.block.powermeter.PowerMeterMenu;
import nieboczek.createpayforpower.block.powermeter.PowerMeterScreen;

public class ModMenus {
    public static final MenuEntry<PowerMeterMenu> POWER_METER = register("power_meter", PowerMeterMenu::new, () -> PowerMeterScreen::new);

    private static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> MenuEntry<M> register(
            String name, MenuBuilder.ForgeMenuFactory<M> factory, NonNullSupplier<MenuBuilder.ScreenFactory<M, S>> screenFactory) {
        return CreatePayForPower.REGISTRATE
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}
}
