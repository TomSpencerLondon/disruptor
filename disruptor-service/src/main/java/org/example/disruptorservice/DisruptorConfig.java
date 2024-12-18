package org.example.disruptorservice;

import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executors;

@Configuration
public class DisruptorConfig {
    @Bean
    public Disruptor<MessageEvent> disruptor() {
        MessageEventFactory factory = new MessageEventFactory();
        int bufferSize = 1024;

        Disruptor<MessageEvent> disruptor = new Disruptor<>(
                factory,
                bufferSize,
                Executors.defaultThreadFactory()
        );

        disruptor.handleEventsWith(new MessageEventHandler());
        disruptor.start();

        return disruptor;
    }
}
