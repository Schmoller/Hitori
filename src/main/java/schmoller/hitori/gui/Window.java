package schmoller.hitori.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class Window {
	@FXML
	private GridPane numberGrid;
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleSolve(ActionEvent event) {
		System.out.println("Solve");
	}
	
	@FXML
	private void handleGenerate(ActionEvent event) {
		System.out.println("Generate");
	}
}
