package nieboczek.createpayforpower.block.networkstresslimiter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nieboczek.createpayforpower.block.directionalnetwork.DirectionalNetworkKineticBlockEntity;

public class NetworkStressLimiterBlockEntity extends DirectionalNetworkKineticBlockEntity {
    public NetworkStressLimiterBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }
}
