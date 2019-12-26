package me.zeroeightsix.kami;

import javax.swing.*;

public class InvalidHwidWindow extends JFrame {
	public InvalidHwidWindow() {
		this.setTitle("Apollyon");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		String message = "fuck off \n(invalid hwid)";
		JOptionPane.showMessageDialog(this, message, "Apollyon", JOptionPane.ERROR_MESSAGE);
	}
}	
		