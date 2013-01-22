package jsk.sudoku.ui;

import static jsk.sudoku.BoardType.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import jsk.sudoku.BoardType;
import jsk.sudoku.model.Board;

public class BoardCreator extends JMenu {
	private static final long serialVersionUID = -2464429714961208625L;
	private final SudokuSolver owner;

	private class BoardFactory extends AbstractAction {
		private static final long serialVersionUID = -5045325673846699526L;
		private final BoardType type;
		
		private BoardFactory(BoardType boardType, String name) {
			super(name);
			type = boardType;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			owner.setBoard(new Board(type));
		}
		
	}
	
	public BoardCreator(SudokuSolver owner) {
		super("New");
		this.owner = owner;
		add(new BoardFactory(STANDARD, "Standard")).setToolTipText("Classic Sudoku - 9 x 9 grid containing only the numbers 1-9");
		add(new BoardFactory(ZERO_BASED, "Octal")).setToolTipText("Standard but with values 0-8");
		add(new BoardFactory(HEX, "Hexadecimal")).setToolTipText("16 x 16, values 0-F");
		add(new BoardFactory(MINI, "Tiny")).setToolTipText("4 x 4, values A, B, C, D");
	}
	
}
