# sensor-fusion

## Running the application

```shell
sbt "runMain com.luxoft.sensors.CatsJob data/"

# Or run it as a spark job
sbt "runMain com.luxoft.sensors.SparkJob data/"
```

## Generating test data

```shell
./generate_sensor_data.sh
```

## Running tests

```shell
sbt test
```
