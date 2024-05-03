package io;

import java.net.URL;
import java.nio.file.Paths;

public class FilePathFinder {

    public static String getFilePath(String fileName) {
        // Get the URL of the resource directory
        URL resourceDir = FilePathFinder.class.getResource("/");

        if (resourceDir != null) {
            // Construct the file path for the file in the resources folder
            String filePath = Paths.get(resourceDir.getPath().substring(1), fileName).toString();

            System.out.println("File path: " + filePath);
            return filePath;
        } else {
            System.err.println("Resource directory not found.");
        }
        return "";
    }
}
