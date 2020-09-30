package com.manning.energy.battery.application.cli;

import com.manning.energy.battery.application.EnergyBatteryApplication;
import com.manning.energy.battery.application.EnergyBatteryConfiguration;
import com.manning.energy.battery.application.jdbi.DeviceStateDAO;
import com.manning.energy.battery.util.JdbiUtil;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.log4j.Logger;


public class IngestCommand extends EnvironmentCommand<EnergyBatteryConfiguration> {

    public IngestCommand(EnergyBatteryApplication app) {
        super(app, "ingest", "ingestion process");
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
    }

    @Override
    public void run(Environment environment, Namespace namespace, EnergyBatteryConfiguration config) {

        Logger.getLogger(getClass()).info("IngestCommand running ");

        final String schemaRegistryUrl = config.getKafkaFactory().getSchemaRegistryUrl();
        final CachedSchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryUrl, 10);

        final DeviceStateDAO deviceStateDAO = JdbiUtil.daoInstanceFor(DeviceStateDAO.class, environment, config);

        final Topology topology = IngestTopology
                .build(deviceStateDAO,
                        config.getEnergyTopicNameIn(),
                        config.getEnergyTopicNameOut(),
                        schemaRegistryClient,
                        schemaRegistryUrl);

        KafkaStreams streams = new KafkaStreams(topology, config.getKafkaFactory().streamConfig());

        streams.cleanUp();
        streams.start();

    }
}
