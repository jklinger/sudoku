package jsk.sudoku;

import java.text.ParseException;

public enum BoardType {
	STANDARD(3) {
		public final int parseInternal(String input) throws ParseException {
			int result = Integer.parseInt(input) - 1;
			if (result > 9) {
				throw new ParseException(input, 0);
			}
			return result;
		}
		public final String format(int value) {
			return Integer.toString(value + 1);
		}
	},
	MINI(2) {
		public final int parseInternal(String input) throws ParseException {
			if (input.length() == 0) {
				throw new ParseException(input, 0);
			}
			
			switch (input.charAt(0)) {
			case 'a':
			case 'A':
				return 0;
			case 'b':
			case 'B':
				return 1;
			case 'c':
			case 'C':
				return 2;
			case 'd':
			case 'D':
				return 3;
			default:
				throw new ParseException(input, 0);
			}
		}
		
		public final String format(int value) {
			switch (value) {
			case 0: return "A";
			case 1: return "B";
			case 2: return "C";
			case 3: return "D";
			default: return "?";
			}
		}
	},
	ZERO_BASED(3) {
		public final int parseInternal(String input) throws ParseException {
			return Integer.parseInt(input, 10);
		}
		public final String format(int value) {
			return Integer.toString(value);
		}
	},
	HEX(4) {
		public final int parseInternal(String input) throws ParseException {
			return Integer.parseInt(input, 16);
		}
		public final String format(int value) {
			return Integer.toHexString(value).toUpperCase();
		}
	};
	
	public final int baseDimension;
	public final int size;
	
	private BoardType(int baseDimension) {
		this.baseDimension = baseDimension;
		this.size = baseDimension * baseDimension;
	}
	
	abstract int parseInternal(String input) throws ParseException;
	public final int parse(String input) throws ParseException {
		if (input == null || input.length() != 1) {
			throw new ParseException(input, 0);
		}
		
		try {
			return parseInternal(input);
		} catch (NumberFormatException e) {
			throw new ParseException(input, 0);
		}
	}

	public abstract String format(int value);
}
