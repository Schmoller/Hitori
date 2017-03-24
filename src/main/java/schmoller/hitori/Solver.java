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
		
		// DeBUG
		for (IndexedNumber dup : duplicates) {
			dup.getNumber().setState(NumberState.Marked);
			Set<IndexedNumber> neighbours = getNeighbours(dup);
			if (!neighbours.isEmpty()) {
				System.out.println("Neighbours of " + dup + ":");
				System.out.println(neighbours);
			}
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
		
		if (cols < cols - 1) {
			neighbours.add(baseSet[(col + 1) + row * cols]);
		}
		
		// TODO: Just dont add it in the first place
		neighbours.removeIf(n -> !duplicates.contains(n));
		return neighbours;
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
