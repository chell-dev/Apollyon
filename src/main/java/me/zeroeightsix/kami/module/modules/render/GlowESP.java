package me.zeroeightsix.kami.module.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.Wrapper;

/**
 * pasted from rusherhack and fixed by FINZ0
 */

 @Module.Info(name = "GlowESP", category = Module.Category.RENDER, description = "Gives players glowing effect")
public class GlowESP extends Module{
		
	public void onUpdate() {
		if (Wrapper.getMinecraft().getRenderManager().options == null) return;
		
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayer) {
				if (!entity.isGlowing()) {
					entity.setGlowing(true);
				}
			}
		}

	}

	public void onDisable() {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayer) {
				if (entity.isGlowing()) {
					entity.setGlowing(false);
				}
			}
		}
		super.onDisable();
	}

}
