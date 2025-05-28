package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class PowerMeterRenderer extends KineticBlockEntityRenderer<PowerMeterBlockEntity> {
    public PowerMeterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(PowerMeterBlockEntity entity, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT, state);
    }
}
