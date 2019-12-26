package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


import java.util.ArrayList;

@Module.Info(name = "HoleESP", category = Module.Category.RENDER)
public class HoleESP extends Module {

    private final BlockPos[] surroundOffset = {
            new BlockPos(0, -1, 0), // down
            new BlockPos(0, 0, -1), // north
            new BlockPos(1, 0, 0), // east
            new BlockPos(0, 0, 1), // south
            new BlockPos(-1, 0, 0) // west
    };

    private Setting<Mode> mode = register(Settings.e("Mode", Mode.BOX));
    private Setting<Double> renderDistance = register(Settings.doubleBuilder("Range").withMinimum(0.0d).withValue(10.0d).build());
    private Setting<Integer> r1 = register(Settings.integerBuilder("Red").withMinimum(0).withValue(200).withMaximum(255).build()); // 144
    private Setting<Integer> g1 = register(Settings.integerBuilder("Green").withMinimum(0).withValue(50).withMaximum(255).build());
    private Setting<Integer> b1 = register(Settings.integerBuilder("Blue").withMinimum(0).withValue(200).withMaximum(255).build());
    private Setting<Integer> a0 = register(Settings.integerBuilder("Opacity").withMinimum(0).withValue(50).withMaximum(255).build());
    
    private enum Mode {
        BOX, BOTTOM
    }

    private ConcurrentHashMap<BlockPos, Boolean> safeHoles;

    @Override
    public void onUpdate() {

        if (safeHoles == null) {
            safeHoles = new ConcurrentHashMap<>();
        } else {
            safeHoles.clear();
        }

        int range = (int) Math.ceil(renderDistance.getValue());

        List<BlockPos> blockPosList = getSphere(getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {

            // block gotta be air
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 1 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 2 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean isSafe = true;
            boolean isBedrock = true;

            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block != Blocks.BEDROCK) {
                    isBedrock = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                    break;
                }
            }

            if (isSafe) {
                safeHoles.put(pos, isBedrock);
            }

        }

    }

    @Override
    public void onWorldRender(final RenderEvent event) {

        if (mc.player == null || safeHoles == null) {
            return;
        }

        if (safeHoles.isEmpty()) {
            return;
        }

        KamiTessellator.prepare(GL11.GL_QUADS);

       /* safeHoles.forEach((blockPos, isBedrock) -> {
            if (isBedrock && renBedr.getValue()) {
                drawBox(blockPos, r2.getValue(), g2.getValue(), b2.getValue());
            } else if (renObby.getValue()){
                drawBox(blockPos, r1.getValue(), g1.getValue(), b1.getValue());
            }
        });
        */
          safeHoles.forEach((blockPos, isBedrock) -> {
                drawBox(blockPos, r1.getValue(), g1.getValue(), b1.getValue());
        });

        KamiTessellator.release();

    }

    private void drawBox(BlockPos blockPos, int r, int g, int b) {
        Color color = new Color(r, g, b, a0.getValue());
        if (mode.getValue().equals(Mode.BOTTOM)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.DOWN);
        } else if (mode.getValue().equals(Mode.BOX)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.ALL);
        }
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

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }


}