package app;

import infra.EssayExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class WebPageParser {

    public static List<String> parseWebPages(List<String> urls) {
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
                System.err.println(e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());

        executor.shutdown();
        return essays;
    }
}
