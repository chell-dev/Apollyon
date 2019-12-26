package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.gui.GuiGameOver;

/**
 * Made by FINZ0
 */

@Module.Info(name = "AntiDeathScreen", category = Module.Category.MISC, description = "")
public class AntiDeathScreen extends Module {
    public void onUpdate() {
        if (mc.player.getHealth() > 0 && mc.currentScreen instanceof GuiGameOver)
        {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
    
    public void onDisable() {
    	
    }

}