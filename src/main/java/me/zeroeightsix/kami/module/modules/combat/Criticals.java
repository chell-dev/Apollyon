package me.zeroeightsix.kami.module.modules.combat;

import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.module.Module;

/***
 * @author S-B99
 */
@Module.Info(name = "Criticals", category = Module.Category.COMBAT)
public class Criticals extends Module
{
    @EventHandler
    private Listener<AttackEntityEvent> attackEntityEventListener= new Listener<AttackEntityEvent>(event -> {
            if (!Criticals.mc.player.isInWater() && !Criticals.mc.player.isInLava()) {
                if (Criticals.mc.player.onGround) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY + 0.1625, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY + 4.0E-6, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY + 1.0E-6, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer());
                    mc.player.onCriticalHit(event.getTarget());
                }
            }
        });//, (Predicate<AttackEntityEvent>[])new Predicate[0]);
}
