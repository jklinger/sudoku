package jsk.sudoku.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Possibilities implements Iterable<Integer> {
	
	/**
	 * This bitmask is inspired by the implementation of EnumSet
	 */
	private int elements;

	public Possibilities(final Possibilities copy) {
		elements = copy.elements;
	}
	
	public Possibilities(final int count) {
		elements = -1 >>> -count;
	}
	
	public boolean contains(int value) {
		return (elements & (1 << value)) != 0;
	}
	
	public boolean add(final int value) {
		int oldElements = elements;
		elements |= (1 << value);
		return oldElements != elements;
	}

	public boolean remove(final int value) {
		int oldElements = elements;
		elements &= ~(1 << value);
		return oldElements != elements;
	}
	
	public boolean retainAll(Possibilities possibilities) {
		int oldElements = elements;
		elements &= possibilities.elements;
		return oldElements != elements;
	}
	
	public int size() {
		return Integer.bitCount(elements);
	}
	
	public void is(int value) {
		elements = 1 << value;
	}
	
	public int value() {
		if (size() != 1)
			throw new IllegalStateException();
		
		return Integer.numberOfTrailingZeros(elements);
	}
	
	public void clear() {
		elements = 0;
	}
	
	public String toString() {
		int size = size();
		if (size == 1)
			return '<' + Integer.toString(value()) + '>';
		
		StringBuilder sb = new StringBuilder(size * 2);
		for (Integer value : this)
			sb.append(' ').append(value.intValue());
		return sb.substring(1);
	}
	
	public int hashCode() {
		return elements;
	}
	
	public boolean equals(Object o) {
		return o instanceof Possibilities && ((Possibilities) o).elements == elements;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new ValueIterator();
	}
	
	/**
	 * Adapted from {@link java.util.RegularEnumSet.EnumSetIterator}
	 */
	private class ValueIterator implements Iterator<Integer> {
		/**
		 * A bit vector representing the elements in the set not yet returned by
		 * this iterator.
		 */
		int unseen;

		/**
		 * The bit representing the last element returned by this iterator but
		 * not removed, or zero if no such element exists.
		 */
		int lastReturned = 0;

		ValueIterator() {
			unseen = elements;
		}

		public boolean hasNext() {
			return unseen != 0;
		}

		public Integer next() {
			if (unseen == 0)
				throw new NoSuchElementException();
			
			lastReturned = unseen & -unseen;
			unseen -= lastReturned;
			return Integer.numberOfTrailingZeros(lastReturned);
		}

		public void remove() {
			if (lastReturned == 0)
				throw new IllegalStateException();
			elements -= lastReturned;
			lastReturned = 0;
		}
	}

}
