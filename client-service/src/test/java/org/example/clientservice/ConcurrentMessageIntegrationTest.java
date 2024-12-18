package org.example.clientservice;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcurrentMessageIntegrationTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    public void testConcurrentMessageProcessing() throws InterruptedException {
        // Configure WebClient to communicate with the disruptor-service
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8080/api/messages").build();

        // Number of concurrent messages to send
        int numMessages = 100;

        // Create a fixed thread pool to simulate concurrent clients
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Use a CountDownLatch to wait for all messages to be processed
        CountDownLatch latch = new CountDownLatch(numMessages);

        // Track processed messages in a thread-safe list
        List<String> processedMessages = Collections.synchronizedList(new ArrayList<>());

        // Submit tasks to send messages concurrently
        IntStream.range(0, numMessages).forEach(i -> {
            executorService.submit(() -> {
                String message = "Message-" + i;
                try {
                    String response = webClient.post()
                            .uri(uriBuilder -> uriBuilder.queryParam("message", message).build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    // Add the response to the processed list
                    processedMessages.add(response);
                } finally {
                    latch.countDown();
                }
            });
        });

        // Wait for all tasks to complete
        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assertions
        Assertions.assertEquals(numMessages, processedMessages.size(), "Not all messages were processed.");

        // Verify that each message was processed
        IntStream.range(0, numMessages).forEach(i -> {
            Assertions.assertTrue(
                    processedMessages.contains("Message published: Message-" + i),
                    "Message " + i + " was not processed correctly."
            );
        });
    }
}
