package jsk.sudoku;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import javax.swing.UnsupportedLookAndFeelException;

import jsk.sudoku.model.Board;
import jsk.sudoku.ui.SudokuSolver;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Main implements Runnable {
	
	/**
	 * @param args
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5c %m%n")));
        setupLookAndFeel();
		invokeLater(new Main());
	}
	
	private static void setupLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setLookAndFeel(getSystemLookAndFeelClassName());
	}

	public void run() {
		SudokuSolver window = new SudokuSolver(new Board(BoardType.STANDARD));
		window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		window.setVisible(true);
	}
	
}