package infra;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static io.FilePathFinder.getFilePath;

public class DictionaryPreProcessor {

    private String dictionaryFilePath;

    public DictionaryPreProcessor(String dictionaryFilePath) {
        this.dictionaryFilePath = getFilePath(dictionaryFilePath);
    }

    public Set<String> preprocess() {
        // Number of threads to use
        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available processors

        // Executor service for concurrent processing
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        Set<String> dictionary = new HashSet<>();
        // Original file contains 466.650 lines
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilePath))) {
            List<Future<Set<String>>> futures = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                // Submit tasks to process each line concurrently
                String finalLine = line;
                Future<Set<String>> future = executor.submit(() -> processLine(finalLine));
                futures.add(future);
            }

            // Collect results from the futures
            for (Future<Set<String>> future : futures) {
                dictionary.addAll(future.get()); // Merge results into the main set
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println(e.getMessage());
        } finally {
            executor.shutdown();
        }

        System.out.println("Preprocessed dictionary contains " + dictionary.size() + " words."); // For given file 415701
        return dictionary;
    }

    private static Set<String> processLine(String line) {
        Set<String> validWords = new HashSet<>();
        String[] words = line.split("\\s+");
        for (String word : words) {
            if (isValidWord(word)) {
                validWords.add(word);
            }
        }
        return validWords;
    }

    private static boolean isValidWord(String word) {
        return word.matches("[a-zA-Z]{3,}"); // At least 3 characters, only alphabetic
    }
}
