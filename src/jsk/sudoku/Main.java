package jsk.sudoku;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import java.util.Properties;

import javax.swing.UnsupportedLookAndFeelException;

import jsk.sudoku.model.Board;
import jsk.sudoku.ui.SudokuSolver;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.jtattoo.plaf.aero.AeroLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;

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

	/*
	private static void setupLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		Properties props = new Properties();

		props.put("logoString", " _ ");
		//props.put("licenseKey", "INSERT YOUR LICENSE KEY HERE");

		props.put("disabledForegroundColor", "255 0 0");
//		props.put("menuSelectionBackgroundColor", "180 240 197");

		props.put("gridColor", "218 23 23");
//		props.put("controlColorLight", "218 254 230");
//		props.put("controlColorDark", "180 240 197");

		props.put("buttonColor", "218 230 254");
		props.put("buttonColorLight", "255 255 255");
		props.put("buttonColorDark", "244 242 232");

//		props.put("rolloverColor", "218 254 230");
//		props.put("rolloverColorLight", "218 254 230");
//		props.put("rolloverColorDark", "180 240 197");

//		props.put("windowTitleForegroundColor", "0 0 0");
//		props.put("windowTitleBackgroundColor", "180 240 197");
//		props.put("windowTitleColorLight", "218 254 230");
//		props.put("windowTitleColorDark", "180 240 197");
//		props.put("windowBorderColor", "218 254 230");

		// set your theme
		AeroLookAndFeel.setCurrentTheme(props);
		// select the Look and Feel
		setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
	}
	*/
	public void run() {
		SudokuSolver window = new SudokuSolver(new Board(BoardType.STANDARD));
		window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		window.setVisible(true);
	}
	
}