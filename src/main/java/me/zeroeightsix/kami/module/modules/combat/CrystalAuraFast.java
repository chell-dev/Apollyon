package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
//import me.zeroeightsix.kami.module.modules.chat.AutoEZheph;
import me.zeroeightsix.kami.module.modules.chat.AutoEZheph;
import me.zeroeightsix.kami.module.modules.render.Tracers;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.zeroeightsix.kami.util.EntityUtil.calculateLookAt;

/**
 * Created by 086 on 28/12/2017.
 * Updated 3 December 2019 by hub
 * Modified by FINZ0
 */
@Module.Info(name = "AutoCrystal", category = Module.Category.COMBAT)
public class CrystalAuraFast extends Module {

	private Setting<Boolean> explode = register(Settings.b("Explode", true));
	private Setting<Integer> waitTick = register(Settings.integerBuilder("Hit Delay").withMinimum(0).withValue(1).build());
    private Setting<Double>  range = register(Settings.doubleBuilder("Hit Range").withRange(0.0, 10.0).withValue(5.0).build());
    private Setting<Double> walls = register(Settings.doubleBuilder("Through Walls Range").withRange(0.0, 10.0).withValue(3.5).build());
    private Setting<Boolean> antiWeakness = register(Settings.b("Anti Weakness", false));
    private Setting<Boolean> nodesync = register(Settings.b("NoDesync", true));

	private Setting<Boolean> place = register(Settings.b("Place", true));
    private Setting<Boolean> autoSwitch = register(Settings.b("Auto Switch", true));
    private Setting<Double>  placeRange = register(Settings.doubleBuilder("Place Range").withRange(0.0, 10.0).withValue(5.0).build());
    private Setting<Double> minDmg = register(Settings.doubleBuilder("Min Damage").withMinimum(0.0).withValue(3.8).build());
    private Setting<Double> facePlace = register(Settings.doubleBuilder("Faceplace Health").withMinimum(0.0).withValue(6.0).build());
    private Setting<Boolean> raytrace = register(Settings.b("RayTrace", true));
    private Setting<Boolean> rotate = register(Settings.b("Rotate", true));
    private Setting<Boolean> spoofRotations = register(Settings.b("Spoof Angles", true));
    private Setting<Boolean> chat = register(Settings.b("Chat Messages", true));

    private Setting<Integer> espR = register(Settings.integerBuilder("ESP Red").withRange(0, 255).withValue(200).build());
    private Setting<Integer> espG = register(Settings.integerBuilder("ESP Green").withRange(0, 255).withValue(50).build());
    private Setting<Integer> espB = register(Settings.integerBuilder("ESP Blue").withRange(0, 255).withValue(200).build());
    private Setting<Integer> espA = register(Settings.integerBuilder("ESP Alpha").withRange(0, 255).withValue(255).build());

    private BlockPos render;
    private Entity renderEnt;
    private long systemTime = -1;
    private static boolean togglePitch = false;
	// we need this cooldown to not place from old hotbar slot, before we have switched to crystals
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;
    private int waitCounter;
    EnumFacing f;

    @Override
    public void onUpdate() {
        if(mc.player == null) return; // bruh
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);
        if (explode.getValue() && crystal != null && mc.player.getDistance(crystal) <= range.getValue()) {
        	if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > walls.getValue()) return;
        	
        	  if (waitTick.getValue() > 0) {
                  if (waitCounter < waitTick.getValue()) {
                      waitCounter++;
                      return;
                  } else {
                      waitCounter = 0;
                  }
              }
        	
                if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                    if (!isAttacking) {
                        // save initial player hand
                        oldSlot = Wrapper.getPlayer().inventory.currentItem;
                        isAttacking = true;
                    }
                    // search for sword and tools in hotbar
                    newSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
                        if (stack == ItemStack.EMPTY) {
                            continue;
                        }
                        if ((stack.getItem() instanceof ItemSword)) {
                            newSlot = i;
                            break;
                        }
                        if ((stack.getItem() instanceof ItemTool)) {
                            newSlot = i;
                            break;
                        }
                    }
                    // check if any swords or tools were found
                    if (newSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = newSlot;
                        switchCooldown = true;
                    }
                }
                lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                return;         
        } else {
            if(rotate.getValue()) resetRotation();
			if (oldSlot != -1) {
                Wrapper.getPlayer().inventory.currentItem = oldSlot;
                oldSlot = -1;
			}
            isAttacking = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
		
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();
        List<Entity> entities = new ArrayList<>();
            entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
        
        BlockPos q = null;
        double damage = .5;
        for (Entity entity : entities) {
            if(mc.player == null || entity == null) continue;
            if(entity == mc.player) continue;
            if (((EntityLivingBase) entity).getHealth() <= 0 || entity.isDead) {
                if(ModuleManager.isModuleEnabled("AutoEZ")) {
                    ((AutoEZheph) ModuleManager.getModuleByName("AutoEZ")).addTargetedPlayer(entity.getName());
                }
                continue;
            }
            for (BlockPos blockPos : blocks) {
                double b = entity.getDistanceSq(blockPos);
                if (b >= 169) {
                    continue; // If this block if further than 13 (3.6^2, less calc) blocks, ignore it. It'll take no or very little damage
                }
                double d = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, entity);
                if(d < minDmg.getValue() && ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount() > facePlace.getValue()) {
                	continue;
                }
                if (d > damage) {
                    double self = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, mc.player);
                    // If this deals more damage to ourselves than it does to our target, continue. This is only ignored if the crystal is sure to kill our target but not us.
                    // Also continue if our crystal is going to hurt us.. alot
                    if ((self > d && !(d < ((EntityLivingBase) entity).getHealth())) || self - .5 > mc.player.getHealth()) {
                        continue;
                    }
                    damage = d;
                    q = blockPos;
                    renderEnt = entity;
                }
            }
        }
        if (damage == .5) {
            render = null;
            renderEnt = null;
            if(rotate.getValue()) resetRotation();
            return;
        }
        render = q;

        if (place.getValue()) {
            if(mc.player == null) return;
            lookAtPacket(q.x + .5, q.y - .5, q.z + .5, mc.player);
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(q.x + .5, q.y - .5d, q.z + .5));
            if(raytrace.getValue()) {
                if(result == null || result.sideHit == null) {
                    q = null;
                    f = null;
                    //render = null;
                    if (rotate.getValue()) resetRotation();
                    return;
                } else {
                    f = result.sideHit;
                }
            }

            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (autoSwitch.getValue()) {
                    mc.player.inventory.currentItem = crystalSlot;
                    if (rotate.getValue()) resetRotation();
                    switchCooldown = true;
                }
                return;
            }
                // return after we did an autoswitch
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }
                //mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                if(q != null && mc.player != null) {
                    if (raytrace.getValue() && f != null) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    } else {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    }
                }
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (render != null && mc.player != null) {
            KamiTessellator.prepare(GL11.GL_QUADS);
            KamiTessellator.drawBox(render, espR.getValue(), espG.getValue(), espB.getValue(), espA.getValue(), GeometryMasks.Quad.ALL);
            KamiTessellator.release();
        }
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue().floatValue(), placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        /*if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));*/
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
         //   damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else {
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    //Better Rotation Spoofing System:

    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @EventHandler
    private Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && spoofRotations.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });

    @EventHandler
    private Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
             if (event.getPacket() instanceof SPacketSoundEffect && nodesync.getValue()) {
                 final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                 if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                     for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                         if (e != null && e instanceof EntityEnderCrystal) {
                             if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                                 e.setDead();
                             }
                         }
                     }
                 }
             }
    
    });
    @Override
    public void onEnable() { 
    	if(chat.getValue() && mc.player != null) {
        	Command.sendChatMessage("AutoCrystal \u00A72ON");
        }
    }

    @Override
    public void onDisable() {
        render = null;
        renderEnt = null;
        resetRotation();
        if(chat.getValue()) {
        	Command.sendChatMessage("AutoCrystal \u00A74OFF");
        }
    }
}
