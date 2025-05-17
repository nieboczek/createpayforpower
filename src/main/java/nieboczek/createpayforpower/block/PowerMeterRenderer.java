package nieboczek.createpayforpower.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class PowerMeterRenderer extends KineticBlockEntityRenderer<PowerMeterBlockEntity> {
    public PowerMeterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(PowerMeterBlockEntity entity) {
        return shaft(getRotationAxisOf(entity));
    }
}
