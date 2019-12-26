package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.Module.Category;
import me.zeroeightsix.kami.setting.*;
import net.minecraftforge.common.*;
import me.zeroeightsix.kami.event.events.*;
import net.minecraft.client.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.*;
import me.zeroeightsix.kami.util.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.eventhandler.*;

@Module.Info(name = "BlockHighlight", category = Category.RENDER)
public class BlockHighlight extends Module {
    private static BlockPos position;   
    
    private Setting<Integer> red = register(Settings.integerBuilder("Red").withRange(1, 255).withValue(255).build());
    private Setting<Integer> green = register(Settings.integerBuilder("Green").withRange(1, 255).withValue(255).build());
    private Setting<Integer> blue = register(Settings.integerBuilder("Blue").withRange(1, 255).withValue(255).build());
    private Setting<Integer> alpha = register(Settings.integerBuilder("Opacity").withRange(1, 255).withValue(255).build());
    private Setting<Integer> width = register(Settings.integerBuilder("Width").withRange(1, 50).withValue(1).build());
    
    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        position = null;
    }

    /*
    @Override
    public void onWorldRender(final RenderEvent event) {
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos blockpos = ray.getBlockPos();
            final IBlockState iblockstate = mc.world.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                final Vec3d interp = MathUtil.interpolateEntity((Entity)mc.player, mc.getRenderPartialTicks());
                KamiTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox((World)mc.world, blockpos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), this.width.getValue(), this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
            }
        }
    }
     */
    @Override
    public void onWorldRender(final RenderEvent event) {
        final RayTraceResult ray = mc.objectMouseOver;
        if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos blockpos = ray.getBlockPos();
            final IBlockState iblockstate = mc.world.getBlockState(blockpos);
            if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                final Vec3d interp = MathUtil.interpolateEntity((Entity)mc.player, mc.getRenderPartialTicks());
                KamiTessellator.prepareGL();
                KamiTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox((World)mc.world, blockpos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), this.width.getValue(), this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
                KamiTessellator.releaseGL();
            }
        }
    }
    
    @SubscribeEvent
    public void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
        if (mc.player == null || mc.world == null || (!mc.playerController.getCurrentGameType().equals(GameType.SURVIVAL) && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE))) return;
        event.setCanceled(true);
    }
}
