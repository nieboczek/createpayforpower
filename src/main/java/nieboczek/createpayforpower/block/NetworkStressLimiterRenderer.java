package nieboczek.createpayforpower.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class NetworkStressLimiterRenderer extends KineticBlockEntityRenderer<NetworkStressLimiterBlockEntity> {
    public NetworkStressLimiterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(NetworkStressLimiterBlockEntity entity) {
        return shaft(getRotationAxisOf(entity));
    }
}
