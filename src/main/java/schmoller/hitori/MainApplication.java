package schmoller.hitori;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/Main.fxml"));
		Parent window = loader.load();
		
		Scene scene = new Scene(window);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
}
