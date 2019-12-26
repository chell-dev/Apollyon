package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

/**
 * Made by FINZ0
 */
@Module.Info(name = "FakePlayer", category = Module.Category.MISC, description = "")
public class FakePlayer extends Module {
    private EntityOtherPlayerMP clonedPlayer;

    public void onEnable(){
        if (mc.player != null) {
            clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            clonedPlayer.copyLocationAndAnglesFrom(mc.player);
            clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-100, clonedPlayer);
        }
    }

    public void onDisable(){
        if (mc.player != null) {
            mc.world.removeEntityFromWorld(-100);
            clonedPlayer = null;
        }
    }

}