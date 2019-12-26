package me.zeroeightsix.kami.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.zeroeightsix.kami.command.*;

/**
 * Created by 086 on 8/04/2018.
 * improved by FINZ0
 */
@Module.Info(name = "Popbob Sex Dupe", category = Module.Category.CHAT, description = "Modifies your chat messages")
public class CustomChat extends Module {

    private Setting<Boolean> commands = register(Settings.b("Commands", false));
    
    private final String PREFIX = " \u267F ";
    private final String SUFFIX = " \u300b\u1d00\u1d18\u1d0f\u029f\u029f\u028f\u1d0f\u0274"; // \u2714\u039B\u03A1\u038C\u053C\u053C\u04B0\u038C\u048A.\u03DC\u039B\u050C

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            if (s.startsWith(Command.getCommandPrefix()) && !commands.getValue()) return;
            if (s.length() >= 256) s = s.substring(0,256);
            String CustomMessage = s + SUFFIX;
            ((CPacketChatMessage) event.getPacket()).message = CustomMessage;
        }
    });

}
