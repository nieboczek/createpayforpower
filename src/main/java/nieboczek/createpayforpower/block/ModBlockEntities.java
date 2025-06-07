package nieboczek.createpayforpower.block;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import nieboczek.createpayforpower.CreatePayForPower;
import nieboczek.createpayforpower.block.networkstresslimiter.NetworkStressLimiterBlockEntity;
import nieboczek.createpayforpower.block.networkstresslimiter.NetworkStressLimiterRenderer;
import nieboczek.createpayforpower.block.powermeter.PowerMeterBlockEntity;
import nieboczek.createpayforpower.displaysource.ModDisplaySources;

public class ModBlockEntities {
    public static final BlockEntityEntry<PowerMeterBlockEntity> POWER_METER = CreatePayForPower.REGISTRATE
            .blockEntity("power_meter", PowerMeterBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .displaySource(ModDisplaySources.POWER_METER_STATUS)
            .validBlocks(ModBlocks.POWER_METER)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<NetworkStressLimiterBlockEntity> NETWORK_STRESS_LIMITER = CreatePayForPower.REGISTRATE
            .blockEntity("network_stress_limiter", NetworkStressLimiterBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT), false)
            .validBlocks(ModBlocks.NETWORK_STRESS_LIMITER)
            .renderer(() -> NetworkStressLimiterRenderer::new)
            .register();

    public static void register() {}
}
