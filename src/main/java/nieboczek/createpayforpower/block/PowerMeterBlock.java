package nieboczek.createpayforpower.block;

import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PowerMeterBlock extends AbstractEncasedShaftBlock implements IBE<PowerMeterBlockEntity> {
    public PowerMeterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<PowerMeterBlockEntity> getBlockEntityClass() {
        return PowerMeterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PowerMeterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.POWER_METER.get();
    }
}
