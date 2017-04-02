package schmoller.hitori.generator;

import java.util.Random;

/**
 * The strategy for filling the grid with duplicates based on the shade map
 * @author schmoller
 */
public interface DuplicateStrategy {
    /**
     * Fills the base, replacing any shaded segments with duplicate numbers
     * @param base The base number grid with only unique numbers
     * @param shadeMap The shade map
     * @param size The width and height of the grid
     * @param random The random instance
     */
    void fill(int[] base, boolean[] shadeMap, int size, Random random);
}
