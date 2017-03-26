package searchTool;

import java.io.*;
import java.util.*;

/**
 * Created by binlix26 on 25/03/17.
 */
public class InvertedIndex {

    private int numForFileID = 1;
    TreeMap<Integer, File> indexToFiles = new TreeMap<>();
    Map<String, Frequency> invertedMap = new HashMap<>();

    // make inverted index
    private void indexFile(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        int pos = 0;

        // read through the file
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {

            for (String word : line.split("\\W+") // split line into words array
                    ) {
                String term = word.toLowerCase();
                pos++; // move position by one

                Frequency fre = invertedMap.get(term);

                // first time
                if (fre == null) {
                    fre = new Frequency();
                    invertedMap.put(term, fre);

                }

                fre.addPosition(numForFileID, pos);
            }
        }

        numForFileID++;
    }

    // receive the chosen directory
    public void getFilesForInvertedIndex(File directory) {
        String dirPath = directory.getAbsolutePath();
        fileFinder(dirPath);
    }

    // find all the files under the chosen directory
    private void fileFinder(String path) {
        File dir = new File(path);
        File[] currentFiles = dir.listFiles();

        if (currentFiles != null && currentFiles.length > 0) {
            // loop through current files
            try {
                for (File file :
                        currentFiles) {
                    if (file.isDirectory()) {
                        fileFinder(file.getAbsolutePath());
                    } else {
                        // stores fileID and file object as key value pair for later use
                        indexToFiles.put(numForFileID, file);

                        // make inverted index for the files
                        indexFile(file);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // show the current inverted index for the chosen directory
    public void displayInvertedIndex() {
        for (Map.Entry<String, Frequency> entry : invertedMap.entrySet()
                ) {
            Frequency fre = entry.getValue();
            List<FileInfo> posting = fre.getPosting();

            System.out.print(entry.getKey() + "\t" + fre.getFrequency() + " -> ");
            for (FileInfo node : posting
                    ) {
                System.out.print("[ " + node.getFileID() +
                        ", " + node.getOccurrence() + ":" + node.getPositions().toString() + "], ");
            }
            System.out.println();
        }
    }

    // show the file list under the chosen directory
    public void displayFileMap() {
        Set<Map.Entry<Integer, File>> set = indexToFiles.entrySet();
        Iterator<Map.Entry<Integer, File>> iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, File> entry = iterator.next();
            System.out.println(entry.getKey() + " -> " + entry.getValue().getAbsolutePath());
        }
    }
}
