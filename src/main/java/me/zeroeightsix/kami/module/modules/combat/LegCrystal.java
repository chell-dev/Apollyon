package me.zeroeightsix.kami.module.modules.combat;

import java.util.Iterator;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.module.Module.Info;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Info(
   name = "LegCrystals",
   category = Category.COMBAT
)
public class LegCrystal extends Module {
   private Setting<Double> range = register(Settings.doubleBuilder("Range").withMinimum(1.0D).withValue(5.5D).withMaximum(10.0D));
   private boolean switchCooldown = false;
   
   private static final Vec3d NORTH1 = new Vec3d(0.0D, 0.0D, -1.0D);
   private static final Vec3d NORTH2 = new Vec3d(0.0D, 0.0D, -2.0D);
   private static final Vec3d EAST1 = new Vec3d(1.0D, 0.0D, 0.0D);
   private static final Vec3d EAST2 = new Vec3d(2.0D, 0.0D, 0.0D);
   private static final Vec3d SOUTH1 = new Vec3d(0.0D, 0.0D, 1.0D);
   private static final Vec3d SOUTH2 = new Vec3d(0.0D, 0.0D, 2.0D);
   private static final Vec3d WEST1 = new Vec3d(-1.0D, 0.0D, 0.0D);
   private static final Vec3d WEST2 = new Vec3d(-2.0D, 0.0D, 0.0D);

   public void onUpdate() {
      if (mc.player != null) {
         int crystalSlot = -1;
         if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            crystalSlot = mc.player.inventory.currentItem;
         } else {
            for(int slot = 0; slot < 9; ++slot) {
               if (mc.player.inventory.getStackInSlot(slot).getItem() == Items.END_CRYSTAL) {
                  crystalSlot = slot;
                  break;
               }
            }
         }

         if (crystalSlot != -1) {
            EntityPlayer closestTarget = this.findClosestTarget();
            if (closestTarget != null) {
               Vec3d targetVector = this.findPlaceableBlock(closestTarget.getPositionVector().add(0.0D, -1.0D, 0.0D));
               if (targetVector != null) {
                  BlockPos targetBlock = new BlockPos(targetVector);
                  if (mc.player.inventory.currentItem != crystalSlot) {
                     mc.player.inventory.currentItem = crystalSlot;
                     this.switchCooldown = true;
                  } else if (this.switchCooldown) {
                     this.switchCooldown = false;
                  } else {
                     mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetBlock, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                  }
               }
            }
         }
      }
   }

   private Vec3d findPlaceableBlock(Vec3d startPos) {
      if (this.canPlaceCrystal(startPos.add(NORTH2)) && !this.isExplosionProof(startPos.add(NORTH1).add(0.0D, 1.0D, 0.0D))) {
         return startPos.add(NORTH2);
      } else if (this.canPlaceCrystal(startPos.add(NORTH1))) {
         return startPos.add(NORTH1);
      } else if (this.canPlaceCrystal(startPos.add(EAST2)) && !this.isExplosionProof(startPos.add(EAST1).add(0.0D, 1.0D, 0.0D))) {
         return startPos.add(EAST2);
      } else if (this.canPlaceCrystal(startPos.add(EAST1))) {
         return startPos.add(EAST1);
      } else if (this.canPlaceCrystal(startPos.add(SOUTH2)) && !this.isExplosionProof(startPos.add(SOUTH1).add(0.0D, 1.0D, 0.0D))) {
         return startPos.add(SOUTH2);
      } else if (this.canPlaceCrystal(startPos.add(SOUTH1))) {
         return startPos.add(SOUTH1);
      } else if (this.canPlaceCrystal(startPos.add(WEST2)) && !this.isExplosionProof(startPos.add(WEST1).add(0.0D, 1.0D, 0.0D))) {
         return startPos.add(WEST2);
      } else {
         return this.canPlaceCrystal(startPos.add(WEST1)) ? startPos.add(WEST1) : null;
      }
   }

   private EntityPlayer findClosestTarget() {
      EntityPlayer closestTarget = null;
      Iterator var2 = mc.world.playerEntities.iterator();

      while(var2.hasNext()) {
         EntityPlayer target = (EntityPlayer)var2.next();
         if (target != mc.player && !Friends.isFriend(target.getName()) && EntityUtil.isLiving(target) && target.getHealth() > 0.0F && (double)mc.player.getDistance(target) <= (Double)this.range.getValue()) {
            if (closestTarget == null) {
               closestTarget = target;
            } else if (mc.player.getDistance(target) < mc.player.getDistance(closestTarget)) {
               closestTarget = target;
            }
         }
      }

      return closestTarget;
   }

   private boolean canPlaceCrystal(Vec3d vec3d) {
      BlockPos blockPos = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
      BlockPos boost = blockPos.add(0, 1, 0);
      BlockPos boost2 = blockPos.add(0, 2, 0);
      return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
   }

   private boolean isExplosionProof(Vec3d vec3d) {
      BlockPos blockPos = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
      Block block = mc.world.getBlockState(blockPos).getBlock();
      if (block == Blocks.BEDROCK) {
         return true;
      } else if (block == Blocks.OBSIDIAN) {
         return true;
      } else if (block == Blocks.ANVIL) {
         return true;
      } else if (block == Blocks.ENDER_CHEST) {
         return true;
      } else {
         return block == Blocks.BARRIER;
      }
   }
}
