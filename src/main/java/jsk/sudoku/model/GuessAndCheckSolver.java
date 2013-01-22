package jsk.sudoku.model;

import static java.util.Collections.unmodifiableSet;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	public GuessAndCheckSolver(Board board) {
		executor = new ThreadPoolExecutor(
				2, // At least 2 threads
				board.type.size ^ 2, // No more threads than cells
				15, SECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadFactory() {
					private final ThreadGroup solverGroup = new ThreadGroup("Guess-and-Check Solvers");
					private final AtomicInteger threadNumber = new AtomicInteger(0);
					@Override
					public Thread newThread(Runnable task) {
						Thread thread = new Thread(solverGroup, task, "Solver-" + threadNumber.getAndIncrement());
						thread.setDaemon(true); // TODO Look into why the current thread isn't already a daemon
						return thread;
					}
				}
		);
		originalBoard = board;
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