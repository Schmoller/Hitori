package schmoller.hitori.generator;

import java.util.Random;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;

public class Generator {
    private final int[] data;
    private final int size;
    private final Random random;
    
    private final InitialGridPopulator initialPopulator;
    private final ShadeMapStrategy shadeStrategy;
    private final DuplicateStrategy duplicateStrategy;
    
    public Generator(int size, InitialGridPopulator initialPopulator, ShadeMapStrategy shadeStrategy, DuplicateStrategy duplicateStrategy) {
        this(size, System.currentTimeMillis(), initialPopulator, shadeStrategy, duplicateStrategy);
    }
    
    public Generator(int size, long randomSeed, InitialGridPopulator initialPopulator, ShadeMapStrategy shadeStrategy, DuplicateStrategy duplicateStrategy) {
        assert (size > 2);
        
        this.size = size;
        this.initialPopulator = initialPopulator;
        this.shadeStrategy = shadeStrategy;
        this.duplicateStrategy = duplicateStrategy;
        
        System.out.println("Seed: " + randomSeed);
        random = new Random(randomSeed);
        data = new int[size*size];
    }
    
    private BoardNumber[] finalizeBoard() {
        BoardNumber[] finalData = new BoardNumber[size*size];
        for (int i = 0; i < data.length; ++i) {
            finalData[i] = new BoardNumber(data[i], i);
        }
        
        return finalData;
    }
    
    public Board generate() {
        initialPopulator.populate(data, size, random);
        boolean[] shadeMap = shadeStrategy.createShadeMap(size, random);
        duplicateStrategy.fill(data, shadeMap, size, random);
        
        return new Board(size, size, finalizeBoard());
    }
}
