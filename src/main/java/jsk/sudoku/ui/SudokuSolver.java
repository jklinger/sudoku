package jsk.sudoku.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import jsk.sudoku.model.Board;
import jsk.sudoku.model.Cell;
import jsk.sudoku.model.GuessAndCheckSolver;

public class SudokuSolver extends JFrame {
	private class SetButtons implements ActionListener {
		private final boolean value;
		private SetButtons(boolean value) {
			this.value = value;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			buttons = value;
			setBoard(board, true);
		}

	}

	private static final long serialVersionUID = -4181180012395823097L;
	
	private Board board;

	private boolean buttons;
	
	public SudokuSolver(final Board board) {
		this(board, false);
	}
	
	SudokuSolver(final Board board, boolean buttons) {
		super("Sudoku Solver");
		this.board = board;
		this.buttons = buttons;
		populate();
	}
	
	private void populate() {
		Persistence persistence = new Persistence(this);
		
		JMenu boardMenu = new JMenu("Board");
		
		boardMenu.add(new BoardCreator(this));
		
		boardMenu.add(persistence.load);
		boardMenu.add(persistence.save);
		
		boardMenu.add(history);
		
		boardMenu.add(new GuessAndCheck("Check it"));
		
		JMenu viewMenu = new JMenu("Interface");
		JRadioButtonMenuItem
			b1 = new JRadioButtonMenuItem("Classic", !buttons),
			b2 = new JRadioButtonMenuItem("Advanced", buttons);
		viewMenu.add(b1).addActionListener(new SetButtons(false));
		viewMenu.add(b2).addActionListener(new SetButtons(true));
		ButtonGroup group = new ButtonGroup();
		group.add(b1);
		group.add(b2);
		
		JMenuBar bar = new JMenuBar();
		bar.add(boardMenu);
		bar.add(viewMenu);
		bar.add(new ThemeSwitcher("Themes").addTarget(this).addTarget(persistence));
		setJMenuBar(bar);
		setBoard(board);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public SudokuSolver setBoard(Board board) {
		setBoard(board, false);
		return this;
	}
	
	
	// TODO refactor all this into an actual controller class
	private final History history = new History("History", this);
	protected void solve(Cell cell, int value) {
		// FIXME do this on a different thread!
		history.record(board);
		try {
			cell.solve(value);
		} catch (Exception e) {
			Alert.show("Error", "The value " + getBoard().type.format(value) + " caused some cell to become invalid. This action has been undone.", false, this);
			history.undo();
		}
	}
	
	protected void setBoard(Board board, boolean retainHistory) {
		setContentPane(new BoardPane(this, this.board = board, buttons));
		pack();
		if (!retainHistory) {
			history.clear();
		}
	}
	
	private class GuessAndCheck extends AbstractAction implements GuessAndCheckSolver.Listener {
		private static final long serialVersionUID = 2630976927940138384L;

		private GuessAndCheck(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			GuessAndCheckSolver guesser = new GuessAndCheckSolver(board);
			guesser.registerListener(this);
			SwingUtilities.invokeLater(guesser);
		}

		public void solved(Board solution) {
			SudokuSolver solver = new SudokuSolver(solution, buttons);
			solver.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			solver.pack();
			solver.setVisible(true);
		}
	}
}