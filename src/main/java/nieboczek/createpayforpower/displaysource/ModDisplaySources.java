package nieboczek.createpayforpower.displaysource;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import nieboczek.createpayforpower.CreatePayForPower;

public class ModDisplaySources {
    public static RegistryEntry<DisplaySource, PowerMeterStatusDisplaySource> POWER_METER_STATUS = CreatePayForPower.REGISTRATE
            .displaySource("power_meter_status", PowerMeterStatusDisplaySource::new)
            .register();

    public static void register() {}
}
