package com.manning.energy.battery.application.cli;

import com.manning.energy.battery.application.api.DeviceStateEvent;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import com.manning.energy.battery.generated.DeviceEventRow;
import com.manning.energy.battery.util.KafkaUtil;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class IngestTopology {
    static private Logger logger = Logger.getLogger(IngestTopology.class);

    public static Topology build(final DeviceStateDAO dao, String topicIn, String topicOut,
                                 SchemaRegistryClient schemaRegistryClient,
                                 String schemaRegistryUrl) {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, DeviceEventRow> EnergyEventStream = builder
                .stream(topicIn, Consumed.with(Serdes.String(),
                        getAvroSerde(schemaRegistryClient, schemaRegistryUrl)));

        KStream<String, DeviceStateEvent> transformedStream = EnergyEventStream
                .mapValues(
                        (key, value) -> {
                            logger.info(" inserting into DB " + key);

                            final DeviceStateEvent state = new DeviceStateEvent(value);
                            dao.createOrUpdate(state.getDeviceId(), state.getCharging());
                            return state;
                        }
                );

        Serde<DeviceStateEvent> serdeJson = KafkaUtil.jsonSerde();
        transformedStream.to(topicOut, Produced.with(Serdes.String(), serdeJson));

        return builder.build();

    }

    static <T extends SpecificRecord> SpecificAvroSerde<T> getAvroSerde(SchemaRegistryClient client, String url) {
        final SpecificAvroSerde<T> avroSerde = new SpecificAvroSerde<>(client);
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, url);
        avroSerde.configure(serdeConfig, false);
        return avroSerde;
    }


}
