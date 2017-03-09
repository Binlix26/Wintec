package sixProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by binlix26 on 9/03/17.
 *
 * remaining problems:
 * 1. find out how to eliminate "" in the map;
 * 2. Analysis
 *
 */
public class Program5 {
    public static void main(String[] args) {
        File source = new File("input.txt");
        HashMap<String, Integer> hashMap = null;

        try {
            hashMap = getMap(source);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(hashMap);

        findOccurrence(hashMap);

    }

    private static HashMap<String, Integer> getMap(File source) throws FileNotFoundException {
        HashMap<String, Integer> map = new HashMap<>();
        Scanner scanner = new Scanner(source);
        String key;

        while (scanner.hasNext()) {
            // eliminate the punctuations and make it lowercase
            key = clean(scanner.next());


            if (key != "" && map.containsKey(key)) {
                int value = map.get(key);
                value++;
                map.put(key, value);
            } else {
                map.put(key, 1);
            }
        }

        return map;
    }

    private static String clean(String text) {
        text = text.replaceAll("\\W", "").toLowerCase();
        return text;
    }

    public static void findOccurrence(HashMap<String, Integer> hashMap) {
        int maxValueInMap = Collections.max(hashMap.values());
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()
                ) {
            if (entry.getValue() == maxValueInMap)
                System.out.println(entry.getKey() + " = " + maxValueInMap);
        }
    }
}
