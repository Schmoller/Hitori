package schmoller.hitori.generator.implementations;

import java.util.Random;
import schmoller.hitori.generator.InitialGridPopulator;

/**
 * Populates the grid with unique numbers using a swap based approach
 * @author Schmoller
 */
public class SwapPopulator implements InitialGridPopulator {
    @Override
    public void populate(int[] data, int size, Random random) {
        // Initial grid
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < size; ++c) {
                int id = c + r * size;
                data[id] = (c + r) % size + 1;
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
}
