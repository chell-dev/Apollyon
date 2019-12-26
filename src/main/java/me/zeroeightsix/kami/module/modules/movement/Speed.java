package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

import net.minecraft.client.Minecraft;

/**
 * Created by 086 on 23/08/2017.
 */
@Module.Info(name = "SpeedVanilla", description = "vanilla speed", category = Module.Category.MOVEMENT)
public class Speed extends Module {
	 private Setting<Double> speed = register(Settings.d("Speed", 1));
	 
     Minecraft mc;


    @Override
    public void onEnable() {
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {
        if((mc.player.moveForward != 0 || mc.player.moveStrafing != 0)
                && !mc.player.isSneaking() && mc.player.onGround) {
            mc.player.motionX *= speed.getValue();
            mc.player.motionZ *= speed.getValue();
        }
    }


}
