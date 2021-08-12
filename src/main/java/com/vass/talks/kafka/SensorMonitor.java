package com.vass.talks.kafka;

import com.vass.talks.kafka.model.LocationStats;
import com.vass.talks.kafka.model.SensorData;
import com.vass.talks.kafka.serdes.DataSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;

/**
 * Sensor monitor -
 */
public class SensorMonitor {

    public static final String APPLICATION_ID = "SensingMonitor_APP";
    public static final String CLIENT_ID = "SensingMonitor_CLIENT";
    public static final long COMMIT_INTERVAL = 10000;

    public static final String SENSOR_IN_TOPIC = "sensors";
    public static final String MONITOR_OUT_TOPIC = "monitor";

    public static void main(String[] args) {
        //default kafka location
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:29092";
        // Configure the Streams application.
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);
        // Define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        createTopology(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        //Clean local state before start
        streams.cleanUp();
        //Begin processing
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Create stream topology (in/processors/out)
     * @param builder
     */
    private static void createTopology(final StreamsBuilder builder) {
        //Create INPUT STREAM - sensor data from different locations
        final KStream<String, SensorData> sensorStream = builder.stream(SENSOR_IN_TOPIC, Consumed.with(Serdes.String(), DataSerdes.SensorData()));

        //Create a TABLE with counts and sum for each location
        KTable<String, LocationStats> locationStatsKTable = sensorStream
                .map((key, value) -> KeyValue.pair(value.getLocation(), value))
                .groupByKey()
                .aggregate(
                        LocationStats::new,
                        (key, value, aggregateLoc) ->
                                new LocationStats()
                                        .name(key)
                                        .accumulate(aggregateLoc.getCount() + 1, aggregateLoc.getSum() + value.getValue()),
                        Materialized.with(Serdes.String(), DataSerdes.LocationStats())
                );
        //Output average to OUT STREAM
        locationStatsKTable.toStream().map((k, v) -> new KeyValue<>(k, ( v.getSum() / v.getCount() )))
                .to(MONITOR_OUT_TOPIC, Produced.with(Serdes.String(), Serdes.Double()));
    }

    /**
     * Stream configurator
     * @param bootstrapServers
     * @return
     */
    static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, DataSerdes.SensorData().getClass().getName());
        // Records should be flushed every 10 seconds. This is less than the default
        // in order to keep this example interactive.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, COMMIT_INTERVAL);
        // For illustrative purposes we disable record caches.
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return streamsConfiguration;
    }

}
