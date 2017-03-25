package searchTool;

import java.io.*;
import java.util.*;

/**
 * Created by binlix26 on 25/03/17.
 */
public class InvertedIndex {

    private int numForFileID = 1;
    Map<String, Frequency> index = new HashMap<>();

    public void indexFile(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        int pos = 0;

        // read through the file
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {

            for (String word : line.split("\\W+") // split line into words array
                    ) {
                String term = word.toLowerCase();
                pos++; // move position by one

                Frequency fre = index.get(term);

                // first time
                if (fre == null) {
                    fre = new Frequency();
                    index.put(term, fre);

                }

                fre.addPosition(numForFileID, pos);
            }
        }

        numForFileID++;
    }

    public void display() {
        for (Map.Entry<String, Frequency> entry: index.entrySet()
             ) {
            Frequency fre = entry.getValue();
            List<FileInfo> posting = fre.getPosting();

            System.out.print(entry.getKey()+"\t"+fre.getFrequency()+" -> ");
            for (FileInfo node: posting
                 ) {
                System.out.print("[ "+node.getFileID()+", "+node.getPositions().toString()+"], ");
            }
            System.out.println();
        }
    }
}
