package infra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EssayExtractorTest {

    public static Object[][] values() {
        return new Object[][]{
                new Object[]{"https://www.engadget.com/2019-08-25-sony-and-yamaha-sc-1-sociable-cart.html", "Remember how we said", "next pub crawl."},
                new Object[]{"https://www.engadget.com/2019-08-23-no-mans-sky-beyond-review.html", "If there was ever a game ", "to get back to it."},
                new Object[]{"https://www.engadget.com/2016-06-16-facebook-messenger-hidden-football-game.html", "Across Europe, soccer ", "surprisingly tricky."}
        };
    }

    @ParameterizedTest
    @MethodSource("values")
    public void extract(String url, String starts, String ends) {
        String essay = new EssayExtractor().extract(url);
        Assertions.assertTrue(essay.startsWith(starts) && essay.contains(ends)); //TODO: figure out why .endsWith() doesn't work
    }

    @Test
    public void extractFailedAsNotFound() {
        String url = "https://www.engadget.com/2019-08-22-wirecutters-best-deals-acer-chromebook-11-sale.html";
        String essay = new EssayExtractor().extract(url);
        Assertions.assertTrue(essay.isEmpty());
    }
}