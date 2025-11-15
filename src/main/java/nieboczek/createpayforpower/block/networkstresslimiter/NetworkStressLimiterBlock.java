package nieboczek.createpayforpower.block.networkstresslimiter;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nieboczek.createpayforpower.block.ModBlockEntities;

// TODO: make this a double block
public class NetworkStressLimiterBlock extends DirectionalKineticBlock implements IBE<NetworkStressLimiterBlockEntity> {
    public static final BooleanProperty FRONT_HALF = BooleanProperty.create("front_half");

    public NetworkStressLimiterBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(FRONT_HALF, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FRONT_HALF);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(3, 0, 3, 13, 8, 13);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return state.getValue(FACING) == (state.getValue(FRONT_HALF) ? face.getOpposite() : face);
    }

    @Override
    public Class<NetworkStressLimiterBlockEntity> getBlockEntityClass() {
        return NetworkStressLimiterBlockEntity.class;
    }

    @Override
    public BlockEntityType<NetworkStressLimiterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.NETWORK_STRESS_LIMITER.get();
    }
}
