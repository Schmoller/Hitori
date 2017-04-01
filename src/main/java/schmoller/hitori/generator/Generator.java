package schmoller.hitori.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;

public class Generator {
    private final int[] data;
    private final int size;
    private final Random random;
    
    public Generator(int size) {
        assert (size > 2);
        
        this.size = size;
        random = new Random();
        data = new int[size*size];
    }
    
    private void fillUnique() {
        // Initial grid
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < size; ++c) {
                int id = c + r * size;
                data[id] = (c + r) % 9 + 1;
            }
        }
        
        // Swap columns and rows for a while
        int count = 10;
        for (int c = 0; c < count; ++c) {
            // Select 2 columns
            int from = random.nextInt(size);
            int to = random.nextInt(size-1);
            if (to >= from) {
                ++to;
            }
            
            // Swap them
            for (int i = 0; i < size; ++i) {
                int temp = data[to + i * size];
                data[to + i * size] = data[from + i * size];
                data[from + i * size] = temp;
            }
            
            // Select 2 rows
            from = random.nextInt(size);
            to = random.nextInt(size-1);
            if (to >= from) {
                ++to;
            }
            
            // Swap them
            for (int i = 0; i < size; ++i) {
                int temp = data[i + to * size];
                data[i + to * size] = data[i + from * size];
                data[i + from * size] = temp;
            }
        }
    }
    
    private boolean[] randomShade(int toAdd) {
        // Randomly select ones to be shaded,
        // Make sure not to place them next to another shaded
        boolean[] grid = new boolean[size*size];
        
        int added = 0;
        while (added < toAdd) {
            // Pick a spot, make sure it does not touch another shaded
            int index = random.nextInt(grid.length);
            
            // Check for touching
            boolean ok = true;
            int next = index(index, 0, -1);
            if (next >= 0 && grid[next]) {
                ok = false;
            }
            next = index(index, 0, 1);
            if (next >= 0 && grid[next]) {
                ok = false;
            }
            next = index(index, 1, 0);
            if (next >= 0 && grid[next]) {
                ok = false;
            }
            next = index(index, -1, 0);
            if (next >= 0 && grid[next]) {
                ok = false;
            }
            
            if (ok) {
                // We can add this
                grid[index] = true;
                ++added;
            }
        }
        
        return grid;
    }
    
    private int index(int start, int dr, int dc) {
        int r = start / size;
        int c = start % size;
        
        r += dr;
        c += dc;
        
        if (r < 0 || r >= size) {
            return -1;
        }
        if (c < 0 || c >= size) {
            return -1;
        }
        
        return c + r * size;
    }
    
    private void removeShaded(int root, int dr, int dc, boolean[] grid) {
    	int r = root / size;
        int c = root % size;
        
        r += dr;
        c += dc;
    	
        boolean isHorizontal = random.nextBoolean();
        
        if (r < 0 || r >= size) {
        	// Cant do vertical
        	isHorizontal = true;
        } else if (c < 0 || c >= size) {
        	// Cant do horizontal
        	isHorizontal = false;
        }
        
        System.out.println("Remove shaded " + r + "," + c);
        
    	if (isHorizontal) {
    		// Horizontal
    		grid[index(root, 0, dc)] = false;
    		System.out.println("Removed " + index(root, 0, dc));
    	} else {
    		// Vertical
    		grid[index(root, dr, 0)] = false;
    		System.out.println("Removed " + index(root, dr, 0));
    	}
    }
    
    private void makeContinuous(boolean[] grid) {
        // Flood fill and make sure we visit every non shaded spot
        
        boolean[] visited = Arrays.copyOf(grid, grid.length);
        
        boolean canReachAll = false;
        while (!canReachAll) {
            // see what we can reach
            Deque<Integer> search = new ArrayDeque<>();
            // Initial search spot
            // Find the first that is not shaded
            for (int i = 0; i < grid.length; ++i) {
                if (!grid[i]) {
                    search.add(i);
                    System.out.println("Using " + i + " as the initial flood point");
                    break;
                }
            }

            while(!search.isEmpty()) {
                int index = search.pop();
                visited[index] = true;

                // Left
                int next = index(index, 0, -1);
                if (next >= 0 && !visited[next] && !grid[next]) {
                    search.add(next);
                }
                
                // Right
                next = index(index, 0, 1);
                if (next >= 0 && !visited[next] && !grid[next]) {
                    search.add(next);
                }
                
                // Up
                next = index(index, -1, 0);
                if (next >= 0 && !visited[next] && !grid[next]) {
                    search.add(next);
                }
                
                // Down
                next = index(index, 1, 0);
                if (next >= 0 && !visited[next] && !grid[next]) {
                    search.add(next);
                }
            }
            
            // Debug output
            for (int row = 0; row < size; ++row) {
                for (int c = 0; c < size; ++c) {
                    if (grid[c + row * size]) {
                        System.out.print("#");
                    } else {
                        System.out.print((visited[c + row * size] ? "." : " "));
                    }
                }
                System.out.println();
            }

            canReachAll = true;
            // See if we missed any
            for (int i = 0; i < visited.length; ++i) {
                if (!visited[i]) {
                    canReachAll = false;
                    System.out.println("Missed " + (i % size) + " " + (i / size));

                    // Now find a shaded thing we can remove to connect them
                    // There should be a visited area on the diagonal, that is not a shaded block
                    boolean done = false;
                    // Search top left
                    int next = index(i, -1, -1);
                    if (next >= 0 && visited[next] && !grid[next]) {
                    	removeShaded(i, -1, -1, grid);
                        done = true;
                        System.out.println("Remove " + index(i, -1, 0));
                    }
                    
                    // Search top right
                    next = index(i, -1, 1);
                    if (next >= 0 && visited[next] && !grid[next]) {
                    	removeShaded(i, -1, 1, grid);
                        done = true;
                        System.out.println("Remove " + index(i, -1, 0));
                    }

                    // Search bottom left
                    next = index(i, 1, -1);
                    if (next >= 0 && visited[next] && !grid[next]) {
                    	removeShaded(i, 1, -1, grid);
                        done = true;
                        System.out.println("Remove " + index(i, 1, 0));
                    }

                    // Search bottom right
                    next = index(i, 1, 1);
                    if (next >= 0 && visited[next] && !grid[next]) {
                    	removeShaded(i, 1, 1, grid);
                        done = true;
                        System.out.println("Remove " + index(i, 1, 0));
                    }
                    
                    if (done) {
                        System.out.println("Removed one shaded");
                        break;
                    }
                }
            }

            // Clear visited
            System.arraycopy(grid, 0, visited, 0, grid.length);
        }
    }
    
    private boolean canReachNeighbours(boolean[] grid, int start) {
    	boolean[] visited = Arrays.copyOf(grid, grid.length);
    	
    	visited[start] = true;
    	
    	// Neighbour indices
    	int left = index(start, 0, -1);
    	int right = index(start, 0, 1);
    	int up = index(start, -1, 0);   	
    	int down = index(start, 1, 0);
     	
    	Deque<Integer> search = new ArrayDeque<>();
    	if (left >= 0) {
    		search.add(left);
    	} else if (right >= 0) {
    		search.add(right);
    	} else if (up >= 0) {
    		search.add(up);
    	} else if (down >= 0) {
    		search.add(down);
    	}
    	
    	// Flood
    	while(!search.isEmpty()) {
            int index = search.pop();
            visited[index] = true;

            // Left
            int next = index(index, 0, -1);
            if (next >= 0 && !visited[next] && !grid[next]) {
                search.add(next);
            }
            
            // Right
            next = index(index, 0, 1);
            if (next >= 0 && !visited[next] && !grid[next]) {
                search.add(next);
            }
            
            // Up
            next = index(index, -1, 0);
            if (next >= 0 && !visited[next] && !grid[next]) {
                search.add(next);
            }
            
            // Down
            next = index(index, 1, 0);
            if (next >= 0 && !visited[next] && !grid[next]) {
                search.add(next);
            }
        }
    	
    	// Debug output
        for (int row = 0; row < size; ++row) {
            for (int c = 0; c < size; ++c) {
            	int index = c + row * size;
            	if (index == start) {
            		System.out.println("*");
            	} else {
	                if (visited[c + row * size]) {
	                    System.out.print(" ");
	                } else {
	                    System.out.print("#");
	                }
            	}
            }
            System.out.println();
        }
    	
    	// Check all are visited
    	if (left >= 0 && !visited[left]) {
    		return false;
    	}
    	
    	if (right >= 0 && !visited[right]) {
    		return false;
    	}
    	
    	if (up >= 0 && !visited[up]) {
    		return false;
    	}
    	
    	if (down >= 0 && !visited[down]) {
    		return false;
    	}
    	
    	return true;
    }
    
    private void makeUnique(boolean[] grid) {
    	// Randomly fill up empty spaces with shaded blocks
    	// The space cannot have a shaded block adjacent to it
    	// and must not cut off the grid
    	
    	System.out.println("Ensuring unique solution");
    	List<Integer> potentials = new ArrayList<>();
    	for (int i = 0; i < grid.length; ++i) {
    		if (grid[i]) {
    			continue;
    		}
    		
    		// Left
            int next = index(i, 0, -1);
            if (next >= 0 && grid[next]) {
            	continue;
            }
            
            // Right
            next = index(i, 0, 1);
            if (next >= 0 && grid[next]) {
            	continue;
            }
            
            // Up
            next = index(i, -1, 0);
            if (next >= 0 && grid[next]) {
            	continue;
            }
            
            // Down
            next = index(i, 1, 0);
            if (next >= 0 && grid[next]) {
            	continue;
            }
            
        	System.out.println("Look at " + (i / size) + "," + (i % size));
        	potentials.add(i);
    	}
    	
    	// Fill in the gaps
    	while (!potentials.isEmpty()) {
    		int arrayIndex = random.nextInt(potentials.size());
    		int index = potentials.remove(arrayIndex);
    		
    		System.out.println("Check " + (index / size) + "," + (index % size));
    		
    		// Check for neighbours
    		boolean ok = true;
    		// Left
            int next = index(index, 0, -1);
            if (next >= 0 && grid[next]) {
            	ok = false;
            }
            
            // Right
            next = index(index, 0, 1);
            if (next >= 0 && grid[next]) {
            	ok = false;
            }
            
            // Up
            next = index(index, -1, 0);
            if (next >= 0 && grid[next]) {
            	ok = false;
            }
            
            // Down
            next = index(index, 1, 0);
            if (next >= 0 && grid[next]) {
            	ok = false;
            }
            
            if (!ok) {
            	// Shaded block adjacent
            	System.out.println("Discard " + (index / size) + "," + (index % size));
            	continue;
            }
            
            // Check cutoff
            if (canReachNeighbours(grid, index)) {
            	// Shade it
            	grid[index] = true;
            	System.out.println("Shade " + (index / size) + "," + (index % size));

            	// Debug output
                for (int row = 0; row < size; ++row) {
                    for (int c = 0; c < size; ++c) {
                        if (grid[c + row * size]) {
                            System.out.print("#");
                        } else {
                            System.out.print(".");
                        }
                    }
                    System.out.println();
                }
            }
    	}
    	
    	// Debug output
        for (int row = 0; row < size; ++row) {
            for (int c = 0; c < size; ++c) {
                if (grid[c + row * size]) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
    
    private void applyDuplicates(boolean[] grid) {
        // Take the shaded cells and determine numbers that are duplicate to others
        // in the row or col
        for (int r = 0; r < size; ++r) {
        	for (int c = 0; c < size; ++c) {
        		if (!grid[c + r * size]) {
        			continue;
        		}
        		
        		// This is a shaded cell
        		// Find a number that will work here
        		
        		// Maybe we can use a mask
        		// each bit is a number
        		// 1 means we can use that number, 0 not
        		// generate a mask for the row and the col
        		// or them together
        		// any of those numbers should be ok
        		
        		long mask = genRowMask(r, grid) | genColMask(c, grid);
        		int value = randomNumber(mask);
        		data[c + r * size] = value;
        	}
        }
    }
    
    private long genRowMask(int r, boolean[] grid) {
    	long mask = 0;
    	for (int c = 0; c < size; ++c) {
    		// As long as it is not shaded
    		if (!grid[c + r * size]) {
    			// Can use the number
    			mask |= (1 << data[c + r * size]);
    		}
    	}
    	
    	return mask;
    }
    
    private long genColMask(int c, boolean[] grid) {
    	long mask = 0;
    	for (int r = 0; r < size; ++r) {
    		// As long as it is not shaded
    		if (!grid[c + r * size]) {
    			// Can use the number
    			mask |= (1 << data[c + r * size]);
    		}
    	}
    	
    	return mask;
    }
    
    private int randomNumber(long mask) {
    	// Work out how many we can choose from
    	int choices = 0;
    	for (int i = 1; i <= size; ++i) {
    		if ((mask & (1 << i)) != 0) {
    			++choices;
    		}
    	}
    	
    	int choice = random.nextInt(choices);
    	// Find the value
    	for (int i = 1; i <= size; ++i) {
    		if ((mask & (1 << i)) != 0) {
    			if (choice == 0) {
    				return i;
    			} else {
    				--choice;
    			}
    		}
    	}
    	
    	throw new AssertionError("Cannot reach here");
    }
    
    private BoardNumber[] finalizeBoard() {
        BoardNumber[] finalData = new BoardNumber[size*size];
        for (int i = 0; i < data.length; ++i) {
            finalData[i] = new BoardNumber(data[i], i);
        }
        
        return finalData;
    }
    
    public Board generate() {
        fillUnique();
        
        boolean[] shadeMap = randomShade(40);
        makeContinuous(shadeMap);
        makeUnique(shadeMap);
        applyDuplicates(shadeMap);
        
        return new Board(size, size, finalizeBoard());
    }
}
