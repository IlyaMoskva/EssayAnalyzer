package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UrlReader {

    public static List<String> readUrls(String filePath) {
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String url;
            while ((url = reader.readLine()) != null) {
                urls.add(url);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return urls;
    }
}
