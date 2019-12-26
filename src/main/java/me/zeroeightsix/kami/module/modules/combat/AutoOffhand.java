package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Info(name = "AutoOffhand", category = Module.Category.COMBAT)
public class AutoOffhand extends Module{
    int totems;
    int crystals;
    int gapples;
    boolean moving = false;
    boolean returnI = false;
    private Setting<Mode> mode = register(Settings.e("Mode", Mode.TOTEM));
    private Setting<Boolean> soft = register(Settings.b("Soft", true));
    private Setting<Boolean> gui = register(Settings.b("in GUIs", false));

    @Override
    public void onUpdate() {
        if(!gui.getValue() && (mc.currentScreen instanceof GuiContainer)) return;

        if (returnI) {
            int t = -1;
            for (int i = 0; i < 45; i++)
                if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            if (t == -1) return;
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            returnI = false;
        }
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        gapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) crystals += mc.player.getHeldItemOffhand().getCount();
        if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) gapples += mc.player.getHeldItemOffhand().getCount();
        else {
            if (soft.getValue() && !mc.player.getHeldItemOffhand().isEmpty) return;
            if (moving) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                moving = false;
                if (!mc.player.inventory.itemStack.isEmpty()) returnI = true;
                return;
            }
            if (mc.player.inventory.itemStack.isEmpty()) {
                if(mode.getValue().equals(Mode.TOTEM) && totems == 0) return;
                if(mode.getValue().equals(Mode.CRYSTAL) && crystals == 0) return;
                if(mode.getValue().equals(Mode.GAPPLE) && crystals == 0) return;
                int t = -1;
                if(mode.getValue().equals(Mode.TOTEM)) {
                    for (int i = 0; i < 45; i++)
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                            t = i;
                            break;
                        }
                }
                if(mode.getValue().equals(Mode.CRYSTAL)) {
                    for (int i = 0; i < 45; i++)
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                            t = i;
                            break;
                        }
                }
                if(mode.getValue().equals(Mode.GAPPLE)) {
                    for (int i = 0; i < 45; i++)
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE) {
                            t = i;
                            break;
                        }
                }
                if (t == -1) return; // Should never happen!
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
                moving = true;
            } else if (!soft.getValue()) {
                int t = -1;
                for (int i = 0; i < 45; i++)
                    if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                        t = i;
                        break;
                    }
                if (t == -1) return;
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    @Override
    public String getHudInfo() {
        String t = "";
        switch(mode.getValue()) {
            case TOTEM:
                 t =  "T " + totems;
                 break;
            case CRYSTAL:
                 t =  "C " + crystals;
                 break;
            case GAPPLE:
                t =  "G " + gapples;
                break;
        }
        return t;
    }

    private enum Mode {
        TOTEM,
        CRYSTAL,
        GAPPLE
    }
}
