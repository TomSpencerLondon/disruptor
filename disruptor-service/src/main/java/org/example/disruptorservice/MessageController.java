package org.example.disruptorservice;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final BlockingQueue<MessageEvent> messageQueue;
    private final RingBuffer<MessageEvent> ringBuffer;

    @Autowired
    public MessageController(BlockingQueue<MessageEvent> messageQueue, Disruptor<MessageEvent> disruptor) {
        this.messageQueue = messageQueue;
        this.ringBuffer = disruptor.getRingBuffer();
    }

    @PostMapping
    public String publishMessage(@RequestParam String message) {
        long startTime = System.currentTimeMillis();
        logger.info("Received message: {}", message);
        long sequence = ringBuffer.next();
        try {
            MessageEvent event = ringBuffer.get(sequence);
            event.setMessage(message);
            event.setStartTime(startTime); // Add a timestamp to the event
            logger.info("Enqueued message: {} at sequence: {}", message, sequence);

        } finally {
            ringBuffer.publish(sequence);
        }

        return "Message published: " + message;
    }

    @PostMapping("/queue")
    public String publishMessageToQueue(@RequestParam String message) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("Received message for queue: {}", message);
            MessageEvent event = new MessageEvent();
            event.setMessage(message);
            event.setStartTime(startTime);
            messageQueue.put(event); // Add the MessageEvent to the BlockingQueue
            logger.info("Enqueued message to queue: {}", message);
            return "Message published to queue: " + message;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Failed to publish message to queue: " + message;
        }
    }
}

