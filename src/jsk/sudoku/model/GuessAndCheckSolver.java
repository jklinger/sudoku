package jsk.sudoku.model;

import static java.util.Collections.unmodifiableSet;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

public class GuessAndCheckSolver implements Runnable {
	private final Board originalBoard;
	private final Executor executor;
	private final Set<Board> results = new CopyOnWriteArraySet<Board>();
	private final Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
	
	public static interface Listener {
		public void solved(Board board);
	}
	
	private class RecursiveFuture extends FutureTask<Set<Board>> {
		public RecursiveFuture(Board board) {
			super(board.new GuessAndCheck());
		}

		@Override
		protected void done() {
			super.done();
			
			try {
				for (Board branch : get()) {
					if (branch.isSolved()) {
						boolean newlySolved;
						synchronized(results) {
							newlySolved = results.add(branch);
						}
						if (newlySolved) {
							solved(branch);
						}
					} else {
						executor.execute(new RecursiveFuture(branch));
					}
				}
			} catch (InterruptedException e) {
				// Impossible
			} catch (ExecutionException e) {
				// There is no reason for this to happen,
				// since our Callable doesn't throw Exceptions
			} catch (RejectedExecutionException e) {
				// No more processing, please!
			}
		}
	}
	
	public GuessAndCheckSolver(Board board, Executor executor) {
		this.executor = executor;
		originalBoard = board;
	}
	
	public GuessAndCheckSolver(Board board) {
		this(board, Executors.newCachedThreadPool());
	}
	
	public void run() {
		executor.execute(new RecursiveFuture(originalBoard));
	}
	
	public Set<Board> getResults() {
		return unmodifiableSet(results);
	}
	
	public void registerListener(Listener listener) {
		listeners.add(listener);
	}
	
	private void solved(Board board) {
		for (Listener listener : listeners) {
			listener.solved(board);
		}
	}
}