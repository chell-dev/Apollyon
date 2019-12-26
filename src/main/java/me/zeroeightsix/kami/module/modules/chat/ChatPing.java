package me.zeroeightsix.kami.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

@Module.Info(name="ChatPing", category=Module.Category.CHAT)
public class ChatPing extends Module {
    private Setting<Sound> sound = register(Settings.e("Sound", Sound.POP));
    private Setting<Double> volume = register(Settings.doubleBuilder("Volume").withMinimum(0.0).withValue(1.0).build());
    ResourceLocation location;
    SoundEvent sEvent = new SoundEvent(location);

    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketChat){
            if(!((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText().startsWith("<"+mc.player.getName()+">")
               && ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText().toLowerCase().contains(mc.player.getName().toLowerCase())){
                switch (sound.getValue()){
                    case POP:
                        location = new ResourceLocation("minecraft", "sounds/random/pop.ogg");
                        break;
                    case ORB:
                        location = new ResourceLocation("minecraft", "sounds/random/orb.ogg");
                        break;
                    case PLING:
                        location = new ResourceLocation("minecraft", "sounds/note/pling.ogg");
                        break;
                }
                mc.world.playSound(mc.player.getPosition(), sEvent, SoundCategory.MASTER, volume.getValue().floatValue(), 1f, false);
            }
        }
    });

    public enum Sound{
        POP, ORB, PLING
    }
}
