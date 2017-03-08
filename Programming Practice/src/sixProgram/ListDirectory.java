package sixProgram;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Created by binlix26 on 6/03/17.
 */
public class ListDirectory extends BorderPane {
    private TextArea taList;
    private Button btBrowser;
    private StringBuilder stringBuilder;

    public ListDirectory() {
        this.taList = new TextArea();
        this.btBrowser = new Button("Browser");
        this.stringBuilder = new StringBuilder(); // store file and directory name

        // bind the text area's height and width with the panel
        taList.prefWidthProperty().bind(this.widthProperty().subtract(15));
        taList.prefHeightProperty().bind(this.heightProperty().subtract(20));

        // properties for the text area
        taList.setStyle("-fx-border-color: green");
        taList.setFont(new Font("Serif", 14));
        taList.setWrapText(true);
        taList.setEditable(false);

        ScrollPane scroll = new ScrollPane(taList);
        setCenter(scroll);
        setBottom(btBrowser);
        //set Alignment
        BorderPane.setAlignment(btBrowser, Pos.BOTTOM_RIGHT);
        BorderPane.setAlignment(taList, Pos.CENTER);

        // register the event handler
        btBrowser.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(null);
            if (file != null) {
                getList(file.getAbsolutePath(), 0); // retrieve all by recursion
                setListInfo(); // show the result
            }
        });
    }

    public void setListInfo() {
        taList.setText(this.stringBuilder.toString());
    }

    public void getList(String dirPath, int level) {
        File dir = new File(dirPath);
        File[] currentLevelFiles = dir.listFiles();
        if (currentLevelFiles != null && currentLevelFiles.length > 0) {
            for (File file : currentLevelFiles) {
                for (int i = 0; i < level; i++) {
                    // the indentation is for sub folders
                    stringBuilder.append("\t");
                }
                if (file.isDirectory()) {
                    stringBuilder.append("[")
                            .append(file.getName())
                            .append("]\n");
                    getList(file.getAbsolutePath(), level + 1);
                } else {
                    stringBuilder.append(file.getName()).append("\n");
                }
            }
        }
    }
}
