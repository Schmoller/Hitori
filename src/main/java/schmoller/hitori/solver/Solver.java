package schmoller.hitori.solver;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;
import schmoller.hitori.NumberState;

public class Solver {
	private final Board board;
	
	private final int rows;
	private final int cols;
	private final TabledSet duplicates;
	private final Deque<BoardNumber> search;
	
    private boolean isDone;
    private boolean isUnsolvable;
    
	// Advanced solving 
	private boolean isLookingAhead;
	private BoardNumber lookAheadStart;
	private final Set<BoardNumber> unsure; // Numbers where it did a forward search but got an inconclusive result (board not complete and not conflics)
	private final Set<BoardNumber> changed; // The numbers that have been changed in this lookahead
	
	public Solver(Board board) {
		this.board = board;
		rows = board.getRows();
		cols = board.getCols();
		
		unsure = new HashSet<>();
		changed = new HashSet<>();
		isLookingAhead = false;
		
		// Split out the duplicate containing
		duplicates = buildDuplicateSet();
		search = new ArrayDeque<>(rows * cols);
        
        isDone = false;
		
		generateInitialSearchSet();
	}
    
    public boolean isDone() {
        return isDone;
    }
    
    public boolean isUnsolvable() {
        return isUnsolvable;
    }
    
    private void findDuplicates(TabledSet duplicates, List<BoardNumber> line) {
        long map = 0;
        
        for (int i = 0; i < line.size(); ++i) {
				BoardNumber compareBase = line.get(i);
				
				// Look at the rest of the line
				for (int j = i + 1; j < line.size(); ++j) {
					// Check if this has been visited before
					if ((map & (1 << j)) != 0) {
						continue;
					}
					
					BoardNumber number = line.get(j);
					// Check for duplicate
					if (number.getValue() == compareBase.getValue()) {
						// Its a duplicate
						// Mark visited
						map |= (1 << j);
						duplicates.add(compareBase);
						duplicates.add(number);
					}
				}
			}
    }
	
	private TabledSet buildDuplicateSet() {
		TabledSet duplicates = new TabledSet(rows, cols);
		// Check each row
		for (int i = 0; i < rows; ++i) {
            findDuplicates(duplicates, board.getRow(i));
        }
        
        // Check each col
		for (int i = 0; i < cols; ++i) {
            findDuplicates(duplicates, board.getCol(i));
        }
		
		return duplicates;
	}
    
    private void getAndAdd(int row, int col, boolean duplicateOnly, Collection<BoardNumber> out) {
        if (duplicateOnly) {
            BoardNumber neighbour = duplicates.get(row, col);
            if (neighbour != null) {
                out.add(neighbour);
            }
        } else {
            out.add(board.get(row, col));
        }
    }
	
	private Set<BoardNumber> getNeighbours(BoardNumber root, boolean duplicatesOnly) {
		int row = root.getIndex() / cols;
		int col = root.getIndex() % cols;
		
		Set<BoardNumber> neighbours = new HashSet<>();
		if (row > 0) {
            getAndAdd(row-1, col, duplicatesOnly, neighbours);
		}
		
		if (col > 0) {
            getAndAdd(row, col - 1, duplicatesOnly, neighbours);
		}
		
		if (row < rows - 1) {
			getAndAdd(row + 1, col, duplicatesOnly, neighbours);
		}
		
		if (col < cols - 1) {
			getAndAdd(row, col + 1, duplicatesOnly, neighbours);
		}
		
        return neighbours;
	}
	
    private void generateInitialSearchSetFrom(List<BoardNumber> line) {
        for (int i = 0; i < line.size(); ++i) {
            BoardNumber duplicateBase = line.get(i);
            if (duplicateBase == null) {
                continue;
            }

            Set<BoardNumber> neighboursBase = getNeighbours(duplicateBase, true);

            // Check for others of same value
            for (int j = i + 1; j < line.size(); ++j) {
                BoardNumber duplicate = line.get(j);
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
	private void generateInitialSearchSet() {
		// Check each row
        for (int i = 0; i < rows; ++i) {
            List<BoardNumber> line = duplicates.getRow(i);
            generateInitialSearchSetFrom(line);
        }
		
		// Check each col
		for (int i = 0; i < cols; ++i) {
            List<BoardNumber> line = duplicates.getCol(i);
            generateInitialSearchSetFrom(line);
        }
	}
	
	public boolean step() {
		if (isDone) {
            return true;
        }
		
		if (!search.isEmpty()) {
			// Solve this one
			solve(search.pop());
		} else {
			// Check for those that would make the board discontinuous
			boolean found = false;
			for (BoardNumber number : duplicates) {
				if (doesBreakContinuity(number)) {
					solve(number);
					found = true;
					break;
				}
			}
			
			if (!found) {
				if (isLookingAhead) {
					// We ran out of things to check, but the board isnt complete
					// Mark this as unsure and move on
					restore();
					unsure.add(lookAheadStart);
				}
				
				// Engage Lookahead mode
                // Pick a random duplicate and assume that it is present, look for conflicts
                
				isLookingAhead = true;
				lookAheadStart = getNextLookaheadSource();
				lookAheadStart.setState(NumberState.LAMarked);
				
                search.push(lookAheadStart);
			}
		}
		
		// Check for conflicts
		if (isLookingAhead) {
			switch (board.getBoardState()) {
			case Invalid:
				// Ok so its not valid, restore the board and shade it
				restore();
				isLookingAhead = false;
				shade(lookAheadStart);
				break;
			case Complete:
				// Did it
                // Convert fake shaded to real shaded
                for (BoardNumber number : changed) {
                    if (number.getState() == NumberState.LAShaded) {
                        number.setState(NumberState.Shaded);
                    } else {
                        number.setState(NumberState.Normal);
                    }
                }
                
                isDone = true;
                isLookingAhead = false;
                return true;
			}
		} else {
            switch (board.getBoardState()) {
            case Complete:
                isDone = true;
                return true;
            case Incomplete:
                if (duplicates.isEmpty()) {
                    throw new AssertionError("Out of duplicates but not yet complete / invalid");
                }
                break;
            case Invalid:
                isDone = true;
                isUnsolvable = true;
                return true;
            }
        }
        
        return false;
	}
    
    public boolean solve() {
        while (step());
        
        return (!isUnsolvable);
    }
	
	private void restore() {
		for (BoardNumber number : changed) {
			duplicates.add(number);
			number.setState(NumberState.Normal);
		}
		
		changed.clear();
		search.clear();
	}
	
	private BoardNumber getNextLookaheadSource() {
		assert(duplicates.size() > 0): "Ran out of duplicates but still calling this";
		for (BoardNumber duplicate : duplicates) {
			// Do not look at ones we have looked at before with no results
			if (unsure.contains(duplicate)) {
				continue;
			}
			
			return duplicate;
		}
		
		// We have exhausted all duplicates
		// Erase the unsure as we must have found something by now
		unsure.clear();
		return getNextLookaheadSource();
	}
    
    private void solveLine(BoardNumber root, List<BoardNumber> line) {
        for (BoardNumber other : line) {
            if (other == null || other == root) {
                continue;
            }
            
            if (root.getValue() == other.getValue()) {
                shade(other);
            }
        }
    }
	
	private void solve(BoardNumber root) {
		// Find the same numbers in the same row and column and mark them as shaded
		int row = board.getRow(root);
		int col = board.getCol(root);
		
		duplicates.remove(root);
		if (isLookingAhead) {
			changed.add(root);
		}
		
		// Check row
        solveLine(root, duplicates.getRow(row));
        solveLine(root, duplicates.getCol(col));
	}
	
	private boolean doesBreakContinuity(BoardNumber number) {
		FloodFill fill = new FloodFill(board, number, getNeighbours(number, false));
		if (fill.flood()) {
			return false;
		} else {
			return true;
		}
	}
	
	private void shade(BoardNumber number) {
		if (isLookingAhead) {
			changed.add(number);
			number.setState(NumberState.LAShaded);
		} else {
			number.setState(NumberState.Shaded);
		}
		duplicates.remove(number);
		
		Set<BoardNumber> neighbours = getNeighbours(number, true);
		for (BoardNumber neighbour : neighbours) {
			if (!search.contains(neighbour)) {
				search.push(neighbour);
			}
		}
	}
}
