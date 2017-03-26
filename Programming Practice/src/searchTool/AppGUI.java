package searchTool;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Created by binlix26 on 26/03/17.
 */
public class AppGUI extends Pane {

    private TextField queryInput;
    private TextArea result;
    private Button btBrowser;
    private Button btSearch;
    private Label lblStatus;
    private StringBuilder stringBuilder;


    public AppGUI() {
        stringBuilder = new StringBuilder();

        HBox topArea = getTopArea();
        ScrollPane forTextArea = getCenterArea();
        HBox bottomArea = getBottomArea();

        //pane to hold everything
        BorderPane mainPane = new BorderPane();

        mainPane.setCenter(forTextArea);
        mainPane.setTop(topArea);
        mainPane.setBottom(bottomArea);

        // bind width and height
        mainPane.prefWidthProperty().bind(this.widthProperty());
        mainPane.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().add(mainPane);

    }

    private void handleBrowse() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        File file = dirChooser.showDialog(null);

        if (file != null) {

        }
    }

    private void handleSearch() {

    }

    private HBox getTopArea() {
        queryInput = new TextField();
        btSearch = new Button("search");

        HBox pane = new HBox(20);
        pane.getChildren().addAll(queryInput, btSearch);
        pane.setPadding(new Insets(5, 5, 5, 10));
        HBox.setMargin(btSearch, new Insets(0, 0, 0, 20));

        queryInput.prefWidthProperty().bind(this.widthProperty().divide(1.5));
        btSearch.setOnAction(event -> handleSearch());

        return pane;
    }

    private HBox getBottomArea() {
        btBrowser = new Button("browser");
        lblStatus = new Label();

        // default information
        lblStatus.setText("No directory is selected.");

        HBox pane = new HBox(20);
        pane.getChildren().addAll(lblStatus, btBrowser);
        pane.setPadding(new Insets(5, 5, 5, 10));
        HBox.setMargin(btBrowser, new Insets(0, 0, 0, 20));

        lblStatus.prefWidthProperty().bind(this.widthProperty().divide(1.5));
        btBrowser.setOnAction(event -> handleBrowse());

        return pane;
    }

    private ScrollPane getCenterArea() {

        result = new TextArea();
        result.setEditable(false);

        ScrollPane pane = new ScrollPane(result);

        pane.setPadding(new Insets(5));
        result.prefWidthProperty().bind(pane.widthProperty().subtract(25));
        result.prefHeightProperty().bind(pane.heightProperty());

        return pane;
    }

}
