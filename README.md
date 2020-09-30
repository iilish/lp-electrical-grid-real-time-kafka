# lp-electrical-grid-real-time-kafka
liveProject for Managing a Distributed Electrical Grid in Real Time with Kafka


## Building the project

```
 mvn clean package
```

Creates a jar called `energy-kafka-1.0-SNAPSHOT.jar` in the `target/` directory, which can then be run.

## Running
Run the web server to ingest device events

```
 java -jar target/energy-kafka-1.0-SNAPSHOT.jar \
    server path_to_yaml_file
````

Run the processing server  that listens to Kafka and writes the event updates to the database.

```
 java -jar target/energy-kafka-1.0-SNAPSHOT.jar \
     ingest path_to_yaml_file
````

There is a yaml file called `battery-energy.yaml` in the `src/main/resources/` directory, which can be used as template to generate the second parameter for both servers (web and processing).

