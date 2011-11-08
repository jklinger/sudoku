package jsk.sudoku.ui;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import jsk.sudoku.model.Board;
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
			setBoard(board);
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
		setContentPane(new BoardPane(this.board = board, buttons));
		pack();
		return this;
	}
	
	private class GuessAndCheck extends AbstractAction {
		private static final long serialVersionUID = 2630976927940138384L;

		private GuessAndCheck(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				GuessAndCheckSolver guesser = new GuessAndCheckSolver(board);
				while (!guesser.isDone()) {
					Board solution = guesser.awaitNextResult(30, SECONDS);
					if (solution == null)
						continue;
					SudokuSolver solver = new SudokuSolver(solution, buttons);
					solver.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					solver.pack();
					solver.setVisible(true);
				}
			} catch (InterruptedException e) {
				// Too much waiting!
			}
		}
	}
}