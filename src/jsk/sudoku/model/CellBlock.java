package jsk.sudoku.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CellBlock implements Iterable<Cell>, CellListener {
	
	protected final List<Cell> cells;
	private final boolean[] solved;
	
	public CellBlock(int size) {
		cells = new ArrayList<Cell>(size);
		solved = new boolean[size];
	}
	
	public CellBlock(CellBlock copy) {
		cells = new ArrayList<Cell>(copy.cells);
		solved = copy.solved.clone();
	}

	CellBlock add(Cell cell) {
		cells.add(cell);
		return this;
	}
	
	@Override
	public Iterator<Cell> iterator() {
		return cells.iterator();
	}

	public void solved(Cell solved) {
		int value = solved.getValue();
		this.solved[value] = true;
		for (Cell cell : cells) {
			if (cell != solved)
				cell.eliminate(value);
		}
	}
	
	public boolean isSolved(int value) {
		return solved[value];
	}

	public void cellChanged(Cell changedCell) {
		CHECKING_VALUES: for (int value = 0; value < solved.length; value++) {
			if (isSolved(value))
				continue;
			
			Cell onlyCell = null;
			for (Cell cell : cells) {
				if (cell.couldBe(value)) {
					if (onlyCell == null)
						onlyCell = cell;
					else
						continue CHECKING_VALUES;
				}
			}
			
			if (onlyCell != null)
				onlyCell.solve(value);
		}
	
		// TODO Why doesn't this have any effect?
		//mutexRule();
	}
	
	void mutexRule() {
		Possibilities[] possibilities = new Possibilities[solved.length];
		for (int value = 0; value < solved.length; value++) {
			if (isSolved(value))
				continue;
			
			possibilities[value] = new Possibilities(possibilities.length);
			for (int i = 0; i < cells.size(); i++) {
				Cell cell = cells.get(i);
				if (cell.couldBe(value)) {
					possibilities[value].add(i);
				}
			}
		}
		
		// Figure out which ones are in the same places
		// That's easy, it's just onePossibilities.equals(anotherPossibilities)
		Map<Possibilities, Possibilities> samePlaces = new HashMap<Possibilities, Possibilities>();
		for (Possibilities locations : possibilities) {
			if (locations == null)
				continue;
			
			for (int i = 0; i < solved.length; i++) {
				Possibilities other = possibilities[i];
				if (locations.equals(other)) {
					Possibilities entry = samePlaces.get(locations);
					if (entry == null) {
						entry = new Possibilities(0);
						samePlaces.put(locations, entry);
					}
					entry.add(i);
				}
			}
		}
		
		for (Map.Entry<Possibilities, Possibilities> entry : samePlaces.entrySet()) {
			if (entry.getKey().size() == entry.getValue().size()) {
				// As many locations as values
				for (int location : entry.getKey()) {
					cells.get(location).mustBe(entry.getValue());
				}
			}
		}
	}
	
}
