package nieboczek.createpayforpower.block;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import nieboczek.createpayforpower.CreatePayForPower;

public class ModBlockEntities {
    public static final BlockEntityEntry<PowerMeterBlockEntity> POWER_METER = CreatePayForPower.REGISTRATE
            .blockEntity("power_meter", PowerMeterBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT), false)
            .validBlocks(ModBlocks.POWER_METER)
            .renderer(() -> PowerMeterRenderer::new)
            .register();

    public static final BlockEntityEntry<NetworkStressLimiterBlockEntity> NETWORK_STRESS_LIMITER = CreatePayForPower.REGISTRATE
            .blockEntity("network_stress_limiter", NetworkStressLimiterBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT), false)
            .validBlocks(ModBlocks.NETWORK_STRESS_LIMITER)
            .renderer(() -> NetworkStressLimiterRenderer::new)
            .register();

    public static void register() {}
}
