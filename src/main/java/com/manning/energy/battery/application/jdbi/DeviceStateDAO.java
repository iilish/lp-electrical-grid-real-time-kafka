package com.manning.energy.battery.application.jdbi;

import com.manning.energy.battery.application.api.DeviceStateEvent;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;


public interface DeviceStateDAO {

    @SqlUpdate("insert into device_state (deviceId, charging) values (:deviceId, :charging)")
    void insert(@Bind("deviceId") String deviceId, @Bind("charging") int charging);

    @SqlUpdate("update device_state set charging = :charging where deviceId = :deviceId")
    void update(@Bind("deviceId") String deviceId, @Bind("charging") int charging);

    @SqlQuery("SELECT deviceId, charging from device_state where deviceId = :deviceId")
    @RegisterBeanMapper(DeviceStateEvent.class)
    Optional<DeviceStateEvent> findById(@Bind("deviceId") String deviceId);


    default DeviceStateEvent createOrUpdate(String deviceId, int charging) {
        if (!findById(deviceId).isPresent()) {
            insert(deviceId, charging);
        } else {
            update(deviceId, charging);
        }
        return findById(deviceId).get();
    }
}
