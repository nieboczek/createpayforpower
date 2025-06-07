package nieboczek.createpayforpower.block.directionalnetwork;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

// TODO: hell
// Actually it might be still possible with Inject-INVOKE mixins but that's literally hell.
// Other useful mixin types: Redirect-FIELD
// How to implement / More mixin types: https://wiki.fabricmc.net/tutorial:mixin_examples
public abstract class DirectionalNetworkKineticBlock<T extends DirectionalNetworkKineticBlockEntity> extends DirectionalKineticBlock implements IBE<T> {
    public DirectionalNetworkKineticBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
//        withBlockEntityDo(level, pos, T::updateOutput);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }
}
