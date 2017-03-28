package schmoller.hitori.solver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;

public class FloodFill {
	private final Board board;
	private final Set<BoardNumber> unshaded;
	
	private Set<BoardNumber> targets;
	private BoardNumber from;
	
	private Deque<BoardNumber> queue;
	private Set<BoardNumber> visited;
	
	public FloodFill(Board board) {
		this.board = board;
		
		unshaded = new HashSet<>();
		generateUnshadedSet();
	}
	
	public FloodFill(Board board, BoardNumber from, Set<BoardNumber> targets) {
		this(board);
		
		this.from = from;
		this.targets = targets;
	}
	
	private void generateUnshadedSet() {
		for (int row = 0; row < board.getRows(); ++row) {
			for (int col = 0; col < board.getCols(); ++col) {
				BoardNumber number = board.get(row, col);
				if (number.getState().isClear()) {
					unshaded.add(number);
				}
			}
		}
	}
	
	/**
	 * Performs a flood fill.
	 * Depending on whether an origin and targets were provided or not
	 * will change the result of this method.
	 * @return
	 * 		if the origin and targets are provided, the flood will start from one 
	 * 		of them and return true only if all targets were visited
	 * 		<p>
	 * 		if the origin was not set, then this will return true if
	 * 		all unshaded spaces are visited
	 */
	public boolean flood() {
		queue = new ArrayDeque<>();
		visited = new HashSet<>();
		
		// Just get one so we can start the fill
		Iterator<BoardNumber> it;
		if (from != null) {
			it = targets.iterator();
		} else {
			it = unshaded.iterator();
		}
		
		queue.add(it.next());
		
		// Visit all until we cant find any more
		while (!queue.isEmpty()) {
			BoardNumber number = queue.pop();
			unshaded.remove(number);
			addNeighbours(number);
		}
		
		if (from != null) {
			return targets.isEmpty();
		} else {
			// If any remain in unshaded, we know that the board is discontinuous
			return unshaded.isEmpty();
		}
	}
	
	private void addNeighbours(BoardNumber number) {
		int row = number.getIndex() / board.getCols();
		int col = number.getIndex() % board.getCols();
		
		if (row > 0) {
			BoardNumber next = board.get(row-1, col);
			if (next.getState().isClear()) {
				pushNeighbour(next);
			}
		}
		
		if (col > 0) {
			BoardNumber next = board.get(row, col-1);
			if (next.getState().isClear()) {
				pushNeighbour(next);
			}
		}
		
		if (row < board.getRows() - 1) {
			BoardNumber next = board.get(row+1, col);
			if (next.getState().isClear()) {
				pushNeighbour(next);
			}
		}
		
		if (col < board.getCols() - 1) {
			BoardNumber next = board.get(row, col+1);
			if (next.getState().isClear()) {
				pushNeighbour(next);
			}
		}
	}
	
	private void pushNeighbour(BoardNumber number) {
		if (number != from && visited.add(number)) {
			if (targets != null) {
				targets.remove(number);
			}
			queue.push(number);
		}
	}
}
