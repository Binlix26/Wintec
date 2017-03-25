package searchTool;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by binlix26 on 25/03/17.
 */
public class SearchApp extends Application {
    @Override
    public void start(Stage primaryStage) {

    }

    public static void main(String[] args) {
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
    }
}
