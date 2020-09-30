package com.manning.energy.battery.application;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.manning.energy.battery.application.config.KafkaFactory;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class EnergyBatteryConfiguration extends Configuration {

    @NotNull
    private String energyTopicNameIn;

    @NotNull
    private String energyTopicNameOut;

    @Valid
    @NotNull
    private KafkaFactory messageQueue = new KafkaFactory();

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }


    @JsonProperty("kafka")
    public KafkaFactory getKafkaFactory() {
        return messageQueue;
    }

    @JsonProperty("kafka")
    public void setKafkaFactory(KafkaFactory factory) {
        this.messageQueue = factory;
    }

    @JsonProperty("energyTopicNameIn")
    public String getEnergyTopicNameIn() {
        return energyTopicNameIn;
    }

    @JsonProperty("energyTopicNameOut")
    public String getEnergyTopicNameOut() {
        return energyTopicNameOut;
    }


}