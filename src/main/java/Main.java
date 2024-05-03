import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import app.EssayProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infra.DictionaryPreProcessor;
import infra.EssayExtractor;
import io.FilePathFinder;
import io.UrlReader;

public class Main {

    public static void main(String[] args) {

        List<String> urls = UrlReader.readUrls(FilePathFinder.getFilePath("endg-urls-short.txt"));
        System.out.println(urls.size() + " urls read.");

        // Parse web pages in parallel
        List<String> essays = parseWebPages(urls);
        System.out.println("Parser done, got " + essays.size() + " essays.");

        // Process essays concurrently
        Map<String, Integer> wordCounts = processEssaysConcurrently(essays);

        // Identify top 10 words across all essays
        List<Map.Entry<String, Integer>> topWords = identifyTopWords(wordCounts);

        // Print top 10 words
        topWords.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Convert results to JSON format
        String jsonOutput = convertToJSON(topWords);

        // Print JSON output to stdout
        System.out.println(jsonOutput);
    }

    private static List<String> parseWebPages(List<String> urls) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<String>> futures = new ArrayList<>();
        for (String url : urls) {
            Future<String> future = executor.submit(() -> EssayExtractor.extract(url));
            futures.add(future);
        }

        List<String> essays = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        executor.shutdown();
        return essays;
    }

    private static Map<String, Integer> processEssaysConcurrently(List<String> essays) {

        Set<String> dictionary = new DictionaryPreProcessor("words.txt").preprocess();

        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available processors
        // Create an ExecutorService with a number of available threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Create a list to hold Future objects representing the asynchronous tasks
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        // Submit tasks for each essay to the executor
        for (String essay : essays) {
            Callable<Map<String, Integer>> task = () -> EssayProcessor.process(essay, dictionary);
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

    private static List<Map.Entry<String, Integer>> identifyTopWords(Map<String, Integer> wordCounts) {
        // Sort the word counts map by value in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordCounts.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Select the top 10 words
        return sortedEntries.subList(0, Math.min(10, sortedEntries.size()));
    }

    private static String convertToJSON(List<Map.Entry<String, Integer>> topWords) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        for (Map.Entry<String, Integer> entry : topWords) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("word", entry.getKey());
            jsonObject.addProperty("count", entry.getValue());
            jsonArray.add(jsonObject);
        }

        return gson.toJson(jsonArray);
    }

}
