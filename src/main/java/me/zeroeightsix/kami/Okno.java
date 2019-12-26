package me.zeroeightsix.kami;

import javax.swing.*;

public class Okno extends JFrame {
	public Okno() {
		this.setTitle("Apollyon");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		String message = "Apollyon Initialized - Made by FINZ0";
		JOptionPane.showMessageDialog(this, message, "Apollyon", JOptionPane.PLAIN_MESSAGE);
	}
}	
		