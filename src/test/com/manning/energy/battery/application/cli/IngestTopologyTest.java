package com.manning.energy.battery.application.cli;

import com.manning.energy.battery.application.api.DeviceStateEvent;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import com.manning.energy.battery.generated.DeviceEventRow;
import com.manning.energy.battery.util.KafkaUtil;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class IngestTopologyTest {

    private TopologyTestDriver topologyDriver;

    private TestInputTopic<String, DeviceEventRow> inputTopic;
    private TestOutputTopic<String, DeviceStateEvent> outputTopic;
    private DeviceStateDAO dao;

    @Before
    public void setUp() {
        final String TOPIC_NAME_IN = "energyInputTopic";
        final String TOPIC_NAME_OUT = "energyOuputTopic";
        final String SCHEMA_REGISTRY_URL = "registryUrl";
        final Serde<String> StringSerde = Serdes.String();
        final MockSchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();
        final Deserializer<DeviceStateEvent> jsonDeserializer = jsonDeserializerFor(DeviceStateEvent.class);
        final SpecificAvroSerde<DeviceEventRow> avroSerde =
                IngestTopology.getAvroSerde(schemaRegistryClient, SCHEMA_REGISTRY_URL);


        // setup topology
        dao = mock(DeviceStateDAO.class);
        Topology topology = IngestTopology.build(dao,
                TOPIC_NAME_IN,
                TOPIC_NAME_OUT,
                schemaRegistryClient,
                SCHEMA_REGISTRY_URL);

        // setup test driver
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Ingest-Streams-Events-App");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "testUrl:9092");
        topologyDriver = new TopologyTestDriver(topology, props);

        // setup test topics
        inputTopic = topologyDriver.createInputTopic(TOPIC_NAME_IN, StringSerde.serializer(), avroSerde.serializer());
        outputTopic = topologyDriver.createOutputTopic(TOPIC_NAME_OUT, StringSerde.deserializer(), jsonDeserializer);
    }


    @After
    public void tearDown() {
        topologyDriver.close();
    }

    @Test
    public void shouldInsertIntoDB() {
        // Given
        final String deviceId = "uuid";
        final int charging = 69;
        DeviceEventRow inputEvent = newDeviceEventRow(deviceId, charging);

        // When
        inputTopic.pipeInput(deviceId, inputEvent);

        // Then
        verify(dao).createOrUpdate(deviceId, charging);
    }

    @Test
    public void shouldTransformRowEvent() {
        // Given
        final String deviceId = "uuid";
        final int charging = 99;
        DeviceEventRow inputEvent = newDeviceEventRow(deviceId, charging);
        DeviceStateEvent expectedRecord = new DeviceStateEvent(inputEvent);

        // When
        inputTopic.pipeInput(deviceId, inputEvent);

        // Then
        KeyValue<String, DeviceStateEvent> receivedRecord = outputTopic.readKeyValue();
        assertThat(receivedRecord.key, equalTo(deviceId));
        assertThat(receivedRecord.value.getDeviceId(), equalTo(expectedRecord.getDeviceId()));
        assertThat(receivedRecord.value.getCharging(), equalTo(expectedRecord.getCharging()));
        assertThat(outputTopic.isEmpty(), is(true));
    }

    private <T> Deserializer<T> jsonDeserializerFor(Class<T> clazz) {
        Serde<T> serde = KafkaUtil.jsonSerde();
        Deserializer<T> deserializer = serde.deserializer();
        Map<String, Class<?>> mapConfig = new HashMap<>();
        mapConfig.put("JsonPOJOClass", clazz);
        deserializer.configure(mapConfig, false);
        return deserializer;
    }

    private DeviceEventRow newDeviceEventRow(String deviceId, int charging) {
        return new DeviceEventRow(
                deviceId,
                charging,
                "testing",
                200L,
                100,
                23.6f);
    }
}
