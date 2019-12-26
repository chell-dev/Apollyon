package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import net.minecraft.init.Items;

/**
 *  made by FINZ0
 */
@Module.Info(name = "FastUse", category = Module.Category.PLAYER, description = "")
public class FastUse extends Module {
	private Setting<Boolean> xp = register(Settings.b("EXP", false));
	private Setting<Boolean> crystals = register(Settings.b("Crystals", false));
	private Setting<Boolean> all = register(Settings.b("Everything", false));
	public void onUpdate() {
		if(xp.getValue()) {
        if (mc.player != null && (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE)) {         	
            	  mc.rightClickDelayTimer = 0;
        }
	 }
		
		if(crystals.getValue()) {
			if (mc.player != null && (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {	            	
          	  mc.rightClickDelayTimer = 0;
      }
	}
		
		if(all.getValue()) {
			mc.rightClickDelayTimer = 0;
		}
   }
}
