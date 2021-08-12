package com.vass.talks.kafka.serdes;

import com.vass.talks.kafka.model.LocationStats;
import com.vass.talks.kafka.model.SensorData;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public final class DataSerdes {

    static public final class SensorDataSerde extends Serdes.WrapperSerde<SensorData> {
        public SensorDataSerde() {
            super(new JsonSerializer<>(),
                    new JsonDeserializer<>(SensorData.class));
        }
    }
    static public final class LocationStatsSerde extends Serdes.WrapperSerde<LocationStats> {
        public LocationStatsSerde() {
            super(new JsonSerializer<>(),
                    new JsonDeserializer<>(LocationStats.class));
        }
    }
    public static Serde<SensorData> SensorData() {
        return new DataSerdes.SensorDataSerde();
    }
    public static Serde<LocationStats> LocationStats() {
        return new DataSerdes.LocationStatsSerde();
    }
}
