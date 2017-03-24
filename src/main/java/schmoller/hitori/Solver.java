package schmoller.hitori;

import java.util.HashSet;
import java.util.Set;

import schmoller.hitori.Board.BoardNumber;

public class Solver {
	private final IndexedNumber[] baseSet;
	private final int rows;
	private final int cols;
	private final Set<IndexedNumber> duplicates;
	private final Set<IndexedNumber> search;
	
	public Solver(Board board) {
		rows = board.getRows();
		cols = board.getCols();
		
		// Convert the numbers to something we can work with
		baseSet = new IndexedNumber[rows * cols];
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				int index = c + r * cols;
				baseSet[index] = new IndexedNumber(board.get(r, c), index);
			}
		}
		
		// Split out the duplicate containing
		duplicates = buildDuplicateSet();
		search = new HashSet<>();
		
		generateInitialSearchSet();
		
		// DeBUG
		for (IndexedNumber dup : search) {
			dup.getNumber().setState(NumberState.Marked);
		}
	}
	
	private Set<IndexedNumber> buildDuplicateSet() {
		Set<IndexedNumber> duplicates = new HashSet<>(baseSet.length);
		// Check each row
		for (int r = 0; r < rows; ++r) {
			long map = 0;
			
			// Check this column
			for (int c = 0; c < cols; ++c) {
				IndexedNumber compareBase = baseSet[c + r * cols];
				
				// Look at the rest of the row
				for (int c2 = c + 1; c2 < cols; ++c2) {
					// Check if this has been visited before
					if ((map & (1 << c2)) != 0) {
						continue;
					}
					
					IndexedNumber number = baseSet[c2 + r * cols];
					// Check for duplicate
					if (number.getNumber().getValue() == compareBase.getNumber().getValue()) {
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
				IndexedNumber compareBase = baseSet[c + r * cols];
				
				// Look at the rest of the row
				for (int r2 = r + 1; r2 < rows; ++r2) {
					// Check if this has been visited before
					if ((map & (1 << r2)) != 0) {
						continue;
					}
					
					IndexedNumber number = baseSet[c + r2 * cols];
					// Check for duplicate
					if (number.getNumber().getValue() == compareBase.getNumber().getValue()) {
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
	
	private Set<IndexedNumber> getNeighbours(IndexedNumber root) {
		int row = root.getIndex() / cols;
		int col = root.getIndex() % cols;
		
		Set<IndexedNumber> neighbours = new HashSet<>();
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
		neighbours.removeIf(n -> !duplicates.contains(n));
		return neighbours;
	}
	
	private void generateInitialSearchSet() {
		IndexedNumber[] duplicateArray = new IndexedNumber[baseSet.length];
		
		for (IndexedNumber duplicate : duplicates) {
			duplicateArray[duplicate.getIndex()] = duplicate;
		}
		
		// Check each row
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				IndexedNumber duplicateBase = duplicateArray[col + row * cols];
				if (duplicateBase == null) {
					continue;
				}
				
				Set<IndexedNumber> neighboursBase = getNeighbours(duplicateBase);
				
				// Check for others of same value
				for (int col2 = col + 1; col2 < cols; ++col2) {
					IndexedNumber duplicate = duplicateArray[col2 + row * cols];
					if (duplicate == null) {
						continue;
					}
					
					if (duplicateBase.getNumber().getValue() == duplicate.getNumber().getValue()) {
						// They are the same value, check for definite values
						Set<IndexedNumber> neighbours = getNeighbours(duplicate);
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
				IndexedNumber duplicateBase = duplicateArray[col + row * cols];
				if (duplicateBase == null) {
					continue;
				}
				
				Set<IndexedNumber> neighboursBase = getNeighbours(duplicateBase);
				
				// Check for others of same value
				for (int row2 = row + 1; row2 < rows; ++row2) {
					IndexedNumber duplicate = duplicateArray[col + row2 * cols];
					if (duplicate == null) {
						continue;
					}
					
					if (duplicateBase.getNumber().getValue() == duplicate.getNumber().getValue()) {
						// They are the same value, check for definite values
						Set<IndexedNumber> neighbours = getNeighbours(duplicate);
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
	
	private void solve(IndexedNumber start) {
		
	}
	
	private static class IndexedNumber {
		private final BoardNumber number;
		private final int index;
		
		public IndexedNumber(BoardNumber number, int index) {
			this.number = number;
			this.index = index;
		}
		
		public BoardNumber getNumber() {
			return number;
		}
		
		public int getIndex() {
			return index;
		}

		@Override
		public String toString() {
			return "[" + number + "@" + index + "]";
		}
	}
}
