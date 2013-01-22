package jsk.sudoku.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class ThemeSwitcher extends JMenu {

	private static final long serialVersionUID = 125881251613515160L;
	
	private final Collection<Component> targets = new ArrayList<Component>();
	
	private class GuiReplace extends AbstractAction {
		private static final long serialVersionUID = -3810325196330286372L;
		private final LookAndFeelInfo gui;
		
		GuiReplace(LookAndFeelInfo gui) {
			super(gui.getName());
			this.gui = gui;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				UIManager.setLookAndFeel(gui.getClassName());
			} catch (Exception e) {
				setEnabled(false);
				return;
			}
			
			for (Component target : targets) {
				SwingUtilities.updateComponentTreeUI(target);
				target.setSize(target.getPreferredSize());
			}
		}
	}
	
	public ThemeSwitcher(String name) {
		super(name);
		for (LookAndFeelInfo gui : UIManager.getInstalledLookAndFeels()) {
			add(new GuiReplace(gui));
		}
	}

	public ThemeSwitcher addTarget(Component target) {
		targets.add(target);
		return this;
	}
}
