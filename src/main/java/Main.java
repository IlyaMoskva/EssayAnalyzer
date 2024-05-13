import java.util.*;

import app.EssayProcessor;
import app.WebPageParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.FilePathFinder;
import io.UrlReader;

public class Main {

    public static final String ENDG_URLS_FILE = "endg-urls-short.txt";
    public static final int NUMBER_OF_TOP_WORDS = 10;

    public static void main(String[] args) {

        List<String> urls = UrlReader.readUrls(FilePathFinder.getFilePath(ENDG_URLS_FILE));
        System.out.println(urls.size() + " urls read.");

        // Parse web pages in parallel to get essays as a text
        List<String> essays = WebPageParser.parseWebPages(urls);
        System.out.println("Parser done, got " + essays.size() + " essays.");

        // Process essays concurrently
        Map<String, Integer> wordCounts = EssayProcessor.processEssaysConcurrently(essays);

        // Identify top 10 words across all essays
        List<Map.Entry<String, Integer>> topWords = identifyTopWords(wordCounts);

        // Convert results to JSON format
        String jsonOutput = convertToJSON(topWords);

        // Print JSON output to stdout
        System.out.println(jsonOutput);
    }

    private static List<Map.Entry<String, Integer>> identifyTopWords(Map<String, Integer> wordCounts) {
        // Sort the word counts map by value in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordCounts.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Select the top 10 words
        return sortedEntries.subList(0, Math.min(NUMBER_OF_TOP_WORDS, sortedEntries.size()));
    }

    /**
     * Beautification of result
     * @param topWords
     * @return
     */
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
