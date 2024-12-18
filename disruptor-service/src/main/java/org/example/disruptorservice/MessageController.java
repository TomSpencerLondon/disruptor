package org.example.disruptorservice;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final RingBuffer<MessageEvent> ringBuffer;

    @Autowired
    public MessageController(Disruptor<MessageEvent> disruptor) {
        this.ringBuffer = disruptor.getRingBuffer();
    }

    @PostMapping
    public String publishMessage(@RequestParam String message) {
        logger.info("Received message: {}", message);

        long sequence = ringBuffer.next();
        try {
            MessageEvent event = ringBuffer.get(sequence);
            event.setMessage(message);
            logger.info("Message placed in ring buffer at sequence: {}", sequence);

        } finally {
            ringBuffer.publish(sequence);
        }

        return "Message published: " + message;
    }
}

