package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {   

    @Override
    public void start(Stage stage) throws Exception{
    	try {
            stage.setTitle("Galaga");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/test.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root); 
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());  
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}