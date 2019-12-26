package me.zeroeightsix.kami.module.modules.chat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import me.zeroeightsix.kami.command.*;
import java.util.*;

@Module.Info(name = "StrDetect", category = Category.CHAT, description = "Detects when players have Strength 2")
public class StrengthDetect extends Module{
    private Set<EntityPlayer> str;
    
    public StrengthDetect() {
        this.str = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
    }
    
    @Override
    public void onUpdate() {
        for (final EntityPlayer player : StrengthDetect.mc.world.playerEntities) {
            if (player.equals((Object)StrengthDetect.mc.player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(player)) {
                        Command.sendChatMessage(player.getName() + " just drank strength!");
                this.str.add(player);
            }
            if (!this.str.contains(player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH)) {
                continue;
            }
                Command.sendChatMessage(player.getName() + " just ran out of strength!");
            this.str.remove(player);
        }
    }
}
