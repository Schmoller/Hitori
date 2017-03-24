package schmoller.hitori.solver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schmoller.hitori.Board;
import schmoller.hitori.NumberState;
import schmoller.hitori.Board.BoardNumber;

public class FloodFill {
	private final Board board;
	private final Set<BoardNumber> unshaded;
	
	private Deque<BoardNumber> queue;
	private Set<BoardNumber> visited;
	
	public FloodFill(Board board) {
		this.board = board;
		
		unshaded = new HashSet<>();
		generateUnshadedSet();
	}
	
	private void generateUnshadedSet() {
		for (int row = 0; row < board.getRows(); ++row) {
			for (int col = 0; col < board.getCols(); ++col) {
				BoardNumber number = board.get(row, col);
				if (number.getState() != NumberState.Shaded) {
					unshaded.add(number);
				}
			}
		}
	}
	
	public boolean flood() {
		queue = new ArrayDeque<>();
		visited = new HashSet<>();
		
		// Just get one so we can start the fill
		Iterator<BoardNumber> it = unshaded.iterator();
		queue.add(it.next());
		
		// Visit all until we cant find any more
		while (!queue.isEmpty()) {
			BoardNumber number = queue.pop();
			unshaded.remove(number);
			addNeighbours(number);
		}
		
		// If any remain in unshaded, we know that the board is discontinuous
		return unshaded.isEmpty();
	}
	
	private void addNeighbours(BoardNumber number) {
		int row = number.getIndex() / board.getCols();
		int col = number.getIndex() % board.getCols();
		
		if (row > 0) {
			BoardNumber next = board.get(row-1, col);
			if (next.getState() != NumberState.Shaded) {
				pushNeighbour(next);
			}
		}
		
		if (col > 0) {
			BoardNumber next = board.get(row, col-1);
			if (next.getState() != NumberState.Shaded) {
				pushNeighbour(next);
			}
		}
		
		if (row < board.getRows() - 1) {
			BoardNumber next = board.get(row+1, col);
			if (next.getState() != NumberState.Shaded) {
				pushNeighbour(next);
			}
		}
		
		if (col < board.getCols() - 1) {
			BoardNumber next = board.get(row, col+1);
			if (next.getState() != NumberState.Shaded) {
				pushNeighbour(next);
			}
		}
	}
	
	private void pushNeighbour(BoardNumber number) {
		if (visited.add(number)) {
			queue.push(number);
		}
	}
}
