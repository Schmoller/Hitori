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
    
    private void applyDuplicates(boolean[] grid) {
        // Take the shaded cells and determine numbers that are duplicate to others
        // in the row or col
        

        // DEBUG: Make all shaded number 10
        for (int i = 0; i < grid.length; ++i) {
            if (grid[i]) {
                data[i] = 10;
            }
        }
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
        applyDuplicates(shadeMap);
        
        return new Board(size, size, finalizeBoard());
    }
}
