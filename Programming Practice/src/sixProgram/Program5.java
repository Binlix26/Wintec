package sixProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by binlix26 on 9/03/17.
 */
public class Program5 {
    public static void main(String[] args) {
        File source = new File("input.txt");
        HashMap<String,Integer> hashMap = null;

        try {
            hashMap = getMap(source);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(hashMap);

    }

    private static HashMap<String, Integer> getMap(File source) throws FileNotFoundException {
        HashMap<String,Integer> map = new HashMap<>();
        Scanner scanner = new Scanner(source);
        String key;

        while (scanner.hasNext()) {
            // eliminate the punctuations and make it lowercase
            key = clean(scanner.next());

            if (map.containsKey(key)) {
                int value = map.get(key);
                value++;
                map.put(key,value);
            } else {
                map.put(key,1);
            }
        }

        return map;
    }

    private static String clean(String text) {
        text = text.replaceAll("\\W","").toLowerCase();
        return text;
    }
}
