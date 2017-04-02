package schmoller.hitori.generator.implementations;

import java.util.Random;
import schmoller.hitori.generator.DuplicateStrategy;

public class DuplicateFiller implements DuplicateStrategy {
    private Random random;
    private int size;
    
    private int[] data;
    private boolean[] shadeMap;

    @Override
    public void fill(int[] base, boolean[] shadeMap, int size, Random random) {
        this.data = base;
        this.shadeMap = shadeMap;
        this.size = size;
        this.random = random;
        
        applyDuplicates();
    }
    
    private void applyDuplicates() {
    	System.out.println("Converting shade map into duplicates");
    	System.out.println("Input:");
    	// Debug output
        for (int row = 0; row < size; ++row) {
            for (int c = 0; c < size; ++c) {
                if (shadeMap[c + row * size]) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        
    	// Debug output
        for (int row = 0; row < size; ++row) {
            for (int c = 0; c < size; ++c) {
            	if (shadeMap[c + row * size]) {
            		System.out.print(" ");
            	} else {
            		System.out.print(data[c + row * size]);
            	}
            }
            System.out.println();
        }
    	
        // Take the shaded cells and determine numbers that are duplicate to others
        // in the row or col
        for (int r = 0; r < size; ++r) {
        	for (int c = 0; c < size; ++c) {
        		if (!shadeMap[c + r * size]) {
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
        		
        		long mask = genRowMask(r, shadeMap) | genColMask(c, shadeMap);
        		int value = randomNumber(mask);
        		data[c + r * size] = value;
        	}
        }
        
        System.out.println("Output:");
    	// Debug output
        for (int row = 0; row < size; ++row) {
            for (int c = 0; c < size; ++c) {
            	System.out.print(data[c + row * size]);
            }
            System.out.println();
        }
    }
    
    private long genRowMask(int r, boolean[] shadeMap) {
    	long mask = 0;
    	for (int c = 0; c < size; ++c) {
    		// As long as it is not shaded
    		if (!shadeMap[c + r * size]) {
    			// Can use the number
    			mask |= (1 << data[c + r * size]);
    		}
    	}
    	
    	return mask;
    }
    
    private long genColMask(int c, boolean[] shadeMap) {
    	long mask = 0;
    	for (int r = 0; r < size; ++r) {
    		// As long as it is not shaded
    		if (!shadeMap[c + r * size]) {
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
}
