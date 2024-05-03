package app;

import infra.DictionaryPreProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logic class to calculate words and fulfill Frequency Map
 * Method process returns FreqMap of suitable words
 * Uses Word checker to filter out non-dictionary words.
 */
public class EssayProcessor {
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-zA-Z]{3,}\\b");

    public static Map<String, Integer> process(String essayContent, Set<String> dictionary) {
        Map<String, Integer> wordCounts = new HashMap<>();

        Matcher matcher = WORD_PATTERN.matcher(essayContent);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (dictionary.contains(word))
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

        System.out.print(".");
        return wordCounts;
    }
}
