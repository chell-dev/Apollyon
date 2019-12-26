package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.item.ItemBow;

@Module.Info(name = "BowSpam", category = Module.Category.COMBAT, description = "")
public class BowSpam extends Module {
	
	private Setting<Double> speed = register(Settings.d("Speed", 8));
	
	private long currentMS = 0L;
	private long lastMS = -1L;	
		
	public void onUpdate() {
		
		if(Wrapper.getPlayer().getHeldItemMainhand().getItem() instanceof ItemBow && (Wrapper.getMinecraft().gameSettings.keyBindUseItem).isKeyDown()) {
			mc.rightClickDelayTimer = 0;
			currentMS = System.nanoTime() / 1000000;
			if(hasDelayRun((long)(1000 / speed.getValue())))
			{
				Wrapper.getMinecraft().playerController.onStoppedUsingItem(Wrapper.getPlayer());
				lastMS = System.nanoTime() / 1000000;
			}
			}
		
	}
	
	public boolean hasDelayRun(long time) {
		return (currentMS - lastMS) >= time;
	}

}
