package schmoller.hitori.generator;

import java.util.Random;

/**
 * The strategy for generating the shade map
 * @author schmoller
 */
public abstract class ShadeMapStrategy {
    /**
     * Creates a shade map according to the strategy
     * @param size The width and height of the grid
     * @param random The random object to use
     * @return The shade map of length = size^2. Indexes should be at (col + row * size)
     */
    public boolean[] createShadeMap(int size, Random random) {
        boolean[] shadeMap = new boolean[size*size];
        
        generate(shadeMap, size, random);
        
        return shadeMap;
    }
    
    protected abstract void generate(boolean[] shadeMap, int size, Random random);
}
