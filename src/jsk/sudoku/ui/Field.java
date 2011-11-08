package jsk.sudoku.ui;

import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import jsk.sudoku.BoardType;
import jsk.sudoku.model.Cell;
import jsk.sudoku.model.CellListener;

public class Field extends JTextField implements CellListener {
	private static final long serialVersionUID = 4399655095180580380L;

	public Field(Cell cell, BoardType boardType, int maxLength) {
		super();
		setDocument(new DocModel(cell, boardType, maxLength));
		setHorizontalAlignment(JTextField.CENTER);
	}
	
	public void solved(Cell cell) {
		((DocModel) getDocument()).solve(cell.getValue());
		setEnabled(false);
	}
	
	class DocModel extends PlainDocument {
		private static final long serialVersionUID = -3690364091744366812L;
		
		private final Cell cell;
		private final BoardType boardType;
		private final int maxLength;

		DocModel(Cell cell, BoardType boardType, int maxLength) {
			super();
			this.cell = cell;
			this.boardType = boardType;
			this.maxLength = maxLength;
		}
		
		private void solve(int value) {
			try {
				super.insertString(0, boardType.format(value), getAttributeContext().getEmptySet());
			} catch (BadLocationException unlikely) {
				unlikely.printStackTrace();
			}
		}

		@Override
		public void insertString(int offset, String string, AttributeSet a) throws BadLocationException {
			if (string == null)
				return;
			
			if (offset >= maxLength)
				return;
			
			if (offset + string.length() > maxLength)
				string = string.substring(0, maxLength - offset);
			
			try {
				cell.solve(boardType.parse(string));
			} catch (Exception e) {
				new Alert("BAD", "NO");
				return;
			}
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			// TODO This
//			super.remove(offs, len);
		}

		@Override
		public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			// Dispatch to one or both of the input handlers
	        if (length > 0) {
	            remove(offset, length);
	        }
	        if (text != null && text.length() > 0) {
	            insertString(offset, text, attrs);
	        }
		}
		
	}

	@Override
	public void cellChanged(Cell cell) {
		// TODO Care about this
		
	}
	
	@Override
	public Dimension getPreferredSize() {
		int em = getGraphics().getFontMetrics().getHeight();
		int size = 3*em/2;
		return new Dimension(size, size);
	}

}
