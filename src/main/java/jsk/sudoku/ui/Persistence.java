package jsk.sudoku.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import jsk.sudoku.model.Board;

public class Persistence extends JFileChooser {

	private static final long serialVersionUID = 210996795910136522L;
	private final SudokuSolver owner;
	public final Action save, load;
	
	public Persistence(SudokuSolver owner) {
		this.owner = owner;
		save = new Save();
		load = new Load();
	}

	private class Save extends AbstractAction {
		private static final long serialVersionUID = -5959567835449623654L;
		private Save() {
			super("Save...");
		}

		public void actionPerformed(ActionEvent event) {
			int returnVal = showSaveDialog(owner);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = getSelectedFile();
				if (file == null) {
					new Alert("WTF", "File was null!?", true, owner);
					return;
				}
				
				try {
					FileOutputStream out = new FileOutputStream(file);
					try {
						owner.getBoard().save(out);
					} finally {
						out.close();
					}
				} catch (IOException e) {
					new Alert("Save Failed", e.getMessage(), false, owner);
				}
			}
		}
	}
	
	private class Load extends AbstractAction {
		private static final long serialVersionUID = 8315818363265320347L;
		
		private Load() {
			super("Open...");
		}

		public void actionPerformed(ActionEvent event) {
			if (showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
				File file = getSelectedFile();
				if (file == null) {
					new Alert("WTF", "File was null!?", true, owner);
					return;
				}
				
				try {
					FileInputStream in = new FileInputStream(file);
					try {
						owner.setBoard(Board.load(in));
					} finally {
						in.close();
					}
				} catch (IOException e) {
					new Alert("Load Failed", e.getMessage(), false, owner);
				}
			}
		}
	}
	
}
