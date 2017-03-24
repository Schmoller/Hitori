package schmoller.hitori.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;
import schmoller.hitori.solver.Solver;
import schmoller.hitori.NumberState;

public class Window {
	@FXML
	private GridPane numberGrid;
	@FXML
	private Label solvedOutput;
	
	private Board board;
	private Solver solver;
	
	private Map<BoardNumber, Label> labels;
	
	public Window() {
		labels = new HashMap<>();
	}
	
	@FXML
	private void initialize() {
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
		
		numberGrid.getChildren().clear();
		for (int col = 0; col < board.getCols(); ++col) {
			for (int row = 0; row < board.getRows(); ++row) {
				BoardNumber num = board.get(row, col);
				Label label = new Label(String.valueOf(num.getValue()));
				label.setMaxSize(1000, 1000);
				label.setAlignment(Pos.CENTER);
				label.getStyleClass().add("number");
				updateNumber(num, label);
				
				label.setOnMouseClicked((e) -> {
					if (e.getButton() == MouseButton.PRIMARY) {
						switch (num.getState()) {
						case Marked:
							num.setState(NumberState.Normal);
							break;
						default:
						case Normal:
							num.setState(NumberState.Shaded);
							break;
						case Shaded:
							num.setState(NumberState.Marked);
							break;
						}
					} else if (e.getButton() == MouseButton.SECONDARY) {
						num.setState(NumberState.Normal);
					}
					
					updateNumber(num, label);
					
					if (board.isSolved()) {
						solvedOutput.setText("YES!");
					} else {
						solvedOutput.setText("No");
					}
				});
				
				numberGrid.add(label, col, row);
				labels.put(num, label);
			}
		}
	}
	
	private void updateNumber(BoardNumber number, Label label) {
		switch (number.getState()) {
		case Marked:
			label.getStyleClass().remove("normal");
			label.getStyleClass().add("marked");
			label.getStyleClass().remove("shaded");
			break;
		default:
		case Normal:
			label.getStyleClass().add("normal");
			label.getStyleClass().remove("marked");
			label.getStyleClass().remove("shaded");
			break;
		case Shaded:
			label.getStyleClass().remove("normal");
			label.getStyleClass().remove("marked");
			label.getStyleClass().add("shaded");
			break;
		}
	}
	
	private void updateAll() {
		for (int col = 0; col < board.getCols(); ++col) {
			for (int row = 0; row < board.getRows(); ++row) {
				BoardNumber num = board.get(row, col);
				Label label = labels.get(num);
				updateNumber(num, label);
			}
		}
		
		if (board.isSolved()) {
			solvedOutput.setText("YES!");
		} else {
			solvedOutput.setText("No");
		}
	}
	
	
	@FXML
	private void handleSolve(ActionEvent event) {
		if (solver == null) {
			solver = new Solver(board);
		} else {
			solver.step();
		}
		
		updateAll();
	}
	
	@FXML
	private void handleGenerate(ActionEvent event) {
		System.out.println("Generate");
	}
}
