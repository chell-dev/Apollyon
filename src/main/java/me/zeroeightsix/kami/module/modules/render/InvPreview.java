package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Module.Info(name = "InvPreview", category = Module.Category.RENDER)
public class InvPreview extends Module
{
    private static final ResourceLocation box;
    private Setting<Integer> optionX;
    private Setting<Integer> optionY;

    public InvPreview() {
        this.optionX = this.register(Settings.i("X", 0));
        this.optionY = this.register(Settings.i("Y", 0));
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();
    }

    private static void postboxrender() {
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        final NonNullList<ItemStack> items = (NonNullList<ItemStack>)mc.player.inventory.mainInventory;
        this.boxrender(this.optionX.getValue(), this.optionY.getValue());
        this.itemrender(items, this.optionX.getValue(), this.optionY.getValue());
    }

    private void boxrender(final int x, final int y) {
        preboxrender();
        mc.renderEngine.bindTexture(InvPreview.box);
        mc.ingameGUI.drawTexturedModalRect(x, y, 7, 17, 162, 54);
        postboxrender();
    }

    private void itemrender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotx = x + 1 + item % 9 * 18;
            final int sloty = y + 1 + (item / 9 - 1) * 18;
            preitemrender();
            mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)items.get(item), slotx, sloty);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, (ItemStack)items.get(item), slotx, sloty);
            postitemrender();
        }
    }

    static {
        box = new ResourceLocation("textures/gui/container/shulker_box.png");
    }
}
