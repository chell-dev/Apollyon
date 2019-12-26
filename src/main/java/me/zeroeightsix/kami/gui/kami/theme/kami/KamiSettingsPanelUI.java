package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.gui.kami.RenderHelper;
import me.zeroeightsix.kami.gui.kami.component.SettingsPanel;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.ClickGuiColors;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 16/12/2017.
 */
public class KamiSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {

    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        super.renderComponent(component, fontRenderer);

        float lineR = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).lineRed.getValue();
        float lineG = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).lineGreen.getValue();
        float lineB = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).lineBlue.getValue();

        float fillR = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).fillRed.getValue();
        float fillG = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).fillBlue.getValue();
        float fillB = ((ClickGuiColors) ModuleManager.getModuleByName("ClickGuiColors")).fillGreen.getValue();

//        glLineWidth(2);
//        glColor3f(.59f,.05f,.11f);
//        glBegin(GL_LINES);
//        {
//            glVertex2d(0,component.getHeight());
//            glVertex2d(component.getWidth(),component.getHeight());
//        }
//        glEnd();

        /*
        glLineWidth(2f);
        glColor4f(.17f,.17f,.18f,.9f);
        RenderHelper.drawFilledRectangle(0,0,component.getWidth(),component.getHeight());
        glColor3f(.59f,.05f,.11f);
        glLineWidth(1.5f);
        RenderHelper.drawRectangle(0,0,component.getWidth(),component.getHeight());
         */

        glColor4f(lineR, lineG, lineB, .6f);
        RenderHelper.drawOutlinedRoundedRectangle(0, 0, component.getWidth(), component.getHeight() + 1, 6f, fillR, fillG, fillB, component.getOpacity(), 1.5f);
    }
}