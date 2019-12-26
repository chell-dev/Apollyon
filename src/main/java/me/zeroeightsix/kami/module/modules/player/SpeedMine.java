package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;


@Module.Info(name = "SpeedMine", category = Module.Category.PLAYER, description = "")
public class SpeedMine extends Module{
	
	public void onUpdate() {		
        if (Wrapper.getMinecraft().playerController.curBlockDamageMP < 1) {
        	Wrapper.getMinecraft().playerController.curBlockDamageMP = 1;
        }
        if (Wrapper.getMinecraft().playerController.blockHitDelay > 1) {
        	Wrapper.getMinecraft().playerController.blockHitDelay = 1;
        }

	}

}
