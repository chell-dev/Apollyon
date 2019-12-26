package me.zeroeightsix.kami.module.modules.combat;

import com.google.common.collect.Lists;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.builder.SettingBuilder;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Info(name = "Surround", category = Module.Category.COMBAT)
public class Surround extends Module {

    private List<Block> whiteList = Arrays.asList(new Block[] {
            Blocks.OBSIDIAN,
            Blocks.ENDER_CHEST,
    });

    private Setting<Integer> future = register(Settings.integerBuilder("Ticks").withMinimum(0).withMaximum(60).withValue(0).build());
    private Setting<Boolean> sneak = register(Settings.b("Sneak Only", false));

    private boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if(!Wrapper.getWorld().getBlockState(neighbour).getMaterial().isReplaceable())
                return true;
        }
        return false;
    }

    @Override
    public void onUpdate() {
    	if(sneak.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) return;
        if (isDisabled() || mc.player == null || ModuleManager.isModuleEnabled("Freecam")) return;
        Vec3d vec3d = EntityUtil.getInterpolatedPos(mc.player, future.getValue());
        BlockPos northBlockPos = new BlockPos(vec3d).north();
        BlockPos southBlockPos = new BlockPos(vec3d).south();
        BlockPos eastBlockPos = new BlockPos(vec3d).east();
        BlockPos westBlockPos = new BlockPos(vec3d).west();

       // //check if block is already placed
       // if(!Wrapper.getWorld().getBlockState(northBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(southBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(eastBlockPos).getMaterial().isReplaceable() || !Wrapper.getWorld().getBlockState(westBlockPos).getMaterial().isReplaceable())
       //     return;
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
        Wrapper.getPlayer().inventory.currentItem = newSlot;

        // check if we don't have a block adjacent to North blockpos
        A: if (!hasNeighbour(northBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = northBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    northBlockPos = neighbour;
                    break A;
                }
            }
            return;
        }
        
        // check if we don't have a block adjacent to South blockpos
        B: if (!hasNeighbour(southBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = southBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    southBlockPos = neighbour;
                    break B;
                }
            }
            return;
        }
        
        // check if we don't have a block adjacent to East blockpos
        C: if (!hasNeighbour(eastBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = eastBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    eastBlockPos = neighbour;
                    break C;
                }
            }
            return;
        }
        
        // check if we don't have a block adjacent to West blockpos
        D: if (!hasNeighbour(westBlockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = westBlockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    westBlockPos = neighbour;
                    break D;
                }
            }
            return;
        }

        // place blocks       
        if(Wrapper.getWorld().getBlockState(northBlockPos).getMaterial().isReplaceable())
        placeBlockScaffold(northBlockPos);
        
        if(Wrapper.getWorld().getBlockState(southBlockPos).getMaterial().isReplaceable())
        placeBlockScaffold(southBlockPos);
        
        if(Wrapper.getWorld().getBlockState(eastBlockPos).getMaterial().isReplaceable())
        placeBlockScaffold(eastBlockPos);
        
        if(Wrapper.getWorld().getBlockState(westBlockPos).getMaterial().isReplaceable())
        placeBlockScaffold(westBlockPos);

        // reset slot
        Wrapper.getPlayer().inventory.currentItem = oldSlot;
    }

    public static boolean placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX,
                Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(),
                Wrapper.getPlayer().posZ);

        for(EnumFacing side : EnumFacing.values())
        {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            // check if side is visible (facing away from player)
            if(eyesPos.squareDistanceTo(
                    new Vec3d(pos).add(0.5, 0.5, 0.5)) >= eyesPos
                    .squareDistanceTo(
                            new Vec3d(neighbor).add(0.5, 0.5, 0.5)))
                continue;

            // check if neighbor can be right clicked
            if(!canBeClicked(neighbor))
                continue;

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                    .add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            // check if hitVec is within range (4.25 blocks)
            if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
                continue;

            // place block
            faceVectorPacketInstant(hitVec);
            processRightClickBlock(neighbor, side2, hitVec);
            Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 0;

            return true;
        }

        return false;
    }

    private static PlayerControllerMP getPlayerController()
    {
        return Minecraft.getMinecraft().playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side,
                                              Vec3d hitVec)
    {
        getPlayerController().processRightClickBlock(Wrapper.getPlayer(),
                mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static IBlockState getState(BlockPos pos)
    {
        return Wrapper.getWorld().getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos)
    {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec)
    {
        float[] rotations = getNeededRotations2(vec);

        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], Wrapper.getPlayer().onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec)
    {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                Wrapper.getPlayer().rotationYaw
                        + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw),
                Wrapper.getPlayer().rotationPitch + MathHelper
                        .wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch)};
    }

    public static Vec3d getEyesPos()
    {
        return new Vec3d(Wrapper.getPlayer().posX,
                Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(),
                Wrapper.getPlayer().posZ);
    }

}

