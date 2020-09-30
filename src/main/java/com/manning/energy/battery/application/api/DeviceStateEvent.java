package com.manning.energy.battery.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manning.energy.battery.generated.DeviceEventRow;

public class DeviceStateEvent {
    private String deviceId;
    private int charging;

    public DeviceStateEvent() {
        // Jackson deserialization
    }

    public DeviceStateEvent(DeviceEventRow rowEvent) {
        this.deviceId = rowEvent.getDeviceId().toString();
        this.charging = rowEvent.getCharging();
    }

    @JsonProperty()
    public int getCharging() {
        return charging;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setCharging(int charging) {
        this.charging = charging;
    }
}
