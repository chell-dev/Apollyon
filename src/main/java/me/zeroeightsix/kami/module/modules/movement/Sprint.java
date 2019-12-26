package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "Sprint", description = "Automatically sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {
	
	public void onUpdate() {
		if(mc.player.moveForward > 0) {
			mc.player.setSprinting(true);
		}
	}
	
	public void onDisable() {
		
	}

}
