package jsk.sudoku.model;

import static java.util.Collections.unmodifiableSet;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class GuessAndCheckSolver {
	private final Executor executor;
	private final Set<Board> results = new CopyOnWriteArraySet<Board>();
	private final BlockingQueue<Board> resultBuffer = new LinkedBlockingQueue<Board>();
	private final ReentrantLock lock = new ReentrantLock();
	
	private boolean done;
	
	public final Condition completion = lock.newCondition();
	
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
						if (results.add(branch)) {
							resultBuffer.add(branch);
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
		executor.execute(new RecursiveFuture(board) {
			protected void done() {
				super.done();
				
				lock.lock();
				try {
					done = true;
					completion.signalAll();
				} finally {
					lock.unlock();
				}
			}
		});
	}
	
	public GuessAndCheckSolver(Board board) {
		this(board, Executors.newCachedThreadPool());
	}
	
	public Board awaitNextResult() throws InterruptedException {
		return resultBuffer.take();
	}
	
	public Board awaitNextResult(long timeout, TimeUnit unit) throws InterruptedException {
		return resultBuffer.poll(timeout, unit);
	}
	
	public Set<Board> getResults() {
		return unmodifiableSet(results);
	}
	
	public boolean isDone() {
		return done;
	}
}