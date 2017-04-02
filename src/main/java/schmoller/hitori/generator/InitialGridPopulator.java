package schmoller.hitori.generator;

import java.util.Random;

/**
 * Populates the grid with unique numbers.
 * @author schmoller
 */
public interface InitialGridPopulator {
    /**
     * Populates the grid with initial data
     * @param data The data to populate. Indices are at (col + row * size)
     * @param size The width and height of the grid
     * @param random The random instance
     */
    void populate(int[] data, int size, Random random);
}
