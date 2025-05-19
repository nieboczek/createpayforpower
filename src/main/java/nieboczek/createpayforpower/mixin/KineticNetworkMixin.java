package nieboczek.createpayforpower.mixin;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import nieboczek.createpayforpower.block.networkstresslimiter.NetworkStressLimiterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KineticNetwork.class)
public abstract class KineticNetworkMixin {
    @Shadow public Map<KineticBlockEntity, Float> sources;
    @Shadow public Map<KineticBlockEntity, Float> members;
    @Shadow protected abstract void updateFromNetwork(KineticBlockEntity be);

    @Inject(method = "add", at = @At("HEAD"))
    public void add(KineticBlockEntity entity, CallbackInfo ci) {
        if (entity instanceof NetworkStressLimiterBlockEntity limiterEntity) {
            if (members.containsKey(entity)) {
                return;
            }

            sources.put(entity, limiterEntity.maxStress);
            members.put(entity, 0f);
            updateFromNetwork(entity);
            entity.networkDirty = true;
        }
    }
}
