package com.manning.energy.battery.application.resources;

import com.manning.energy.battery.application.api.DeviceStateEvent;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceStatusResourceTest {

    private static DeviceStateDAO dao = mock(DeviceStateDAO.class);

    @Before
    public void init() {
        when(dao.findById("7fba40ac-0244-11eb-adc1-0242ac120002"))
                .thenReturn(Optional.of(new DeviceStateEvent()));
    }

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DeviceStatusResource(dao))
            .build();


    @Test
    public void noContentCode() {
        // When
        Response resp = resources
                .target("/device-status/60062cac-1879-4624-bf8c-51a7ef71f29e")
                .request()
                .get();

        // Then
        assertThat(resp.getStatus()).isEqualTo(204);
    }

    @Test
    public void okCode() {
        // When
        Response resp = resources
                .target("/device-status/7fba40ac-0244-11eb-adc1-0242ac120002")
                .request()
                .get();

        // Then
        assertThat(resp.getStatus()).isEqualTo(200);
    }

    @Test
    public void methodNotAllowedCode() {
        // When
        Response resp = resources
                .target("/device-status/18173a2d-389b-4c4b-aa8d-53f74e59691b")
                .request()
                .post(Entity.json("useless"));

        // Then
        assertThat(resp.getStatus()).isEqualTo(405);
    }

    @Test
    public void BadRequestCode() {
        // When
        Response resp = resources
                .target("/device-status/bad-uuid")
                .request()
                .get();

        // Then
        assertThat(resp.getStatus()).isEqualTo(400);
    }
}