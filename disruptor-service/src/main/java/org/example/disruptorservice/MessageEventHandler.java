package org.example.disruptorservice;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEventHandler implements EventHandler<MessageEvent> {
    private static final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);

    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        logger.info("Processing message: {} at sequence: {}", event.getMessage(), sequence);

        try {
            Thread.sleep(10); // Optional delay for demonstration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long processingEndTime = System.currentTimeMillis();
        logger.info("Processed message: {} at sequence: {} in {} ms",
                event.getMessage(), sequence, (processingEndTime - event.getStartTime()));
    }
}

