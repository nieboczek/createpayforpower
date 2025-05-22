package nieboczek.createpayforpower;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nieboczek.createpayforpower.block.ModBlockEntities;
import nieboczek.createpayforpower.block.ModBlocks;
import nieboczek.createpayforpower.displaysource.ModDisplaySources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreatePayForPower.MOD_ID)
public class CreatePayForPower {
    public static final String MOD_ID = "createpayforpower";
    public static final String MOD_NAME = "Create: Pay for Power";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TAB_REGISTER.register("group",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Create: Pay for Power"))
                    .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
                    .icon(ModBlocks.POWER_METER::asStack)
                    .build());

    public CreatePayForPower(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModDisplaySources.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModMenus.register();
        ModPacket.register();

        CREATIVE_MODE_TAB_REGISTER.register(modEventBus);
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
