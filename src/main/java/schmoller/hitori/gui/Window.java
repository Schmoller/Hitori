package schmoller.hitori.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardState;
import schmoller.hitori.solver.Solver;
import schmoller.hitori.generator.Generator;

public class Window {
	@FXML
	private BorderPane numberGrid;
	@FXML
	private Label solvedOutput;
	
	private Board board;
	private Solver solver;
	private BoardDisplay display;
	
	@FXML
	private void initialize() {
		Board board;
//		// Easy 1
//		board = Board.from(9, 9,
//			1,8,2,4,3,9,4,1,5,
//			6,5,5,1,1,4,9,3,3,
//			8,4,6,9,5,3,2,6,7,
//			4,8,3,8,7,8,8,2,9,
//			1,6,4,2,3,6,5,9,1,
//			9,5,8,3,5,2,7,6,1,
//			2,5,3,1,6,4,4,2,8,
//			5,4,4,2,9,3,7,7,6,
//			7,9,1,5,1,6,3,8,8
//		);
//		// Medium 1
//		board = Board.from(9, 9,
//			6,8,8,5,8,6,4,9,3,
//			1,7,3,8,2,8,5,1,4,
//			4,4,9,4,8,3,7,6,1,
//			8,6,2,7,5,2,9,5,3,
//			3,4,7,1,5,1,3,1,8,
//			6,5,8,1,9,7,3,4,2,
//			2,8,6,3,8,4,8,7,5,
//			7,1,5,8,4,7,7,1,6,
//			7,3,7,2,6,5,1,8,7
//		);
		// Hard 1
		board = Board.from(9, 9,
			2,4,3,8,5,6,6,7,8,
			9,5,7,8,4,4,6,3,2,
			2,3,5,1,9,7,4,8,2,
			1,5,8,8,7,9,2,4,6,
			4,6,6,7,8,2,9,6,1,
			7,6,3,9,4,1,1,2,3,
			6,2,3,4,1,3,8,6,7,
			3,1,2,5,6,8,4,9,4,
			6,1,4,7,3,7,2,1,7
		);
		
		setBoard(board);
	}
    
    private void setBoard(Board board) {
        this.board = board;
        solver = null;
        display = new BoardDisplay(board);
        numberGrid.setCenter(display);
        
        display.setOnNumberStateChange(() -> {
        	clearSolver();
        	updateBoardState();
        });
    }
	
    private void updateBoardState() {
    	if (solver == null) {
	    	switch (board.getBoardState()) {
			case Complete:
				solvedOutput.setText("Solved");
				break;
			case Incomplete:
				solvedOutput.setText("Incomplete");
				break;
			case Invalid:
				solvedOutput.setText("Invalid");
				break;
			}
    	} else {
            switch (solver.getState()) {
            case Solved:
                solvedOutput.setText("Solved");
                break;
            case Invalid:
                solvedOutput.setText("Invalid");
                break;
            case NotUnique:
                solvedOutput.setText("Not Unique");
                break;
            default:
            case Unsolved:
                solvedOutput.setText("Unsolved");
                break;
            }
    	}
	}
    
    private void clearSolver() {
		if (solver != null) {
			solver.abort();
			solver = null;
			display.refreshNumbers();
		}
	}
    
    @FXML
	private void handleSolve(ActionEvent event) {
    	if (board.getBoardState() == BoardState.Complete) {
    		return;
    	}
    	
        clearSolver();
        
        solver = new Solver(board);
        solver.solve();
        
        updateBoardState();
        display.refreshNumbers();
    }
    
	@FXML
	private void handleSolveStep(ActionEvent event) {
		if (board.getBoardState() == BoardState.Complete) {
    		return;
    	}
    	
		if (solver == null) {
			solver = new Solver(board);
		} else {
			solver.step();
		}
		
		updateBoardState();
		display.refreshNumbers();
	}
	
	@FXML
	private void handleGenerate(ActionEvent event) {
        Generator gen = new Generator(9);
        setBoard(gen.generate());
	}
}
