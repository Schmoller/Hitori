package schmoller.hitori.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    
    private BoardNumber[] finalizeBoard() {
        BoardNumber[] finalData = new BoardNumber[size*size];
        for (int i = 0; i < data.length; ++i) {
            finalData[i] = new BoardNumber(data[i], i);
        }
        
        return finalData;
    }
    
    public Board generate() {
        fillUnique();
        
        return new Board(size, size, finalizeBoard());
    }
}
