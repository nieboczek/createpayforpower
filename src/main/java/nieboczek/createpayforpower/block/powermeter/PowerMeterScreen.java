package nieboczek.createpayforpower.block.powermeter;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.createmod.catnip.platform.CatnipServices;
import nieboczek.createpayforpower.CPFPLang;
import nieboczek.createpayforpower.ModGuiTexture;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PowerMeterScreen extends AbstractSimiContainerScreen<PowerMeterMenu> {
    // TODO: Save the filter item!!
    private static final ModGuiTexture BG = ModGuiTexture.POWER_METER_BG;

    public PowerMeterScreen(PowerMeterMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void init() {  // FIXME: In Create Mod's code we have an additional + 2 (or + 4) on the height. Consider adding
        setWindowSize(BG.getWidth(), BG.getHeight() + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(0, 0); // +x = right
        super.init();

        int x = leftPos;
        int y = topPos;

        // Mode: Item/Redstone
        ScrollInput mode = new SelectionScrollInput(x + 80, y + 22, 82, 20)
                .forOptions(CPFPLang.translatedOptions("gui.power_meter.mode", "item", "redstone"))
                .calling(idx -> setItemMode(idx == 0))
                .setState(menu.contentHolder.itemMode ? 0 : 1)
                .titled(CPFPLang.translate("gui.power_meter.mode").component());

        // Add VALUE time/ksuh
        @SuppressWarnings("Convert2MethodRef")
        ScrollInput value = new ScrollInput(x + 46, y + 58, 62, 20)
                .calling(val -> setIncreaseBy(val))
                .withRange(1, 10_001)
                .withShiftStep(10)
                .setState(menu.contentHolder.increaseBy)
                .titled(CPFPLang.translate("gui.power_meter.amount").component());

        // Add value TIME/KSUH
        ScrollInput measurement = new SelectionScrollInput(x + 108, y + 58, 54, 20)
                .forOptions(CPFPLang.translatedOptions("gui.power_meter.measurement", "hour", "ksuh"))
                .calling(idx -> setHourMeasurement(idx == 0))
                .setState(menu.contentHolder.hourMeasurement ? 0 : 1)
                .titled(CPFPLang.translate("gui.power_meter.measurement").component());

        addRenderableWidget(mode);
        addRenderableWidget(value);
        addRenderableWidget(measurement);
    }

    private void setItemMode(boolean itemMode) {
        CatnipServices.NETWORK.sendToServer(new PowerMeterScreenPacket(itemMode, menu.contentHolder.increaseBy, menu.contentHolder.hourMeasurement));
        menu.contentHolder.itemMode = itemMode;
    }

    private void setIncreaseBy(int increaseBy) {
        CatnipServices.NETWORK.sendToServer(new PowerMeterScreenPacket(menu.contentHolder.itemMode, increaseBy, menu.contentHolder.hourMeasurement));
        menu.contentHolder.increaseBy = increaseBy;
    }

    private void setHourMeasurement(boolean hourMeasurement) {
        CatnipServices.NETWORK.sendToServer(new PowerMeterScreenPacket(menu.contentHolder.itemMode, menu.contentHolder.increaseBy, hourMeasurement));
        menu.contentHolder.hourMeasurement = hourMeasurement;
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
        Component mode = CPFPLang.choice(entity.itemMode, "gui.power_meter.mode", "item", "redstone").component();
        graphics.drawString(font, mode, x + 87, y + 28, 0xffffff);

        if (entity.itemMode)
            ModGuiTexture.POWER_METER_SLOT.render(graphics, x + 9, y + 22);

        // On item inserted/receive redstone signal
        Component action = CPFPLang.choice(entity.itemMode, "gui.power_meter.on", "item", "redstone").component();
        graphics.drawString(font, action, x + 9, y + 48, 0xffffff);

        // Add X Y
        Component add = CPFPLang.translate("gui.power_meter.add").component();
        graphics.drawString(font, add, x + 16, y + 64, 0xffffff);
        graphics.drawString(font, Integer.toString(entity.increaseBy), x + 53, y + 64, 0xffffff);

        Component measurement = CPFPLang.choice(entity.hourMeasurement, "gui.power_meter.measurement", "hour", "ksuh").component();
        graphics.drawString(font, measurement, x + 115, y + 64, 0xffffff);

        // Stats
        // ksuh/Time Left:
        Component left = CPFPLang.choice(entity.hourMeasurement, "gui.power_meter", "time_left", "ksuh_left").component();
        graphics.drawString(font, left, x + 16, y + 90, 0xffffff);

        String leftValue = Integer.toString(entity.thingsLeft);
        graphics.drawString(font, leftValue, x + 104, y + 90, 0xffffff);

        // Total Used:
        Component totalUsed = CPFPLang.translate("gui.power_meter.total_used").component();
        graphics.drawString(font, totalUsed, x + 16, y + 112, 0xffffff);

        String totalUsedValue;

        if (entity.hourMeasurement)
            totalUsedValue = entity.hoursUsed + " h";
        else
            totalUsedValue = entity.ksuh + " ksuh";

        graphics.drawString(font, totalUsedValue, x + 104, y + 112, 0xffffff);
    }
}
