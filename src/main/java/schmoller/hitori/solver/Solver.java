package schmoller.hitori.solver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;
import schmoller.hitori.NumberState;

public class Solver {
	private final Board board;
	
	private final BoardNumber[] baseSet;
	private final int rows;
	private final int cols;
	private final TabledSet duplicates;
	private final Deque<BoardNumber> search;
	
	public Solver(Board board) {
		this.board = board;
		rows = board.getRows();
		cols = board.getCols();
		
		// Convert the numbers to something we can work with
		baseSet = new BoardNumber[rows * cols];
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				int index = c + r * cols;
				baseSet[index] = board.get(r, c);
			}
		}
		
		// Split out the duplicate containing
		duplicates = buildDuplicateSet();
		search = new ArrayDeque<>(rows * cols);
		
		generateInitialSearchSet();
		
		// DeBUG
		for (BoardNumber dup : search) {
			dup.setState(NumberState.Marked);
		}
	}
	
	private TabledSet buildDuplicateSet() {
		TabledSet duplicates = new TabledSet(rows, cols);
		// Check each row
		for (int r = 0; r < rows; ++r) {
			long map = 0;
			
			// Check this column
			for (int c = 0; c < cols; ++c) {
				BoardNumber compareBase = baseSet[c + r * cols];
				
				// Look at the rest of the row
				for (int c2 = c + 1; c2 < cols; ++c2) {
					// Check if this has been visited before
					if ((map & (1 << c2)) != 0) {
						continue;
					}
					
					BoardNumber number = baseSet[c2 + r * cols];
					// Check for duplicate
					if (number.getValue() == compareBase.getValue()) {
						// Its a duplicate
						// Mark visited
						map |= 1 << c2;
						duplicates.add(compareBase);
						duplicates.add(number);
					}
				}
			}
		}
		
		// Check each column
		for (int c = 0; c < cols; ++c) {
			long map = 0;
			
			// Check this row
			for (int r = 0; r < rows; ++r) {
				BoardNumber compareBase = baseSet[c + r * cols];
				
				// Look at the rest of the row
				for (int r2 = r + 1; r2 < rows; ++r2) {
					// Check if this has been visited before
					if ((map & (1 << r2)) != 0) {
						continue;
					}
					
					BoardNumber number = baseSet[c + r2 * cols];
					// Check for duplicate
					if (number.getValue() == compareBase.getValue()) {
						// Its a duplicate
						// Mark visited
						map |= 1 << r2;
						duplicates.add(compareBase);
						duplicates.add(number);
					}
				}
			}
		}
		
		return duplicates;
	}
	
	private Set<BoardNumber> getNeighbours(BoardNumber root, boolean duplicatesOnly) {
		int row = root.getIndex() / cols;
		int col = root.getIndex() % cols;
		
		Set<BoardNumber> neighbours = new HashSet<>();
		if (row > 0) {
			neighbours.add(baseSet[col + (row - 1) * cols]);
		}
		
		if (col > 0) {
			neighbours.add(baseSet[(col - 1) + row * cols]);
		}
		
		if (row < rows - 1) {
			neighbours.add(baseSet[col + (row + 1) * cols]);
		}
		
		if (col < cols - 1) {
			neighbours.add(baseSet[(col + 1) + row * cols]);
		}
		
		// TODO: Just dont add it in the first place
		if (duplicatesOnly) {
			neighbours.removeIf(n -> !duplicates.contains(n));
		}
		return neighbours;
	}
	
	private void generateInitialSearchSet() {
		// Check each row
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				BoardNumber duplicateBase = duplicates.get(row, col);
				if (duplicateBase == null) {
					continue;
				}
				
				Set<BoardNumber> neighboursBase = getNeighbours(duplicateBase, true);
				
				// Check for others of same value
				for (int col2 = col + 1; col2 < cols; ++col2) {
					BoardNumber duplicate = duplicates.get(row, col2);
					if (duplicate == null) {
						continue;
					}
					
					if (duplicateBase.getValue() == duplicate.getValue()) {
						// They are the same value, check for definite values
						Set<BoardNumber> neighbours = getNeighbours(duplicate, true);
						neighbours.retainAll(neighboursBase);
						
						// Any remaining must be there as one of these duplicate values must be shaded
						if (!neighbours.isEmpty()) {
							search.addAll(neighbours);
						}
					}
				}
				
			}
		}
		
		// Check each col
		for (int col = 0; col < cols; ++col) {
			for (int row = 0; row < rows; ++row) {
				BoardNumber duplicateBase = duplicates.get(row, col);
				if (duplicateBase == null) {
					continue;
				}
				
				Set<BoardNumber> neighboursBase = getNeighbours(duplicateBase, true);
				
				// Check for others of same value
				for (int row2 = row + 1; row2 < rows; ++row2) {
					BoardNumber duplicate = duplicates.get(row2, col);
					if (duplicate == null) {
						continue;
					}
					
					if (duplicateBase.getValue() == duplicate.getValue()) {
						// They are the same value, check for definite values
						Set<BoardNumber> neighbours = getNeighbours(duplicate, true);
						neighbours.retainAll(neighboursBase);
						
						// Any remaining must be there as one of these duplicate values must be shaded
						if (!neighbours.isEmpty()) {
							search.addAll(neighbours);
						}
					}
				}
			}
		}
	}
	
	public void step() {
		if (!search.isEmpty()) {
			// Solve this one
			solve(search.pop());
		} else {
			// Check for those that would make the board discontinuous
			for (BoardNumber number : duplicates) {
				if (doesBreakContinuity(number)) {
					solve(number);
					return;
				}
			}
			
			// TODO: Lookahead based searching
			System.out.println("Out of moves");
		}
	}
	
	private void solve(BoardNumber root) {
		// Find the same numbers in the same row and column and mark them as shaded
		int row = root.getIndex() / cols;
		int col = root.getIndex() % cols;
		
		System.out.println("Removing " + root + " from duplicates");
		duplicates.remove(root);
		
		// Check row
		for (int i = 0; i < cols; ++i) {
			BoardNumber other = duplicates.get(row, i);
			if (other == null || other == root) {
				continue;
			}
			
			if (root.getValue() == other.getValue()) {
				shade(other);
			}
		}
		
		// Check column
		for (int i = 0; i < rows; ++i) {
			BoardNumber other = duplicates.get(i, col);
			if (other == null || other == root) {
				continue;
			}
			
			if (root.getValue() == other.getValue()) {
				shade(other);
			}
		}
	}
	
	private boolean doesBreakContinuity(BoardNumber number) {
		System.out.println("Testing continuity " + number);
		FloodFill fill = new FloodFill(board, number, getNeighbours(number, false));
		if (fill.flood()) {
			System.out.println(number + " is ok");
			return false;
		} else {
			System.out.println(number + " breaks continuity");
			return true;
		}
	}
	
	private void shade(BoardNumber number) {
		number.setState(NumberState.Shaded);
		duplicates.remove(number);
		System.out.println("Shading " + number + " and removing from duplicates");
		
		Set<BoardNumber> neighbours = getNeighbours(number, true);
		for (BoardNumber neighbour : neighbours) {
			if (!search.contains(neighbour)) {
				search.push(neighbour);
				System.out.println("Added " + neighbour + " to queue: " + search);
			}
		}
	}
}
