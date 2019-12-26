package me.zeroeightsix.kami.module.modules.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Info(name = "VisualRange", category = Category.CHAT)
public class VisualRange extends Module {
    private Setting<Boolean> leaving = this.register(Settings.b("Leaving", false));
    private Setting<Boolean> publicS = this.register(Settings.b("Public", false));
    private Setting<Boolean> privateS = this.register(Settings.b("Private", true));
    private List<String> knownPlayers;

    public VisualRange() {
    }

    public void onUpdate() {
        if (mc.player != null) {
            List<String> tickPlayerList = new ArrayList();
            Iterator var2 = mc.world.getLoadedEntityList().iterator();

            while(var2.hasNext()) {
                Entity entity = (Entity)var2.next();
                if (entity instanceof EntityPlayer) {
                    tickPlayerList.add(entity.getName());
                }
            }

            String playerName;
            if (tickPlayerList.size() > 0) {
                var2 = tickPlayerList.iterator();

                while(var2.hasNext()) {
                    playerName = (String)var2.next();
                    if (!playerName.equals(mc.player.getName()) && !this.knownPlayers.contains(playerName)) {
                        this.knownPlayers.add(playerName);
                        if(privateS.getValue()) {
                        if (Friends.isFriend(playerName)) {
                            this.sendNotification(playerName + " entered visual range");
                        } else {
                            this.sendNotification(playerName + " entered visual range");
                        }
                        }
                        if(publicS.getValue()) {
                        	if (Friends.isFriend(playerName)) {
                                mc.player.sendChatMessage(playerName + " entered visual range");
                            } else {
                            	mc.player.sendChatMessage(playerName + " entered visual range");
                            }	
                        }

                        return;
                    }
                }
            }

            if (this.knownPlayers.size() > 0) {
                var2 = this.knownPlayers.iterator();

                while(var2.hasNext()) {
                    playerName = (String)var2.next();
                    if (!tickPlayerList.contains(playerName)) {
                        this.knownPlayers.remove(playerName);
                        if ((Boolean)this.leaving.getValue()) {
                        	if(privateS.getValue()) {
                            if (Friends.isFriend(playerName)) {
                                this.sendNotification(playerName + " left visual range");
                            } else {
                                this.sendNotification(playerName + " left visual range");
                            }
                        	}
                        	if(publicS.getValue()) {
                                if (Friends.isFriend(playerName)) {
                                	mc.player.sendChatMessage(playerName + " left visual range");
                                } else {
                                	mc.player.sendChatMessage(playerName + " left visual range");
                                }
                            	}
                        }

                        return;
                    }
                }
            }

        }
    }

    private void sendNotification(String s) {
        Command.sendChatMessage(s);
    }

    public void onEnable() {
        this.knownPlayers = new ArrayList();
    }
}