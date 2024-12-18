package org.example.disruptorservice;

import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class DisruptorConfig {
    @Bean
    public Disruptor<MessageEvent> disruptor() {
        MessageEventFactory factory = new MessageEventFactory();
        int bufferSize = 262_144;

        Disruptor<MessageEvent> disruptor = new Disruptor<>(
                factory,
                bufferSize,
                Executors.defaultThreadFactory()
        );

        disruptor.handleEventsWith(new MessageEventHandler());
        disruptor.start();

        return disruptor;
    }

    @Bean
    public BlockingQueue<MessageEvent> messageQueue() {
        return new LinkedBlockingQueue<>();
    }
}
