package nieboczek.createpayforpower.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nieboczek.createpayforpower.CreatePayForPower;

public class NetworkStressLimiterBlockEntity extends KineticBlockEntity {
    public NetworkStressLimiterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }


}
