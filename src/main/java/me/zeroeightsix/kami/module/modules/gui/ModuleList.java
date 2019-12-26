package me.zeroeightsix.kami.module.modules.gui;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "ModuleList", category = Module.Category.GUI)
public class ModuleList extends Module{
	
	public Setting<ColorMode> colorMode = register(Settings.e("Mode", ColorMode.STATIC));
	public Setting<Integer> listRed = register(Settings.integerBuilder("ModList Red").withRange(0, 255).withValue(255).withVisibility(o -> colorMode.getValue().equals(ColorMode.STATIC)).build());
	public Setting<Integer> listGreen = register(Settings.integerBuilder("ModList Green").withRange(0, 255).withValue(255).withVisibility(o -> colorMode.getValue().equals(ColorMode.STATIC)).build());
	public Setting<Integer> listBlue = register(Settings.integerBuilder("ModList Blue").withRange(0, 255).withValue(255).withVisibility(o -> colorMode.getValue().equals(ColorMode.STATIC)).build());
	
	 public static enum ColorMode {
	        STATIC, RAINBOW
	   }
	
	public void onEnable() {
		if(mc.player != null) {
		disable();
		}
	}
	
	
}
