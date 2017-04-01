package schmoller.hitori.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;
import schmoller.hitori.NumberState;

public class BoardDisplay extends GridPane {
    private final Board board;
    
    private final Map<BoardNumber, Region> numberMap;
    
    private Runnable onNumberStateChange; 
    
    public BoardDisplay(Board board) {
        this.board = board;
        this.numberMap = new HashMap<>();
        
        generate();
        
        setOnNumberStateChange(null);
    }
    
    private void generate() {
        for (int r = 0; r < board.getRows(); ++r) {
            for (int c = 0; c < board.getCols(); ++c) {
                BoardNumber number = board.get(r,c);
                Region region = createItem(number);
                numberMap.put(number, region);
                add(region, r, c);
            }
        }
    }
    
    private Region createItem(BoardNumber number) {
        Label label = new Label(String.valueOf(number.getValue()));
        label.setPrefSize(1000, 1000); // Ensure that it fills the entire cell
        label.setAlignment(Pos.CENTER);
        applyStyle(number, label);
        
        label.setOnMouseClicked(e -> onNumberClicked(e.getButton(), number));
        
        return label;
    }
    
    private void applyStyle(BoardNumber number, Region cell) {
        cell.getStyleClass().setAll("number", number.getState().name().toLowerCase());
    }
    
    private void onNumberClicked(MouseButton button, BoardNumber number) {
        if (button == MouseButton.PRIMARY) {
            switch (number.getState()) {
            case Normal:
                number.setState(NumberState.Shaded);
                break;
            case Shaded:
                number.setState(NumberState.Marked);
                break;
            default:
                number.setState(NumberState.Normal);
                break;
            }
        } else {
            number.setState(NumberState.Normal);
        }
        
        applyStyle(number, numberMap.get(number));
        
        onNumberStateChange.run();
    }
    
    public void setOnNumberStateChange(Runnable r) {
    	if (r == null) {
    		onNumberStateChange = () -> {};
    	} else {
    		onNumberStateChange = r;
    	}
    }
    
    public void refreshNumbers() {
    	for (Entry<BoardNumber, Region> entry : numberMap.entrySet()) {
    		applyStyle(entry.getKey(), entry.getValue());
    	}
    }
}
