package nieboczek.createpayforpower.mixin;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.TorquePropagator;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//@Deprecated(since = "It's here because I tried to do stuff with TorquePropagator.networks, might still be useful so I'm leaving it here.")
@Mixin(TorquePropagator.class)
public interface TorquePropagatorMixin {
    @Accessor
    static Map<LevelAccessor, Map<Long, KineticNetwork>> getNetworks() {
        return null;
    }
}
