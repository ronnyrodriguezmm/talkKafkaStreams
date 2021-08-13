package com.vass.talks.kafka.sp;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaConsumer {
    @Bean
    public Consumer<KStream<String, Double>> monitorConsumer(){
        return stream -> stream.foreach((key, value) -> System.out.println(key+" -> " + value));
    };
}
