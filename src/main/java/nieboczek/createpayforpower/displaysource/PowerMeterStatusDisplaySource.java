package nieboczek.createpayforpower.displaysource;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nieboczek.createpayforpower.CPFPLang;
import nieboczek.createpayforpower.block.powermeter.PowerMeterBlockEntity;

public class PowerMeterStatusDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof PowerMeterBlockEntity entity))
            return EMPTY_LINE;

        int mode = context.sourceConfig().getInt("Mode");

        return Component.literal(switch (mode) {
            case 0 -> entity.getTimeLeft();
            case 1 -> {
                if (entity.hourMeasurement)
                    yield "∞";
                yield entity.unitsLeft + " ksuh";
            }
            case 2 -> entity.ksuh + " ksuh";
            case 3 -> entity.hoursUsed + " h";
            default -> "";
        });
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);

        // no idea what this does but :shrug:
        if (isFirstLine) return;

        builder.addSelectionScrollInput(0, 137, (si, l) -> si
                .forOptions(CPFPLang.translatedOptions("display_source.power_meter_status",
                        "time_left", "ksuh_left", "ksuh", "hours_used"
                ))
                // Less work for me LMAO
                .titled(Component.translatable("create.display_source.kinetic_stress.display")),
                "Mode");
    }
}
