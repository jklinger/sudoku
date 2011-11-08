package jsk.sudoku.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class Alert extends JDialog {
	private static final long serialVersionUID = 4285921878466393491L;

	private class Closer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}		
	}
	
	private static Alert modalWindow, modelessWindow;
	
	public static Alert show(String title, String text, boolean modal, Frame owner) {
		Alert alert = modal ? modalWindow : modelessWindow;
		if (alert != null && !alert.isVisible()) {
			modify(alert, title, text);
		} else {
			alert = new Alert(title, text, modal, owner);
		}
		return alert;
	}
	
	private static synchronized void modify(Alert alert, String title, String text) {
		alert.setTitle(title);
		alert.label.setText(text);
	}

	private JLabel label;

	public Alert(String title, String text) {
		super((JDialog) null, title);
		setup(text);
	}
	
	public Alert(String title, String text, boolean modal, Frame owner) {
		super(owner, title, modal);
		setup(text);
	}
	
	private void setup(String text) {
		add(label = new JLabel(text));
		JButton ok = new JButton("OK");
		ok.addActionListener(new Closer());
		add(ok);
		pack();
		setVisible(true);
	}
}
