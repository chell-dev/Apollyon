package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
/**
 * Created by 086 on 23/08/2017.
 */
@Module.Info(name = "FastFall", description = "", category = Module.Category.MOVEMENT)
public class FastFall2 extends Module {
	private Setting<Integer> distance = register(Settings.i("Distance", 15));
	private Setting<Integer> speed = register(Settings.i("Speed", 3));

	public void onUpdate() {
		if(Wrapper.getPlayer().fallDistance >= distance.getValue()) {
			mc.player.addVelocity(0, -speed.getValue(), 0);
		}
	}
    
   
}
