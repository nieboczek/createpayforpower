package nieboczek.createpayforpower.mixin;

import com.simibubi.create.content.kinetics.gauge.GaugeShaper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GaugeShaper.class)
public interface GaugeShaperMixin {
    @Invoker
    static GaugeShaper callMake() {
        return null;
    }
}
