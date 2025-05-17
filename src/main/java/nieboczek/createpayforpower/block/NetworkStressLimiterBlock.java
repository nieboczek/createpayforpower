package nieboczek.createpayforpower.block;

import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class NetworkStressLimiterBlock extends AbstractEncasedShaftBlock implements IBE<NetworkStressLimiterBlockEntity> {
    public NetworkStressLimiterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<NetworkStressLimiterBlockEntity> getBlockEntityClass() {
        return NetworkStressLimiterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends NetworkStressLimiterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.NETWORK_STRESS_LIMITER.get();
    }

//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
//        return GAUGE.get(state.getValue(FACING), state.getValue(AXIS_ALONG_FIRST_COORDINATE));
//    }
}
