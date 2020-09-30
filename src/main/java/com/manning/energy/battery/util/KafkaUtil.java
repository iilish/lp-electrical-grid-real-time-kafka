package com.manning.energy.battery.util;

import com.manning.energy.battery.application.api.DeviceEvent;
import com.manning.energy.battery.generated.DeviceEventRow;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class KafkaUtil {

    private KafkaUtil() {

    }

    public static ProducerRecord<String, DeviceEventRow> getAvroRecord(String topic, DeviceEvent event) {

        DeviceEventRow receivedEvent = new DeviceEventRow(
                event.getDeviceId(),
                event.getCharging(),
                event.getChargingSource(),
                event.getCurrentCapacity(),
                event.getInverterState(),
                event.getSoCRegulator());

        return new ProducerRecord<>(topic, receivedEvent.getDeviceId().toString(), receivedEvent);
    }

    public static <T> Serde<T> jsonSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>());
    }
}
