package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeShaper;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nieboczek.createpayforpower.block.ModBlockEntities;
import nieboczek.createpayforpower.mixin.GaugeShaperMixin;

public class PowerMeterBlock extends DirectionalAxisKineticBlock implements IBE<PowerMeterBlockEntity> {
    public static final GaugeShaper SHAPER = GaugeShaperMixin.callMake();

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public PowerMeterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide) return;

        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), 2);
            if (previouslyPowered) return;

            withBlockEntityDo(level, pos, entity -> {
                if (!entity.itemMode)
                    entity.increaseUnits();
            });
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AllItems.WRENCH.isIn(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof KineticBlock && hasShaftTowards(level, pos, state, hit.getDirection()))
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        return onBlockEntityUseItemOn(level, pos, entity -> {
            if (entity.itemMode && stack.is(entity.getItemFilter())) {
                // TODO: Put the item onto the block entity like the stock ticker
                stack.consume(1, null);
                entity.increaseUnits();
                player.swing(hand);
            } else {
                if (!entity.canOpen(player))
                    return ItemInteractionResult.SUCCESS;

                player.openMenu(entity, entity::sendToMenu);
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof ServerPlayer)
            withBlockEntityDo(level, pos, entity -> entity.owner = placer.getUUID());
    }

    @Override
    public Class<PowerMeterBlockEntity> getBlockEntityClass() {
        return PowerMeterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PowerMeterBlockEntity> getBlockEntityType() {
        return ModBlockEntities.POWER_METER.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPER.get(state.getValue(FACING), state.getValue(AXIS_ALONG_FIRST_COORDINATE));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        Block block = placedOnState.getBlock();

        if (block instanceof IRotate && ((IRotate) block).hasShaftTowards(world, placedOnPos, placedOnState, face)) {
            BlockState toPlace = defaultBlockState();
            Direction horizontalFacing = context.getHorizontalDirection();
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            boolean lookPositive = nearestLookingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (face.getAxis() == Direction.Axis.X) {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.NORTH : Direction.SOUTH)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, true);
            } else if (face.getAxis() == Direction.Axis.Y) {
                toPlace = toPlace.setValue(FACING, horizontalFacing.getOpposite())
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, horizontalFacing.getAxis() == Direction.Axis.X);
            } else {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.WEST : Direction.EAST)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, false);
            }

            return toPlace;
        }

        return super.getStateForPlacement(context);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    @Override
    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection()
                .getAxis() != Direction.Axis.X;
    }

//    public boolean shouldRenderHeadOnFace(Level world, BlockPos pos, BlockState state, Direction face) {
//        if (face.getAxis()
//                .isVertical())
//            return false;
//        if (face == state.getValue(FACING)
//                .getOpposite())
//            return false;
//        if (face.getAxis() == getRotationAxis(state))
//            return false;
//        if (getRotationAxis(state) == Direction.Axis.Y && face != state.getValue(FACING))
//            return false;
//        if (!Block.shouldRenderFace(state, world, pos, face, pos.relative(face)) && !(world instanceof WrappedLevel))
//            return false;
//        return true;
//    }
}
