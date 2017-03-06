package sixProgram;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Program2 extends Application{

    @Override
    public void start(Stage primaryStage) {
        ListDirectory app = new ListDirectory();

        Scene scene = new Scene(app,300,250);
        primaryStage.setTitle("FileList");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Application.launch(args);
    }
}
