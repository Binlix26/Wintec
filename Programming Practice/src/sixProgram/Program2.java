package sixProgram;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Program2 extends Application{

    @Override
    public void start(Stage primaryStage) {
        ListDirectory app = new ListDirectory();

        Scene scene = new Scene(app,300,250); //create scene to hold the app
        primaryStage.setTitle("FileList");
        primaryStage.setScene(scene); // put the scene in the stage
        primaryStage.show(); // display
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Application.launch(args);
    }
}
