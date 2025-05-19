package nieboczek.createpayforpower.block.networkstresslimiter;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nieboczek.createpayforpower.block.ModBlockEntities;

public class NetworkStressLimiterBlock extends DirectionalKineticBlock implements IBE<NetworkStressLimiterBlockEntity> {
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

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        NetworkStressLimiterBlockEntity entity = getBlockEntity(level, pos);
        entity.updateOutput();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
//        return GAUGE.get(state.getValue(FACING), state.getValue(AXIS_ALONG_FIRST_COORDINATE));
//    }
}
