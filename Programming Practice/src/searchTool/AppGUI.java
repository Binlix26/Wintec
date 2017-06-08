package searchTool;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by binlix26 on 26/03/17.
 * <p>
 * This code snippet is responsible for assembling all the
 * components for the UI, defining the event handler as well as
 * searching the database if needed.
 */
public class AppGUI extends Pane {

    private TextField queryInput;
    private TextArea taResult;
    private Label lblStatus;
    private InvertedIndex invertedIndex;
    private Connection conn;
    private boolean isSynonymEnable = false;

    public AppGUI() {

        // assembling each part of the UI
        HBox topArea = getTopArea();
        HBox bottomArea = getBottomArea();
        VBox rightArea = getRightArea();

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
        mainPane.setRight(rightArea);

        // bind width and height
        mainPane.prefWidthProperty().bind(this.widthProperty());
        mainPane.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().add(mainPane);

        // never block the main thread from listening the event from users
        new Thread(() -> {
            try {
                conn = new DatabaseConnector().initDB();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleBrowse() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        File dir = dirChooser.showDialog(null);

        if (dir != null) {
            String status = dir.getAbsolutePath();

            // make sure the inverted index only contains the file under the current directory
            // clear the records for previously chosen directory
            // never affects the main Thread responding the UI event
            new Thread(() -> {

                invertedIndex = new InvertedIndex();

                // get inverted index for the files under the chosen directory
                invertedIndex.getFilesForInvertedIndex(dir);

                Platform.runLater(() -> {
                    lblStatus.setText(status);
                });

                invertedIndex.displayInvertedIndex();
                invertedIndex.displayFileMap();
            }).start();

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

        //refresh the map: fileId = words ( the map is for better displaying results )
        invertedIndex.refreshIndexToWords();

        // prepare for searching the database
        PreparedStatement statement = null;
        if (isSynonymEnable) {
            String sql = "SELECT * FROM Words WHERE word = ?";
            statement = conn.prepareStatement(sql);
            System.out.println("isSynonymEnable is: " + isSynonymEnable);
        }

        // deal with the term list
        for (String word :
                queryList.split("\\W+")) {

            // stemming the word before it is searched as a key in the map
            String term = WordNormalization.normalize(word.toLowerCase());

            int[] arrOfIDs = null; // file IDs contain this term

            // search synonym database if necessary
            if (isSynonymEnable) {

                statement.setString(1, term);
                ResultSet resultSet = statement.executeQuery();
                System.out.println("<----- Querying ----->");

                if (resultSet.next()) {
                    String key = resultSet.getString(1);
                    String value = resultSet.getString(2);
                    String synonyms = key + "," + value;

                    System.out.println("Data from the database: ");
                    System.out.println(key + " -> " + value + "\n");

                    arrOfIDs = invertedIndex.getSynonymFilesID(synonyms);

                } else {
                    // some of the words do not exist as primary key in the database
                    // so search files for the term no matter what
                    System.out.println("There is NO DATA back!");
                    System.out.println(term + " is not registered in the database." + "\n");

                    arrOfIDs = invertedIndex.getArrayOfFileIDs(term);
                }

            } else {
                // don't need the database
                // put each array of fileIDs for each term in the ArrayList
                arrOfIDs = invertedIndex.getArrayOfFileIDs(term);
            }

            // check if there is any word not in the files
            if (arrOfIDs != null) {
                idList.add(arrOfIDs);
            } else {
                taResult.setText("No match has been found!");
                return;
            }

        }

        // when user is only entering one word
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

    private void handleUpdate(String keyWord, String synonym) throws SQLException {
        String selectSQL = "SELECT * FROM Words WHERE word = ?";
        String updateSQL = "UPDATE Words SET Synonyms = ? WHERE word = " + "'" + keyWord + "'";
        String insertSQL = "INSERT INTO Words (Word,Synonyms) VALUES (?,?)";

        PreparedStatement queryStat = conn.prepareStatement(selectSQL);
        queryStat.setString(1, keyWord);
        queryStat.executeQuery();
        ResultSet resultSet = queryStat.getResultSet();

        // for later inserting or updating
        PreparedStatement statement;

        if (resultSet.next()) {
            statement = conn.prepareStatement(updateSQL);

//            String keyFromDB = resultSet.getString(1);
            String synonymsFromDB = resultSet.getString(2);

            System.out.println("Result from Database ====> ");
            System.out.println(keyWord + " -> " + synonymsFromDB);
            System.out.println("<----- Updating ------>");
            System.out.println("The word to be added: " + synonym);

            // do not have to check repetitiveness by doing this
            Set<String> tempSynonyms = new HashSet<>(Arrays.asList(synonymsFromDB.split("\\W+")));
            // add user input
            tempSynonyms.add(synonym);

            StringBuilder stringBuilder = new StringBuilder();

            for (String target :
                    tempSynonyms) {
                stringBuilder.append(target).append(",");
            }

            int lastIndex = stringBuilder.length();
            stringBuilder.delete(lastIndex - 1, lastIndex);

            statement.setString(1, stringBuilder.toString());
            statement.executeUpdate();

            System.out.println(keyWord + " -> " + stringBuilder.toString() + "\n");

        } else {
            System.out.println(keyWord + " is not registered to the Database!");
            System.out.println("<------ Doing Inserting ----->");
            statement = conn.prepareStatement(insertSQL);
            statement.setString(1, keyWord);
            statement.setString(2, synonym);
            statement.executeUpdate();
            System.out.println(keyWord + " -> " + synonym + "\n");
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
                    .append("\n")
                    .append("Contains -> ")
                    .append(invertedIndex.getDetailedResult(fileID))
                    .append("\n");
        }

        taResult.setText(stringBuilder.toString());
    }

    private HBox getTopArea() {
        queryInput = new TextField();
        Button btSearch = new Button("Search");

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
        Button btBrowser = new Button("Browser");
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

    private VBox getRightArea() {
        VBox pane = new VBox(15);

        pane.setPadding(new Insets(5));
        pane.setStyle("-fx-border-color: green");

        RadioButton enableDB = new RadioButton("Enable");
        RadioButton disableDB = new RadioButton("Disable");
        Button btUpdate = new Button("UpdateDB");
        pane.getChildren().addAll(new Label("Database: "), enableDB, disableDB, btUpdate);

        disableDB.setSelected(true);

        ToggleGroup group = new ToggleGroup();
        enableDB.setToggleGroup(group);
        disableDB.setToggleGroup(group);

        // event handle
        enableDB.setOnAction(event -> {
            if (enableDB.isSelected())
                this.isSynonymEnable = true;
            // debug purpose
            System.out.println("Inside the EventHandling, Enable DB is :" + isSynonymEnable + "\n");
        });

        disableDB.setOnAction(event -> {
            if (disableDB.isSelected())
                this.isSynonymEnable = false;
            // debug purpose
            System.out.println("Inside the EventHandling, Enable DB is :" + isSynonymEnable + "\n");
        });

        btUpdate.setOnAction(event -> {
            GridPane updatePane = new GridPane();

            updatePane.setAlignment(Pos.CENTER);
            updatePane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
            updatePane.setHgap(5.5);
            updatePane.setVgap(5.5);

            TextField tfKeyWord = new TextField();
            TextField tfSynonym = new TextField();

            updatePane.add(new Label("Key Word:"), 0, 0);
            updatePane.add(tfKeyWord, 1, 0);
            updatePane.add(new Label("Synonym:"), 0, 1);
            updatePane.add(tfSynonym, 1, 1);

            Button btAdd = new Button("Add Synonym");

            Label lblStatus = new Label("please enter one synonym per time.");
            lblStatus.setTextFill(Color.BLUE);
            lblStatus.setStyle("-fx-font-family: sans-serif");

            updatePane.add(lblStatus, 1, 3);
            updatePane.add(btAdd, 1, 4);
            GridPane.setHalignment(btAdd, HPos.RIGHT);

            btAdd.setOnAction(event1 -> {
                Boolean isSucceed = true;
                String keyWord = tfKeyWord.getText();
                String synonym = tfSynonym.getText();

                if (keyWord.equals("") || synonym.equals("")) {
                    lblStatus.setText("Both fields must be filled out.");
                    return;
                }

                if ((keyWord.split("\\W+").length != 1) ||
                        (synonym.split("\\W+").length != 1)) {
                    lblStatus.setText("One key word and One synonym per time.");
                    return;
                }

                keyWord = WordNormalization.normalize(keyWord);
                synonym = WordNormalization.normalize(synonym);

                try {
                    handleUpdate(keyWord, synonym);
                } catch (SQLException e) {
                    isSucceed = false;
                    e.printStackTrace();
                }

                if (isSucceed)
                    lblStatus.setText("Update Successful!");
                else
                    lblStatus.setText("Failed, Try again!");

            });

            Stage stage = new Stage();
            Scene scene = new Scene(updatePane, 450, 200);
            stage.setScene(scene);
            stage.setTitle("Update Database");
            stage.show();
        });

        return pane;
    }
}
