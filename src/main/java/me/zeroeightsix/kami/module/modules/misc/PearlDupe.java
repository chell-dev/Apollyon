package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

/**
 * Made by FINZ0
 */

@Module.Info(name = "PearlDupe", category = Module.Category.MISC)
public class PearlDupe extends Module {
	
	
	  @EventHandler
	    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
	    	if(event.getPacket() instanceof CPacketConfirmTeleport) {
	    		mc.player.sendChatMessage("/kill");
	    			disable();
	    		}	    	
	    });
    
	public void onEnable() {
		if(mc.player != null) {
		Command.sendChatMessage("PearlDupe enabled");
		Command.sendChatMessage("THIS SENDS /KILL WHEN YOU TELEPORT");
		}
	}
	  
    public void onDisable() {
    	if(mc.player != null) {
    	Command.sendChatMessage("PearlDupe disabled");
    	}
    }

}