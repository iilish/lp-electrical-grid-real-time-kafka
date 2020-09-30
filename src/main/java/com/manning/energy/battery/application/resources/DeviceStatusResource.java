package com.manning.energy.battery.application.resources;

import com.codahale.metrics.annotation.Timed;
import com.manning.energy.battery.application.api.DeviceStateEvent;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import io.dropwizard.jersey.params.UUIDParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/device-status/{deviceId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceStatusResource {
    private DeviceStateDAO dao;

    public DeviceStatusResource(DeviceStateDAO dao) {
        this.dao = dao;
    }

    @GET
    @Timed
    public Response receiveDeviceEvent(@PathParam("deviceId") UUIDParam deviceId) {

        Optional<DeviceStateEvent> status = dao.findById(deviceId.toString());
        if (status.isPresent()) {
            return Response.ok(status.get()).build();

        } else {
            return Response.noContent().build();
        }
    }
}