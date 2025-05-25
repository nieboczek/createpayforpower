package nieboczek.createpayforpower;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CPFPLang extends Lang {
    public static LangBuilder builder() {
        return new LangBuilder(CreatePayForPower.MOD_ID);
    }

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate(prefix + "." + key).component());
        return result;
    }

    public static LangBuilder choice(boolean chooseLeft, String prefix, String left, String right) {
        if (chooseLeft)
            return builder().translate(prefix + "." + left);
        return builder().translate(prefix + "." + right);
    }

    public static LangBuilder translate(String key, Object... args) {
        return builder().translate(key, args);
    }

    public static LangBuilder gui(String key) {
        return translate("gui." + key);
    }

    public static LangBuilder block(String identifier) {
        return builder().add(Component.translatable("block.createpayforpower." + identifier));
    }
}
