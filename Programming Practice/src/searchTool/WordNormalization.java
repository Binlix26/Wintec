package searchTool;

/**
 * Created by binlix26 on 28/03/17.
 */
public class WordNormalization {
    private static Stemmer stemmer = new Stemmer();

    private WordNormalization() {

    }

    // use the Porter stemmer Class to process the term
    public static String normalize(String term) {
        Stemmer stemmer = new Stemmer();

        char[] chs = term.toCharArray();
        stemmer.add(chs, chs.length);
        stemmer.stem();

        String termRoot;
        termRoot = new String(
                stemmer.getResultBuffer(), 0, stemmer.getResultLength());

        return termRoot;

    }
}
