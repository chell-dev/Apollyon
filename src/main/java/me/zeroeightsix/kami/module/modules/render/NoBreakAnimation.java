package me.zeroeightsix.kami.module.modules.render;

import java.util.Iterator;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Info(
    name = "NoBreakAnim",
    category = Category.RENDER,
    description = "Prevents block break animation server side"
)
public class NoBreakAnimation extends Module {
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;
    
    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging cPacketPlayerDigging = (CPacketPlayerDigging)event.getPacket();
            Iterator var3 = mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(cPacketPlayerDigging.getPosition())).iterator();

            while(var3.hasNext()) {
                Entity entity = (Entity)var3.next();
                if (entity instanceof EntityEnderCrystal) {
                    this.resetMining();
                    return;
                }

                if (entity instanceof EntityLivingBase) {
                    this.resetMining();
                    return;
                }
            }

            if (cPacketPlayerDigging.getAction().equals(Action.START_DESTROY_BLOCK)) {
                this.isMining = true;
                this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
            }

            if (cPacketPlayerDigging.getAction().equals(Action.STOP_DESTROY_BLOCK)) {
                this.resetMining();
            }
        }

    });

    public NoBreakAnimation() {
    }

    public void onUpdate() {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.resetMining();
        } else {
            if (this.isMining && this.lastPos != null && this.lastFacing != null) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
            }

        }
    }

    private void setMiningInfo(BlockPos lastPos, EnumFacing lastFacing) {
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    private void resetMining() {
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }
}
