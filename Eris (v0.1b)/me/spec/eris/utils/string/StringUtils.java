package me.spec.eris.utils.string;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils {

    public static int findIndexInString(String searchString, String wordsToFind) {
        List<String> words = Arrays.asList(wordsToFind);

        int index = 0;
        int result = -1;
        String match = null;

        StringTokenizer tokenizer = new StringTokenizer(searchString, " ", true);

        while(result < 0 && tokenizer.hasMoreElements()) {
            String next = tokenizer.nextToken();

            if(words.contains(next)) {
                result = index;
                match = next;
            } else {
                index += next.length();
            }
        }

        if(match == null) {
            System.out.println("Not found.");
        } else {
            System.out.println("Found '" + match + "' at index: " + result);
            return result;
        }
        return 0;
    }
}
