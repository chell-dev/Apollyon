package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketAnimation;


//made by FINZ0
@Module.Info(name = "NoSwing", category = Module.Category.RENDER)
public class NoSwing extends Module {
    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketAnimation) {
            event.cancel();
        }
    });

}
