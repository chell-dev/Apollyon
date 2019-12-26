package me.zeroeightsix.kami.module.modules.misc;

import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent.Send;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;

@Info(
    name = "BuildHeight",
    category = Category.MISC
)
public class BuildHeight extends Module {
    @EventHandler
    private Listener<Send> packetListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock oldPacket = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (oldPacket.getPos().getY() >= 255) {
                if (oldPacket.getDirection() == EnumFacing.UP) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(oldPacket.getPos(), EnumFacing.DOWN, oldPacket.getHand(), oldPacket.getFacingX(), oldPacket.getFacingY(), oldPacket.getFacingZ()));
                    event.cancel();
                }
            }
        }
    });

    public BuildHeight() {
    }
}
