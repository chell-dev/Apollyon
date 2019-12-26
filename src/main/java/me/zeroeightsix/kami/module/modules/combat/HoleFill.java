package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
//import sun.awt.image.PixelConverter;

import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.Wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//made by FINZ0 on 12.12.2019
@Module.Info(name = "HoleFill", category = Module.Category.COMBAT)
public class HoleFill extends Module {
    private ArrayList<BlockPos> holes = new ArrayList();
    
    private List<Block> whiteList = Arrays.asList(new Block[] {
            Blocks.OBSIDIAN
          //  Blocks.ENDER_CHEST,
    });
    
    private Setting<Double> range = register(Settings.doubleBuilder("Range").withRange(0.0, 10.0).withValue(4.0).build());
  //  private Setting<Double> minRange = register(Settings.doubleBuilder("Min Range").withRange(0.0, 10.0).withValue(1.0));
    private Setting<Double> yRange = register(Settings.doubleBuilder("Y Range").withRange(0.0, 10.0).withValue(1.0).build());
    private Setting<Integer> waitTick = register(Settings.integerBuilder("Tick Delay").withMinimum(0).withValue(3).build());
    private Setting<Boolean> chat = register(Settings.b("Chat Messages", true));

    BlockPos pos;
    private int waitCounter;

    public void onUpdate() {
        this.holes = new ArrayList();

            Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-range.getValue(), -yRange.getValue(), -range.getValue()), mc.player.getPosition().add(range.getValue(), yRange.getValue(), range.getValue()));
            for (BlockPos pos : blocks) {
                if (!mc.world.getBlockState(pos).getMaterial().blocksMovement() && !mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
                    boolean solidNeighbours = (
                            mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN
                                    && mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                                    && mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN
                                    && mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                                    && mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN
                                    && mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR
                                    && mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR
                                    && mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR);
                    if (solidNeighbours) {
                        this.holes.add(pos);
                    }
                }
            }
            
            // search blocks in hotbar
            int newSlot = -1;
            for(int i = 0; i < 9; i++)
            {
                // filter out non-block items
                ItemStack stack =
                        Wrapper.getPlayer().inventory.getStackInSlot(i);

                if(stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                    continue;
                }
                // only use whitelisted blocks
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (!whiteList.contains(block)) {
                    continue;
                }

                newSlot = i;
                break;
            }

            // check if any blocks were found
            if(newSlot == -1)
                return;

            // set slot
            int oldSlot = Wrapper.getPlayer().inventory.currentItem;
        //    Wrapper.getPlayer().inventory.currentItem = newSlot;
            
      	  if (waitTick.getValue() > 0) {
              if (waitCounter < waitTick.getValue()) {
                //  waitCounter++;
                  Wrapper.getPlayer().inventory.currentItem = newSlot;
                  holes.forEach(blockPos -> place(blockPos));
                  Wrapper.getPlayer().inventory.currentItem = oldSlot;
                  return;
              } else {
                  waitCounter = 0;
              }
          }
            
          //  holes.forEach(blockPos -> BlockInteractionHelper.placeBlockScaffold(blockPos));
    }
    
    public void onEnable() {
    	if(mc.player != null && chat.getValue()) Command.sendChatMessage("HoleFill Enabled!");   	
    }
    
    public void onDisable() {
    	if(mc.player != null && chat.getValue()) Command.sendChatMessage("HoleFill Disabled!");   	
    }
    
    private void place(BlockPos blockPos) {
    	//if(mc.player.getDistanceSq(blockPos) <= minRange.getValue()) return;
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))) {
            if (entity instanceof EntityLivingBase) { return; }
        }// entity on block
    //	mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        if(!mc.player.isSneaking())  mc.player.setSneaking(true);
    	BlockInteractionHelper.placeBlockScaffold(blockPos);
    //	mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    	if(mc.player.isSneaking()) mc.player.setSneaking(false);
        waitCounter++;   	
    }
    
}