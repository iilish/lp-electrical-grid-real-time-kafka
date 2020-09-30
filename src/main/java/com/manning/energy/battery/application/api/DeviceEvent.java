package com.manning.energy.battery.application.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"processor1_temp", "processor2_temp", "processor3_temp", "processor4_temp", "moduleL_temp", "moduleR_temp"})
public class DeviceEvent {

    private String deviceId;
    private int charging;
    private String chargingSource;
    private Long currentCapacity;
    private int inverterState;
    private float SoCRegulator;

    public DeviceEvent() {
        // Jackson deserialization
    }

    public DeviceEvent(String deviceId, int charging) {
        this.deviceId = deviceId;
        this.charging = charging;
    }

    @JsonProperty("charging_source")
    public String getChargingSource() {
        return chargingSource;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty()
    public int getCharging() {
        return charging;
    }

    @JsonProperty("current_capacity")
    public Long getCurrentCapacity() {
        return currentCapacity;
    }

    @JsonProperty("inverter_state")
    public int getInverterState() {
        return inverterState;
    }

    @JsonProperty("SoC_regulator")
    public float getSoCRegulator() {
        return SoCRegulator;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setCharging(int charging) {
        this.charging = charging;
    }

    public void setChargingSource(String chargingSource) {
        this.chargingSource = chargingSource;
    }

    public void setCurrentCapacity(Long currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setInverterState(int inverterState) {
        this.inverterState = inverterState;
    }

    public void setSoCRegulator(float soCRegulator) {
        SoCRegulator = soCRegulator;
    }
}
