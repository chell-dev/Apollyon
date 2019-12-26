package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.world.GameType;
import me.zeroeightsix.kami.util.Wrapper;

/**
 * Made by FINZ0
 */

@Module.Info(name = "FakeCreative", category = Module.Category.MISC, description = "")
public class FakeCreative extends Module {

	   public void onUpdate() {
		Wrapper.getMinecraft().playerController.setGameType(GameType.CREATIVE);

  }
	   
	   
		   public void onDisable() {
			   Wrapper.getMinecraft().playerController.setGameType(GameType.SURVIVAL);    
		   
	   }
}