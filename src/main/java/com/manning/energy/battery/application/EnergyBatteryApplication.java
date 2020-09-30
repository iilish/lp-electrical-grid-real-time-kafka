package com.manning.energy.battery.application;

import com.manning.energy.battery.application.cli.IngestCommand;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import com.manning.energy.battery.application.resources.DeviceEventResource;
import com.manning.energy.battery.application.resources.DeviceStatusResource;
import com.manning.energy.battery.generated.DeviceEventRow;
import com.manning.energy.battery.util.JdbiUtil;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;

public class EnergyBatteryApplication extends Application<EnergyBatteryConfiguration> {

    public static void main(String[] args) throws Exception {
        new EnergyBatteryApplication().run(args);
    }

    @Override
    public String getName() {
        return "energy-kafka";
    }

    @Override
    public void initialize(Bootstrap<EnergyBatteryConfiguration> bootstrap) {
        bootstrap.addCommand(new IngestCommand(this));
    }

    @Override
    public void run(EnergyBatteryConfiguration config, Environment environment) {
        KafkaProducer<String, DeviceEventRow> kafkaProducer = config.getKafkaFactory().buildProducer(environment);
        DeviceStateDAO deviceStateDAO = JdbiUtil.daoInstanceFor(DeviceStateDAO.class, environment, config);

        environment.jersey().register(new DeviceEventResource(kafkaProducer, config.getEnergyTopicNameIn()));
        environment.jersey().register(new DeviceStatusResource(deviceStateDAO));
    }

}
