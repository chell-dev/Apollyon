package me.zeroeightsix.kami.module.modules.chat;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import me.zeroeightsix.kami.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Info(name = "AutoEZ", category = Category.CHAT)
public class AutoEZheph extends Module {
	EntityEnderCrystal crystal;
    private ConcurrentHashMap<String, Integer> targetedPlayers = null;
    @EventHandler
    public Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
        if (mc.player != null) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            if (event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity cPacketUseEntity = (CPacketUseEntity)event.getPacket();
                if (cPacketUseEntity.getAction().equals(Action.ATTACK)) {
                    Entity targetEntity = cPacketUseEntity.getEntityFromWorld(mc.world);
                    if (EntityUtil.isPlayer(targetEntity)) {
                        this.addTargetedPlayer(targetEntity.getName());
                    }
                }
            }
        }
    });
    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (mc.player != null) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            Entity entity = null;
            Packet packet = event.getPacket();
            if (packet instanceof SPacketAnimation) {
                SPacketAnimation sPacketAnimation = (SPacketAnimation)packet;
                entity = mc.world.getEntityByID(sPacketAnimation.getEntityID());
            } else if (packet instanceof SPacketEntityMetadata) {
                SPacketEntityMetadata sPacketEntityMetadata = (SPacketEntityMetadata)packet;
                entity = mc.world.getEntityByID(sPacketEntityMetadata.getEntityId());
            } else if (packet instanceof SPacketEntityProperties) {
                SPacketEntityProperties sPacketEntityProperties = (SPacketEntityProperties)packet;
                entity = mc.world.getEntityByID(sPacketEntityProperties.getEntityId());
            } else {
                if (!(packet instanceof SPacketEntityStatus)) {
                    return;
                }

                SPacketEntityStatus sPacketEntityStatus = (SPacketEntityStatus)packet;
                entity = sPacketEntityStatus.getEntity(mc.world);
            }

            if (entity != null) {
                if (EntityUtil.isPlayer(entity)) {
                    EntityPlayer player = (EntityPlayer)entity;
                    if (player.getHealth() <= 0.0F) {
                        this.announceInChat(player.getName());
                    }
                }
            }
        }
    });
    
    public void onEnable() {
        this.targetedPlayers = new ConcurrentHashMap();
    }

    public void onDisable() {
        this.targetedPlayers = null;
    }

    public void onUpdate(PacketEvent.Send event) {
        if (!this.isDisabled() && mc.player != null) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            Iterator var1 = mc.world.getLoadedEntityList().iterator();

            while(var1.hasNext()) {
                Entity entity = (Entity)var1.next();
                if (EntityUtil.isPlayer(entity)) {
                    EntityPlayer player = (EntityPlayer)entity;
                    if (player.getHealth() <= 0.0F && player.isDead) {
                        String name = player.getName();
                        if (this.targetedPlayers.containsKey(name)) {
                            this.announceInChat(name);
                            break;
                        }
                    }
                }
            }

            this.targetedPlayers.forEach((namex, timeout) -> {
                if (timeout <= 0 || timeout > 20) {
                    this.targetedPlayers.remove(namex);
                } else {
                    this.targetedPlayers.put(namex, timeout - 1);
                }

            });
        }
    }

    public void onWorldRender(RenderEvent event) {
        if (mc.player != null) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            mc.world.loadedEntityList.stream().filter(EntityUtil::isPlayer).filter((entity) -> {
                return mc.player != entity;
            }).map((entity) -> {
                return (EntityPlayer)entity;
            }).filter((player) -> {
                return player.isDead;
            }).forEach((player) -> {
                this.announceInChat(player.getName());
            });
        }
    }

    private void announceInChat(String name) {
        if (this.targetedPlayers.containsKey(name)) {
            this.targetedPlayers.remove(name);
            StringBuilder message = new StringBuilder();
                message.append("yep, ");
                message.append(name);
                message.append(" is going to the cringe compilation.");
          //      message.append("Apollyon owns me and all!");
            

            String messageSanitized = message.toString().replaceAll("�", "");
            if (messageSanitized.length() > 255) {
                messageSanitized = messageSanitized.substring(0, 255);
            }

            mc.player.connection.sendPacket(new CPacketChatMessage(messageSanitized));
        }
    }

    public void addTargetedPlayer(String name) {
        if (!Objects.equals(name, mc.player.getName())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }

            this.targetedPlayers.put(name, 20);
        }
    }
}
