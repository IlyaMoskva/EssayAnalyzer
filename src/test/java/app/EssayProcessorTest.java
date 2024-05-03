package app;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

    public class EssayProcessorTest {

        @Test
        void testProcessEssay() {

            // Sample essay content
            String essayContent = "This is a sample essay. It contains words like apple, banana, and orange.";

            // Expected word counts
            Map<String, Integer> expectedWordCounts = new HashMap<>();
            expectedWordCounts.put("this", 1);
            expectedWordCounts.put("sample", 1);
            expectedWordCounts.put("essay", 1);
            expectedWordCounts.put("contains", 1);
            expectedWordCounts.put("words", 1);
            expectedWordCounts.put("like", 1);
            expectedWordCounts.put("apple", 1);
            expectedWordCounts.put("banana", 1);
            expectedWordCounts.put("and", 1);
            expectedWordCounts.put("orange", 1);

            Set<String> dictionary = expectedWordCounts.keySet();

            // Process the essay content
            Map<String, Integer> wordCounts = EssayProcessor.process(essayContent, dictionary);

            // Assert that the word counts match the expected values
            assertEquals(expectedWordCounts, wordCounts);
        }

}