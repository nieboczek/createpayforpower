package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nieboczek.createpayforpower.block.ModBlockEntities;

public class PowerMeterBlock extends DirectionalAxisKineticBlock implements IBE<PowerMeterBlockEntity> {
    public PowerMeterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(level, pos, entity -> player.openMenu(entity, entity::sendToMenu));
        return InteractionResult.SUCCESS;
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
