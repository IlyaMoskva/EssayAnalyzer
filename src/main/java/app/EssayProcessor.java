package app;

import infra.DictionaryPreProcessor;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EssayProcessor {
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-zA-Z]{3,}\\b");
    public static final String FILE = "words.txt";

    /**
     * Logic class to calculate words and fulfill Frequency Map
     * @return FreqMap of suitable words
     * Uses Word checker to filter out non-dictionary words.
     */
    public static Map<String, Integer> process(String essayContent, Set<String> dictionary) {
        Map<String, Integer> wordCounts = new HashMap<>();

        Matcher matcher = WORD_PATTERN.matcher(essayContent);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();
            if (dictionary.contains(word))
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }
        return wordCounts;
    }

    /**
     * Run parallel threads to process all essays and calculate word frequency across all of essays
     * @param essays as a plain text
     * @return Map with words and their frequency
     */
    public static Map<String, Integer> processEssaysConcurrently(List<String> essays) {

        Set<String> dictionary = new DictionaryPreProcessor(FILE).preprocess();

        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available processors
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Create a list to hold Future objects representing the asynchronous tasks
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        // Submit tasks for each essay to the executor
        for (String essay : essays) {
            Callable<Map<String, Integer>> task = () -> process(essay, dictionary);
            futures.add(executor.submit(task));
        }

        // Create a map to store combined word counts from all essays
        Map<String, Integer> combinedWordCounts = new ConcurrentHashMap<>();

        // Wait for all tasks to complete and collect the results
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> wordCounts = future.get();
                // Merge word counts from current essay into the combined map
                wordCounts.forEach((word, count) -> combinedWordCounts.merge(word, count, Integer::sum));
            } catch (InterruptedException | ExecutionException e) {
                System.err.println(e.getMessage());
            }
        }
        executor.shutdown();

        return combinedWordCounts;
    }
}
