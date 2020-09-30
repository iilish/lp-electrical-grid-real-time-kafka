package com.manning.energy.battery.application.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manning.energy.battery.generated.DeviceEventRow;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class KafkaFactory {

    String serversUrl;
    private String schemaRegistryUrl;

    @JsonProperty
    public String getServersUrl() {
        return serversUrl;
    }


    @JsonProperty
    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public KafkaProducer<String, DeviceEventRow> buildProducer(Environment environment) {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DeviceEventProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getServersUrl());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, getSchemaRegistryUrl());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        KafkaProducer<String, DeviceEventRow> producer = new KafkaProducer<>(props);

        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
                producer.close();
            }
        });
        return producer;
    }


    public Properties streamConfig() {
        final Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "energyBatteryStreamProcess");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getServersUrl());
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, getSchemaRegistryUrl());

        return config;
    }
}
