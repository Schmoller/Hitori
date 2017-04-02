package schmoller.hitori.generator.implementations;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import schmoller.hitori.generator.ShadeMapStrategy;

/**
 * A shade map generator that 
 * @author schmoller
 */
public class RandomShader extends ShadeMapStrategy {
    private int frequency;
    private boolean isFrequencyFixed;
    
    private boolean[] shadeMap;
    private int size;
    private Random random;
    
    /**
     * Creates a random shader that has a fixed frequency
     * @param frequency The maximum number of cells to add. Less may be added 
     *                  if it is not possible to add them
     */
    public RandomShader(int frequency) {
        if (frequency <= 0) {
            throw new IllegalArgumentException("Invalid frequency");
        }
        this.frequency = frequency;
        isFrequencyFixed = true;
    }
    
    /**
     * Creates a random shader with a variable frequency of half the grid size
     */
    public RandomShader() {
        frequency = -1;
        isFrequencyFixed = false;
    }

    @Override
    protected void generate(boolean[] shadeMap, int size, Random random) {
        if (!isFrequencyFixed) {
            frequency = (size * size) / 2;
        }
        
        this.shadeMap = shadeMap;
        this.size = size;
        this.random = random;
        
        randomShade();
        makeContinuous();
        fillEmpty();
    }
    
    /**
     * Step 1 - Randomly fill the shade map with shaded segments.
     * This will obey the rule of no adjacent shaded cells. It will not 
     * ensure that the field is continuous
     */
    private void randomShade() {
        // Randomly select ones to be shaded,
        // Make sure not to place them next to another shaded
        int added = 0;
        while (added < frequency) {
            // Pick a spot, make sure it does not touch another shaded
            int index = random.nextInt(shadeMap.length);
            
            // Check for touching
            boolean ok = true;
            int next = index(index, 0, -1);
            if (next >= 0 && shadeMap[next]) {
                ok = false;
            }
            next = index(index, 0, 1);
            if (next >= 0 && shadeMap[next]) {
                ok = false;
            }
            next = index(index, 1, 0);
            if (next >= 0 && shadeMap[next]) {
                ok = false;
            }
            next = index(index, -1, 0);
            if (next >= 0 && shadeMap[next]) {
                ok = false;
            }
            
            if (ok) {
                // We can add this
                shadeMap[index] = true;
                ++added;
            }
        }
    }
    
    /**
     * Step 2 - Ensure that the rule of continuity is kept.
     */
    private void makeContinuous() {
        // Flood fill and make sure we visit every non shaded spot
        
        boolean[] visited = Arrays.copyOf(shadeMap, shadeMap.length);
        
        boolean canReachAll = false;
        while (!canReachAll) {
            // see what we can reach
            Deque<Integer> search = new ArrayDeque<>();
            // Initial search spot
            // Find the first that is not shaded
            for (int i = 0; i < shadeMap.length; ++i) {
                if (!shadeMap[i]) {
                    search.add(i);
                    break;
                }
            }

            while(!search.isEmpty()) {
                int index = search.pop();
                visited[index] = true;

                // Left
                int next = index(index, 0, -1);
                if (next >= 0 && !visited[next] && !shadeMap[next]) {
                    search.add(next);
                }
                
                // Right
                next = index(index, 0, 1);
                if (next >= 0 && !visited[next] && !shadeMap[next]) {
                    search.add(next);
                }
                
                // Up
                next = index(index, -1, 0);
                if (next >= 0 && !visited[next] && !shadeMap[next]) {
                    search.add(next);
                }
                
                // Down
                next = index(index, 1, 0);
                if (next >= 0 && !visited[next] && !shadeMap[next]) {
                    search.add(next);
                }
            }
            
            canReachAll = true;
            // See if we missed any
            for (int i = 0; i < visited.length; ++i) {
                if (!visited[i]) {
                    canReachAll = false;
                    // Now find a shaded thing we can remove to connect them
                    // There should be a visited area on the diagonal, that is not a shaded block
                    boolean done = false;
                    // Search top left
                    int next = index(i, -1, -1);
                    if (next >= 0 && visited[next] && !shadeMap[next]) {
                    	removeShaded(i, -1, -1);
                        done = true;
                    }
                    
                    // Search top right
                    next = index(i, -1, 1);
                    if (next >= 0 && visited[next] && !shadeMap[next]) {
                    	removeShaded(i, -1, 1);
                        done = true;
                    }

                    // Search bottom left
                    next = index(i, 1, -1);
                    if (next >= 0 && visited[next] && !shadeMap[next]) {
                    	removeShaded(i, 1, -1);
                        done = true;
                    }

                    // Search bottom right
                    next = index(i, 1, 1);
                    if (next >= 0 && visited[next] && !shadeMap[next]) {
                    	removeShaded(i, 1, 1);
                        done = true;
                    }
                    
                    if (done) {
                        break;
                    }
                }
            }

            // Clear visited
            System.arraycopy(shadeMap, 0, visited, 0, shadeMap.length);
        }
    }
        
    /**
     * Step 3 - Ensure maximum fill of the grid while not breaking either rule
     */
    private void fillEmpty() {
    	// Randomly fill up empty spaces with shaded blocks
    	// The space cannot have a shaded block adjacent to it
    	// and must not cut off the shadeMap

    	List<Integer> potentials = new ArrayList<>();
    	for (int i = 0; i < shadeMap.length; ++i) {
    		if (shadeMap[i]) {
    			continue;
    		}
    		
    		// Left
            int next = index(i, 0, -1);
            if (next >= 0 && shadeMap[next]) {
            	continue;
            }
            
            // Right
            next = index(i, 0, 1);
            if (next >= 0 && shadeMap[next]) {
            	continue;
            }
            
            // Up
            next = index(i, -1, 0);
            if (next >= 0 && shadeMap[next]) {
            	continue;
            }
            
            // Down
            next = index(i, 1, 0);
            if (next >= 0 && shadeMap[next]) {
            	continue;
            }

        	potentials.add(i);
    	}
    	
    	// Fill in the gaps
    	while (!potentials.isEmpty()) {
    		int arrayIndex = random.nextInt(potentials.size());
    		int index = potentials.remove(arrayIndex);
    		
    		// Check for neighbours
    		boolean ok = true;
    		// Left
            int next = index(index, 0, -1);
            if (next >= 0 && shadeMap[next]) {
            	ok = false;
            }
            
            // Right
            next = index(index, 0, 1);
            if (next >= 0 && shadeMap[next]) {
            	ok = false;
            }
            
            // Up
            next = index(index, -1, 0);
            if (next >= 0 && shadeMap[next]) {
            	ok = false;
            }
            
            // Down
            next = index(index, 1, 0);
            if (next >= 0 && shadeMap[next]) {
            	ok = false;
            }
            
            if (!ok) {
            	// Shaded block adjacent
            	continue;
            }
            
            // Check cutoff
            if (canReachNeighbours(shadeMap, index)) {
            	// Shade it
            	shadeMap[index] = true;
            }
    	}
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
    
    private void removeShaded(int root, int dr, int dc) {
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

    	if (isHorizontal) {
    		// Horizontal
    		shadeMap[index(root, 0, dc)] = false;
    	} else {
    		// Vertical
    		shadeMap[index(root, dr, 0)] = false;
    	}
    }
    
    private boolean canReachNeighbours(boolean[] shadeMap, int start) {
    	boolean[] visited = Arrays.copyOf(shadeMap, shadeMap.length);
    	
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
            if (next >= 0 && !visited[next] && !shadeMap[next]) {
                search.add(next);
            }
            
            // Right
            next = index(index, 0, 1);
            if (next >= 0 && !visited[next] && !shadeMap[next]) {
                search.add(next);
            }
            
            // Up
            next = index(index, -1, 0);
            if (next >= 0 && !visited[next] && !shadeMap[next]) {
                search.add(next);
            }
            
            // Down
            next = index(index, 1, 0);
            if (next >= 0 && !visited[next] && !shadeMap[next]) {
                search.add(next);
            }
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
}
