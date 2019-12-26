package me.zeroeightsix.kami.module.modules.gui;

import me.zeroeightsix.kami.module.Module;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.gui.kami.component.ActiveModules;
import me.zeroeightsix.kami.gui.rgui.component.AlignedComponent;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.*;
import me.zeroeightsix.kami.module.modules.gui.ModuleList.ColorMode;
import me.zeroeightsix.kami.util.Wrapper;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.*;

//@Module.Info(name = "SwingList", category = Module.Category.GUI)
public class SwingList extends Module{
/*
	public void onEnable() {
		createWindow();
	}

	public void onDisable(){
	}

	private List<Module> mods = ModuleManager.getModules().stream()
			.filter(Module::isEnabled)
			.filter(Module::isDrawn)
			//.sorted(Comparator.comparing(module -> .getStringWidth(module.getName()+(module.getHudInfo()==null?"":module.getHudInfo()+" "))*(component.sort_up?-1:1)))
			.collect(Collectors.toList());

	private void createWindow() {

		JFrame frame = new JFrame("Module List");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		//textLabel.setPreferredSize(new Dimension(300, 100));
		//JLabel textLabel = new JLabel(module.getName() + (module.getHudInfo() == null ? "" : module.getHudInfo() + "\n"), SwingConstants.LEFT);
		JLabel textLabel = new JLabel("", SwingConstants.LEFT);
		mods.stream().forEach(module -> {
			textLabel.setText(textLabel.getText() + "ewfkwesfvgewsgve" + "\n");
			frame.getContentPane().add(textLabel, BorderLayout.CENTER);
		});
		frame.pack();
		frame.setVisible(true);
	}
*/
}
