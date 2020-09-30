package com.manning.energy.battery.util;

import com.manning.energy.battery.application.EnergyBatteryConfiguration;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

public class JdbiUtil {

    private JdbiUtil() {

    }

    public static <T> T daoInstanceFor(Class<T> t, Environment environment, EnergyBatteryConfiguration config) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config.getDataSourceFactory(), "postgresql");
        return jdbi.onDemand(t);
    }
}
