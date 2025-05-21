package nieboczek.createpayforpower;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CPFPLang extends Lang {
    // Stole from https://github.com/Creators-of-Create/Create/blob/mc1.20.1/dev/src/main/java/com/simibubi/create/foundation/utility/CreateLang.java

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate(prefix + "." + key).component());
        return result;
    }

    public static LangBuilder builder() {
        return new LangBuilder(CreatePayForPower.MOD_ID);
    }

    public static LangBuilder choice(boolean chooseLeft, String prefix, String left, String right) {
        if (chooseLeft)
            return builder().translate(prefix + "." + left);
        return builder().translate(prefix + "." + right);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder block(String identifier) {
        return builder().add(Component.translatable("block.createpayforpower." + identifier));
    }
}
