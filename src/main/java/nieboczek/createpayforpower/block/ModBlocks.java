package nieboczek.createpayforpower.block;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import nieboczek.createpayforpower.CreatePayForPower;
import nieboczek.createpayforpower.block.networkstresslimiter.NetworkStressLimiterBlock;
import nieboczek.createpayforpower.block.powermeter.PowerMeterBlock;

public class ModBlocks {
    static {
        CreatePayForPower.REGISTRATE.setCreativeTab(CreatePayForPower.CREATIVE_TAB);
    }

    public static final BlockEntry<PowerMeterBlock> POWER_METER =
            CreatePayForPower.REGISTRATE.block("power_meter", PowerMeterBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
//                    .item()
//                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<NetworkStressLimiterBlock> NETWORK_STRESS_LIMITER =
            CreatePayForPower.REGISTRATE.block("network_stress_limiter", NetworkStressLimiterBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
//                    .item()
//                    .transform(customItemModel())
                    .register();

//    public static final Block NETWORK_STRESS_LIMITER = null;
    public static void register() {}
}
