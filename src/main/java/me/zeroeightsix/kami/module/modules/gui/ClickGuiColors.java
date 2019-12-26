package me.zeroeightsix.kami.module.modules.gui;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "ClickGuiColors", category = Module.Category.GUI)
public class ClickGuiColors extends Module{

	public Setting<Float> fillRed = register(Settings.floatBuilder("Fill Red").withRange(.00f, 1f).withValue(.10f).build());
	public Setting<Float> fillGreen = register(Settings.floatBuilder("Fill Green").withRange(.00f, 1f).withValue(.10f).build());
	public Setting<Float> fillBlue = register(Settings.floatBuilder("Fill Blue").withRange(.00f, 1f).withValue(.10f).build());

	public Setting<Float> lineRed = register(Settings.floatBuilder("Outline Red").withRange(.00f, 1f).withValue(.50f).build());
	public Setting<Float> lineGreen = register(Settings.floatBuilder("Outline Green").withRange(.00f, 1f).withValue(.10f).build());
	public Setting<Float> lineBlue = register(Settings.floatBuilder("Outline Blue").withRange(.00f, 1f).withValue(.50f).build());
	
	public void onEnable() {
		if(mc.player != null) {
		disable();
		}
	}
	
	
}
