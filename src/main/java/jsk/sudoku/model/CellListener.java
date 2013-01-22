package jsk.sudoku.model;

public interface CellListener {

	public void cellChanged(Cell cell);
	public void solved(Cell cell);
}
