package searchTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by binlix26 on 25/03/17.
 */
public class Frequency {

    private int frequency;
    private Set<Integer> files = new HashSet<>();
    List<FileInfo> posting = new ArrayList<FileInfo>();

    public Frequency() {
        this.frequency = 0;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<FileInfo> getPosting() {
        return posting;
    }

    private void addFileInfoNode(int fileId, int position) {
        FileInfo node = new FileInfo(fileId);
        node.addPosition(position);
        posting.add(node);

        // put a mark for this file
        files.add(fileId);
        this.frequency++;
    }

    public void addPosition(int fileID, int pos) {
        if (files.contains(fileID)) {

            // for the first occurrence of in a new file that has already in the hash table
            // add another node for the terms already in the hash table
            for (FileInfo node : posting
                    ) {
                if (node.getFileID() == fileID) {
                    node.addPosition(pos);
                    break;
                }
            }

        } else {
            addFileInfoNode(fileID, pos);
        }

    }
}
