package com.manning.energy.battery.application.resources;

import com.manning.energy.battery.application.api.DeviceEvent;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DeviceEventResourceTest {

    private static final KafkaProducer producer = mock(KafkaProducer.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DeviceEventResource(producer, "my-topic"))
            .build();


    @Test
    public void objectCreatedCode() {
        // When
        Response resp = resources
                .target("/device-event/7fba40ac-0244-11eb-adc1-0242ac120002")
                .request()
                .post(Entity.json(new DeviceEvent("7fba40ac-0244-11eb-adc1-0242ac120002", 100)));

        // Then
        assertThat(resp.getStatus()).isEqualTo(201);
    }

    @Test
    public void methodNotAllowedCode() {
        // When
        Response resp = resources
                .target("/device-event/7fba40ac-0244-11eb-adc1-0242ac120002")
                .request()
                .get();

        // Then
        assertThat(resp.getStatus()).isEqualTo(405);
    }

    @Test
    public void BadRequestCode() {
        // When
        Response resp = resources
                .target("/device-event/bad-uuid")
                .request()
                .post(Entity.json(new DeviceEvent("7fba40ac-0244-11eb-adc1-0242ac120002", 100)));

        // Then
        assertThat(resp.getStatus()).isEqualTo(400);
    }
}