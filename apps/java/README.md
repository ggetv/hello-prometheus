# Prometheus Java Application Demo

This is a simple Java application that demonstrates how to use the Prometheus Java client to expose metrics.

## Features

- Exposes various types of Prometheus metrics:
  - Counter: Tracks total requests with status labels
  - Gauge: Tracks in-progress requests
  - Histogram: Measures request duration distribution
  - Summary: Tracks request size with quantiles
- Includes JVM metrics (memory, garbage collection, thread count, etc.)
- Simulates a service with random request processing

## Metrics Exposed

- `java_requests_total`: Counter of total requests with status label (success, failure, error)
- `java_inprogress_requests`: Gauge of requests currently being processed
- `java_request_duration_seconds`: Histogram of request durations
- `java_request_size_bytes`: Summary of request sizes with quantiles
- Various JVM metrics with prefix `jvm_`

## Building the Application

```bash
cd apps/java
mvn clean package
```

This will create two JAR files in the `target` directory:
- `prometheus-java-demo-new-1.0-SNAPSHOT.jar`: The application JAR
- `prometheus-java-demo-new-1.0-SNAPSHOT-jar-with-dependencies.jar`: The application JAR with all dependencies included

## Running the Application

```bash
java -jar target/prometheus-java-demo-new-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The application will start and expose metrics on port 8082.

## Accessing Metrics

Once the application is running, you can access the metrics at:

```
http://localhost:8082/metrics
```

## Configuring Prometheus

To configure Prometheus to scrape metrics from this application, add the following to your `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'java-app'
    static_configs:
      - targets: ['localhost:8082']
```

Then restart Prometheus to apply the changes.