package searchTool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by binlix26 on 25/03/17.
 *
 * The entry of this search tool software.
 */
public class SearchApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppGUI app = new AppGUI();
        Scene scene = new Scene(app,450,250);
        primaryStage.setTitle("Search Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    // for test the inverted index at the beginning
    /*public static void main(String[] args) {
        String root = "/Users/binlix26/Documents/test search/doc";
        File[] files = new File[4];
        for (int i = 0; i < files.length; i++) {
            String path = root + (i+1) + ".txt";
            files[i] = new File(path);
        }

        InvertedIndex app = new InvertedIndex();

        for (int i = 0; i < files.length; i++) {
            try {
                app.indexFile(files[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        app.display();

        Application.launch(args);
    }*/
}
