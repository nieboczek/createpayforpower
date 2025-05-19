package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import nieboczek.createpayforpower.CPFPLang;
import nieboczek.createpayforpower.ModGuiTexture;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PowerMeterScreen extends AbstractSimiContainerScreen<PowerMeterMenu> {
    private static final ModGuiTexture BG = ModGuiTexture.POWER_METER_BG;

    public PowerMeterScreen(PowerMeterMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void init() {  // FIXME: In Create Mod's code we have an additional + 2 (or + 4) on the height. Consider adding
        setWindowSize(BG.getWidth(), BG.getHeight() + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(0, 0); // +x = right
        super.init();

        PowerMeterBlockEntity entity = menu.contentHolder;

        int x = leftPos;
        int y = topPos;

        // Mode: Item/Redstone
        ScrollInput modeType = new SelectionScrollInput(x + 87, y + 28, 58, 16)
                .forOptions(CPFPLang.translatedOptions("gui.power_meter.mode", "item", "redstone"))
                .calling(idx -> entity.itemMode = idx == 0)
                .titled(CPFPLang.translate("gui.power_meter.mode").component());

        addRenderableWidget(modeType);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // FIXME: In Create Mod's code we have this if check here. Consider adding
//        if (this != minecraft.screen)
//            return; // stencil buffer does not cooperate with ponders gui fade out

        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + BG.getHeight() + 2;
        renderPlayerInventory(graphics, invX, invY);
        PowerMeterBlockEntity entity = menu.contentHolder;

        int x = leftPos;
        int y = topPos;

        // BG + Title
        BG.render(graphics, x, y);
        graphics.drawString(font, title, x + (BG.getWidth() - 8 - font.width(title)) / 2, y + 4, 0x592424, false);

        // Mode
        Component modePrefix = CPFPLang.translate("gui.power_meter.mode.colon").component();
        graphics.drawString(font, modePrefix, x + 38, y + 28, 0xffffff);

        // Redstone/Item
        Component modeStr;
        if (entity.itemMode) {
            modeStr = CPFPLang.translate("gui.power_meter.mode.item").component();
            ModGuiTexture.SLOT.render(graphics, x + 9, y + 22);
        } else {
            modeStr = CPFPLang.translate("gui.power_meter.mode.redstone").component();
        }

        graphics.drawString(font, modeStr, x + 87, y + 28, 0xffffff);
    }
}
