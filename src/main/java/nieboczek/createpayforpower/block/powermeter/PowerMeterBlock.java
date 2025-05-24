package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nieboczek.createpayforpower.CreatePayForPower;
import nieboczek.createpayforpower.block.ModBlockEntities;

public class PowerMeterBlock extends DirectionalAxisKineticBlock implements IBE<PowerMeterBlockEntity> {
    public PowerMeterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        return onBlockEntityUse(level, pos, entity -> {
            if (!entity.canOpen(player))
                return InteractionResult.SUCCESS;

            player.openMenu(entity, entity::sendToMenu);
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        return onBlockEntityUseItemOn(level, pos, entity -> {
            if (entity.itemMode) {
                if (stack.is(entity.getItemFilter())) {
                    stack.consume(1, null);
                    entity.increaseThings();
                    player.swing(hand);  // Create doesn't use this at all on the depot, but it still works?
                    return ItemInteractionResult.SUCCESS;
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof ServerPlayer) {
            withBlockEntityDo(level, pos, entity -> {
                CreatePayForPower.LOGGER.info("Set owner of power meter to {}", placer.getName());
                entity.owner = placer.getUUID();
            });
        }
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
