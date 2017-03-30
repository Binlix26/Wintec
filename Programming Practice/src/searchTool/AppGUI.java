package searchTool;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private InvertedIndex invertedIndex;
    private PreparedStatement pstmt;
    // TODO: 30/03/17 set checkbox to determine this value
    private boolean isSynonymEnable = true;

    public AppGUI() {

        // assembling each part of the UI
        HBox topArea = getTopArea();
        HBox bottomArea = getBottomArea();

        //pane to hold everything
        BorderPane mainPane = new BorderPane();

        // set the center area
        taResult = new TextArea();
        taResult.setEditable(false);
        taResult.prefWidthProperty().bind(mainPane.widthProperty().subtract(50));
        taResult.prefHeightProperty().bind(mainPane.heightProperty().subtract(25));

        mainPane.setCenter(taResult);
        mainPane.setTop(topArea);
        mainPane.setBottom(bottomArea);

        // bind width and height
        mainPane.prefWidthProperty().bind(this.widthProperty());
        mainPane.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().add(mainPane);

        try {
            pstmt = new DatabaseConnector().initDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void handleBrowse() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        File dir = dirChooser.showDialog(null);

        if (dir != null) {
            String status = dir.getAbsolutePath();

            // make sure the inverted index only contains the file under the current directory
            // clear the records for previously chosen directory
            invertedIndex = new InvertedIndex();

            // get inverted index for the files under the chosen directory
            invertedIndex.getFilesForInvertedIndex(dir);

            lblStatus.setText(status);
            invertedIndex.displayInvertedIndex();
            invertedIndex.displayFileMap();

        }
    }

    private void handleSearch() throws SQLException {
        // NullPointerException will be thrown without this check
        if (invertedIndex == null) {
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

            int[] arrOfIDs = null;

            // TODO: 30/03/17 search synonym database
            if (isSynonymEnable) {

                pstmt.setString(1, term);
                ResultSet resultSet = pstmt.executeQuery();
                // block the thread and wait for result
                if (resultSet.next()) {
                    String key = resultSet.getString(1);
                    String value = resultSet.getString(2);
                    String sysnonyms = key+","+value;

                    arrOfIDs = invertedIndex.getSynonymFilesID(sysnonyms);

                }
            } else {
                // don't need the database
                // put each array of fileIDs for each term in the ArrayList
                arrOfIDs = invertedIndex.getArrayOfFileIDs(term);
            }

            // check if there is any word not in the files
            if ( arrOfIDs != null) {
                idList.add(arrOfIDs);
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
        btSearch.setOnAction(event -> {
            try {
                handleSearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

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
