package searchTool;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by binlix26 on 25/03/17.
 *
 * The very class that holds the posting list for the inverted index
 * for this application.
 */
public class Frequency {

    private int frequency; // indicates how many nodes there are in the posting list
    private List<FileInfo> posting = new ArrayList<>();

    public Frequency() {
        this.frequency = 0;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<FileInfo> getPosting() {
        return posting;
    }

    // get an array of ID
    public int[] getFileIDs() {
        int[] ids = new int[this.frequency];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = posting.get(i).getFileID();
        }

        return ids;
    }

    private void addFileInfoNode(int fileId, int position) {
        FileInfo node = new FileInfo(fileId);
        node.addPosition(position);
        posting.add(node);

        // add one more
        this.frequency++;
    }

    public void addPosition(int fileID, int pos) {

        // add one more positions for this term in the current file
        for (FileInfo node : posting
                ) {
            if (node.getFileID() == fileID) {
                node.addPosition(pos);
                return;
            }
        }

        // add a new node for a new file
        // in other words, the first occurrence of this term in a new file
        addFileInfoNode(fileID, pos);
    }

}
