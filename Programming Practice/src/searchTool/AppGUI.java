package searchTool;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.*;

/**
 * Created by binlix26 on 26/03/17.
 */
public class AppGUI extends Pane {

    private TextField queryInput;
    private TextArea taResult;
    private Button btBrowser;
    private Button btSearch;
    private Label lblStatus;
    private InvertedIndex invertedIndex = new InvertedIndex();

    public AppGUI() {

        // assembling each part of the UI
        HBox topArea = getTopArea();
        HBox bottomArea = getBottomArea();

        //pane to hold everything
        BorderPane mainPane = new BorderPane();

        // set the center area
        taResult = new TextArea();
        taResult.setEditable(false);
        taResult.prefWidthProperty().bind(mainPane.widthProperty().subtract(25));
        taResult.prefHeightProperty().bind(mainPane.heightProperty().subtract(25));

        mainPane.setCenter(taResult);
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
            String status = file.getAbsolutePath();

            // get inverted index for the files under the chosen directory
            invertedIndex.getFilesForInvertedIndex(file);

            lblStatus.setText(status);
            invertedIndex.displayInvertedIndex();
            invertedIndex.displayFileMap();

        }
    }

    private void handleSearch() {
        // NullPointerException will be thrown without this check
        if (invertedIndex.isEmpty()) {
            taResult.setText("Please choose a directory before you search!");
            return;
        }

        // get text from user
        String queryList = queryInput.getText();

        if (queryList.equals("")) {
            taResult.setText("Please enter your query before searching.");
            return;
        }

        // to hold the fileIDs that contain all the terms in the query
        ArrayList<int[]> idList = new ArrayList<>();

        // deal with the term list
        for (String word :
                queryList.split("\\W+")) {

            // stemming the word before it is searched as a key in the map
            String term = WordNormalization.normalize(word.toLowerCase());

            // put each array of fileIDs for each term in the ArrayList
            if (invertedIndex.getArrayOfFileIDs(term) != null) {
                idList.add(invertedIndex.getArrayOfFileIDs(term));
            } else {
                taResult.setText("No match has been found!");
                return;
            }
        }

        if (idList.size() == 1) {
            // pass the list and show the result
            ArrayList<Integer> listIntersection = new ArrayList<>();
            int[] temp = idList.get(0);
            for (int i = 0; i < temp.length; i++) {
                listIntersection.add(temp[i]);
            }

            showResult(listIntersection);
            return;
        }

        // intersections of the fileID need to be found!
        ArrayList<Integer> intersection = getFileIntersection(idList);

        if (intersection.size() != 0) {
            showResult(intersection);
        } else {
            taResult.setText("No match has been found!");
        }
    }

    private ArrayList<Integer> getFileIntersection(ArrayList<int[]> list) {
        ArrayList<Integer> intersection = new ArrayList<>();

        int[] temp = list.get(0);
        for (int i = 0; i < temp.length; i++) {
            intersection.add(temp[i]);
        }

        for (int i = 1; i < list.size(); i++) {
            intersection = getIntersectionBetweenTwo(intersection, list.get(i));
        }

        return intersection;
    }

    // only will be invoked by getFileIntersection
    private ArrayList<Integer> getIntersectionBetweenTwo(ArrayList<Integer> list, int[] arr) {
        ArrayList<Integer> target = new ArrayList<>();
        for (Integer iNum :
                list) {
            for (int i = 0; i < arr.length; i++) {
                if (iNum == arr[i])
                    target.add(iNum);
            }
        }
        return target;
    }

    private void showResult(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The list of results is: ").append("\n");
        for (Integer fileID :
                list) {
            stringBuilder.append(invertedIndex.getFilePathById(fileID))
                    .append("\n");
        }

        taResult.setText(stringBuilder.toString());
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

}
