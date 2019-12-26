package me.zeroeightsix.kami.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.*;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.network.play.server.SPacketChat;
import me.zeroeightsix.kami.util.Wrapper;

/**
 * Made by FINZ0
 */

@Module.Info(name = "AutoReply", category = Module.Category.CHAT, description = "automatically replies to messages")
public class AutoReply extends Module{
	
	@EventHandler
	public Listener <PacketEvent.Receive> receiveListener = new Listener<>(event -> {
		if (event.getPacket() instanceof SPacketChat && ((SPacketChat) event.getPacket()).getChatComponent()
				.getUnformattedText().contains("whispers:"))
			            		Wrapper.getPlayer().sendChatMessage("/r fuck off");
			            }
  );
}