import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Infrastructure class to get text of Essay from specific site
 * Extract text of essay split by paragraphs
 * @Method extract Returns plain String
 *
 */
public class EssayExtractor {
    public String extract(String url) {
        // Initialize a StringBuilder to store the extracted text
        StringBuilder sb = new StringBuilder();

        try {
            // Fetch the HTML content from the URL
            Document document = Jsoup.connect(url).get();

            // Select the main content element using CSS selector
            Elements mainContentElements = document.select("div.caas-body");

            // Iterate over the selected elements and extract text
            for (Element element : mainContentElements) {
                // Exclude certain elements by class or ID if necessary
                Elements paragraphs = element.select("p");
                for (Element paragraph : paragraphs) {
                    sb.append(paragraph.text()).append("\n");
                }
            }
            //TODO: Remove the following two debug lines
            //System.out.println(sb.toString());
            //System.out.println(sb.toString().substring(sb.length()-20));
        } catch (IOException e) {
            System.err.println("Can't get content with given URL, reason: " + e.getMessage());
        }
        return sb.toString();
    }
}
