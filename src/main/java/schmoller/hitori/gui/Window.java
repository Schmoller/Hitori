package schmoller.hitori.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import schmoller.hitori.Board;
import schmoller.hitori.Board.BoardNumber;
import schmoller.hitori.NumberState;
import schmoller.hitori.Solver;

public class Window {
	@FXML
	private GridPane numberGrid;
	
	private Board board;
	
	@FXML
	private void initialize() {
		board = Board.from(9, 9,
			1,8,2,4,3,9,4,1,5,
			6,5,5,1,1,4,9,3,3,
			8,4,6,9,5,3,2,6,7,
			4,8,3,8,7,8,8,2,9,
			1,6,4,2,3,6,5,9,1,
			9,5,8,3,5,2,7,6,1,
			2,5,3,1,6,4,4,2,8,
			5,4,4,2,9,3,7,7,6,
			7,9,1,5,1,6,3,8,8
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
				});
				
				numberGrid.add(label, col, row);
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
	
	
	@FXML
	private void handleSolve(ActionEvent event) {
		System.out.println("Solve");
		Solver solver = new Solver(board);
	}
	
	@FXML
	private void handleGenerate(ActionEvent event) {
		System.out.println("Generate");
	}
}
