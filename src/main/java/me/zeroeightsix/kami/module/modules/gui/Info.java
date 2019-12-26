package me.zeroeightsix.kami.module.modules.gui;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "Info", category = Module.Category.GUI)
public class Info extends Module{
	
	public Setting<Boolean> watermark = register(Settings.b("Watermark", true));
	public Setting<Boolean> welcomer = register(Settings.b("Welcomer", false));
	public Setting<Boolean> tps = register(Settings.b("TPS", true));
	public Setting<Boolean> fps = register(Settings.b("FPS", true));
	public Setting<Boolean> ping = register(Settings.b("Ping", true));
	public Setting<ColorEnum> colorEnum = register(Settings.e("Color", ColorEnum.WHITE));
	
	 public static enum ColorEnum {
	        WHITE, GRAY, BLUE, RED, GREEN, YELLOW, PURPLE
	    }
	
	public void onEnable() {
		if(mc.player != null) {
		disable();
		}
	}
	
	
}
