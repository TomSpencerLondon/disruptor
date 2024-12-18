package org.example.disruptorservice;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DisruptorTest {

    private Disruptor<MessageEvent> disruptor;

    private CountDownLatch latch;

    @BeforeEach
    public void setup() {
        // Set up the Disruptor before tests
        disruptor = new Disruptor<>(MessageEvent::new, 1024, Executors.defaultThreadFactory());

        // Add handlers before starting the Disruptor
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            System.out.println("Processing: " + event.getMessage());
            latch.countDown(); // Decrement latch on each event

        });

        disruptor.start(); // Start after adding handlers
    }

    @Test
    public void testDisruptorThroughput() {
        // Test logic here
        disruptor.getRingBuffer().publishEvent((event, sequence) -> event.setMessage("Test Message"));
    }

    @Test
    public void testDisruptorPerformance() throws InterruptedException {
        // Number of events to process
        int numMessages = 1_000_000;

        // Initialize the latch
        latch = new CountDownLatch(numMessages);

        // Measure start time
        long startTime = System.currentTimeMillis();

        // Publish messages to the Disruptor
        IntStream.range(0, numMessages).forEach(i -> {
            disruptor.getRingBuffer().publishEvent((event, sequence) -> event.setMessage("Message-" + i));
        });

        // Wait for all messages to be processed
        latch.await();

        // Measure end time
        long endTime = System.currentTimeMillis();

        // Calculate total processing time and throughput
        long totalTimeMillis = endTime - startTime;
        double throughput = numMessages / (totalTimeMillis / 1000.0);

        // Output results
        System.out.println("Processed " + numMessages + " messages in " + totalTimeMillis + " ms");
        System.out.println("Throughput: " + throughput + " messages/sec");
    }


    @Test
    public void testBlockingQueuePerformance() throws InterruptedException {
        // Number of events to process
        int numMessages = 1_000_000;

        // BlockingQueue for message passing
        BlockingQueue<MessageEvent> queue = new LinkedBlockingQueue<>();

        // CountDownLatch to ensure all messages are processed
        CountDownLatch latch = new CountDownLatch(numMessages);

        // Start a consumer thread to process messages
        Thread consumerThread = new Thread(() -> {
            try {
                while (latch.getCount() > 0) {
                    MessageEvent event = queue.take(); // Take message from the queue
                    System.out.println("Processing: " + event.getMessage());
                    latch.countDown();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumerThread.start();

        // Measure start time
        long startTime = System.currentTimeMillis();

        // Publish messages
        for (int i = 0; i < numMessages; i++) {
            MessageEvent event = new MessageEvent();
            event.setMessage("Message-" + i);
            queue.put(event);
        }

        // Wait for all messages to be processed
        latch.await();

        // Measure end time
        long endTime = System.currentTimeMillis();

        // Calculate and log throughput
        long totalTime = endTime - startTime;
        System.out.println("Processed " + numMessages + " messages in " + totalTime + " ms");
        System.out.println("Throughput: " + (numMessages / (totalTime / 1000.0)) + " messages/sec");
    }

}
