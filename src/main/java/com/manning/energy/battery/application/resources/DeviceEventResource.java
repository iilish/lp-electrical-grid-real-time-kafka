package com.manning.energy.battery.application.resources;

import com.codahale.metrics.annotation.Timed;
import com.manning.energy.battery.application.api.DeviceEvent;
import com.manning.energy.battery.generated.DeviceEventRow;
import com.manning.energy.battery.util.KafkaUtil;
import io.dropwizard.jersey.params.UUIDParam;
import org.apache.kafka.clients.producer.KafkaProducer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/device-event/{deviceId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceEventResource {
    private KafkaProducer<String, DeviceEventRow> producer;
    private String topic;

    public DeviceEventResource(KafkaProducer<String, DeviceEventRow> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    @POST
    @Timed
    public Response receiveDeviceEvent(@PathParam("deviceId") UUIDParam deviceId, DeviceEvent event) {
        producer.send(KafkaUtil.getAvroRecord(topic, event));

        return Response.created(UriBuilder.fromResource(DeviceEventResource.class)
                .build(deviceId, event))
                .build();
    }
}