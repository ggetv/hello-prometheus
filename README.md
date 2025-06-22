# Quick Start Guide with Prometheus and Grafana
This guide is a quick start guide to get you started with Prometheus and Grafana. It is based on [this guide](https://signoz.io/guides/how-to-install-prometheus-and-grafana-on-docker/) from [SigNoz Blog](signoz.io). SigNoz is another open source APM and observability platform worth trying.

Once you have set up Prometheus and Grafana, you can start the demo applications (see `apps` directory) in different programming languages to send metrics.

## Prometheus and Grafana Setup
   1. Create a Docker network: `docker network create prometheus`
   2. Create Prometheus config file: `prometheus.yml`
   3. Create a Docker Compose file: `docker-compose.yml`
   4. Start the services: `docker compose up -d`
   5. Access Prometheus at `http://localhost:9090`, you can see raw metrics at `http://localhost:9090/metrics`
   6. Try some queries in Prometheus UI, for more details on PromQL, see [PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/):
   ```
   rate(prometheus_http_requests_total[1m])
   avg_over_time(rate(prometheus_http_requests_total[1m])[15m:1m])
   ```
   7. Update Docker Compose file to add Grafana.
   8. Restart Promethus and Grafana: `docker compose restart`
   9. Access Grafana at `http://localhost:3000`, default username and password is defined in `docker-compose.yml` `GF_USERS_DEFAULT_NAME` and `GF_USERS_DEFAULT_PASSWORD`.
   10. Add Prometheus data source in Grafana. Note: since we are running Prometheus and Grafana in the same Docker network, use `http://host.docker.internal:9090` or `http://prometheus:9090` as Prometheus server URL for the connection.
   11. Add cAdvisor to monitor Docker containers. Update `docker-compose.yml` to add cAdvisor.
   12. Update `prometheus.yml` to add cAdvisor. Note: since we are running cAdvisor and Prometheus in the same Docker network, use `cadvisor:8080` as cAdvisor server URL for the connection.
   13. Restart Promethus and Grafana: `docker compose restart`, you can see cAdvisor metrics at `http://localhost:8080/metrics`, there is also a Web UI at `http://localhost:8080`

## Demo Applications
Integration with applications can be done in different ways:
   * Use Prometheus client library (see below)
   * Use [Micrometer](https://micrometer.io): a facade that support many different observability platforms, e.g. Prometheus, Graphite, InfluxDB, etc. Think of it as `slf4j` for logging
   * Use [OpenTelemetry](https://opentelemetry.io): a vendor-neutral open standard for observability data collection, it is a more modern approach and can be used to collect metrics, traces, and logs for different observability platforms.

One common option is to use Prometheus client libraries, e.g. [Go](https://github.com/prometheus/client_golang), [Python](https://github.com/prometheus/client_python), [Java](https://github.com/prometheus/client_java), [Node.js](https://github.com/prometheus/client_node), etc. You can use the client library to expose metrics in your application.

You can find demo applications in the `apps` directory. Follow the instructions in the `README.md` file in each directory to run the demo applications.

Note: Prometheus by default scapes metrics from `/metrics` endpoint. You can configure the endpoint using `metrics_path` in `prometheus.yml`.

### Java Demo Applications
There was an old Java client library `Simpleclient` and then it got rewritten to `client_java`. We provide two demo applications to show how to use both libraries. You can find more examples in [client_java examples](https://github.com/prometheus/client_java/tree/1.0.x/examples).
   * `java-old`: a demo Java application using the old Prometheus Java client library. The application will be running and exposing metrics on `http://localhost:8081/metrics`.
   * `java`: a demo Java application using the Prometheus Java client library. The application exposes various types of metrics (Counter, Gauge, Histogram, Summary) and JVM metrics on `http://localhost:8082/metrics`.

Besides using Prometheus Java client library, you can also use [Micrometer](https://micrometer.io) to expose metrics. Micrometer is a metrics facade for Java applications, it provides a simple API to expose metrics to different monitoring systems, e.g. Prometheus, Graphite, InfluxDB, etc.


## Summary
   * Prometheus UI: http://localhost:9090
   * Prometheus metrics: http://localhost:9090/metrics
   * cAdvisor UI: http://localhost:8080
   * cAdvisor metrics: http://localhost:8080/metrics
   * Grafana UI: http://localhost:3000
   * Demo app `java-old`: http://localhost:8081
   * Demo app `java-old` metrics: http://localhost:8081/metrics
   * Demo app `java`: http://localhost:8082
   * Demo app `java` metrics: http://localhost:8082/metrics


## Prometheus
Prometheus is a time series database, it stores metrics in the form of time series. It supports dimensions called labels, which are key-value pairs that can be used to filter and aggregate metrics. 
   * Data collection: Prometeus scrapes metrics from targets at regular intervals using a pull model. It also supports a push model through the Pushgateway (for short lived jobs).
   * Visualization: Prometeus provides a built-in query language called PromQL to query and aggregate metrics, and a built-in UI to visualize metrics. There are UI such as Grafana that can be used to visualize metrics in a more user-friendly way. You can also use API clients such as curl to query metrics.
   * Alerting: Prometeus can be used to alert on metrics using a built-in alerting system. You can also use [Alertmanager](https://github.com/prometheus/alertmanager) to send alerts to different channels such as email, Slack, etc.
   * Terms:
      * `instance`: an endpoint to scrape metrics is known as an `instance`, e.g. `<host>:<port>`
      * `job`: a group of `instance` is known as a `job`, e.g. an application process forked to run in parallel, the group is a `job`. The job name is configured in `prometheus.yml`.

Alternatives to Prometheus: [this doc](https://prometheus.io/docs/introduction/comparison/) compares Prometheus with some of them.
   * Graphite
   * InfluxDB
   * OpenTSDB
   * DataDog
   * New Relic
   * [Sensu from sumo logic](https://sensu.io)
   * [SigNoz](https://signoz.io)


You can learn more about Prometheus [here](https://prometheus.io/docs/introduction/overview/)

![Prometheus Architecture](https://prometheus.io/assets/docs/architecture.svg?w=600)

## Grafana
Grafana is the UI for observability platforms, for the impatient, [this documentation](https://grafana.com/docs/grafana/latest/fundamentals/dashboards-overview/) is a good short overview to get you started with Dashboards. It covers basic concepts like: data sources, panels, dashboards, queries, transforms, plugins, etc. You can also deep dive into topics like different types of visualizations, variables, alerts, etc.

![Grafana Dashboard Component Architecture](https://grafana.com/media/docs/grafana/dashboards-overview/dashboard-component-architecture.png?w=600)

## Sample Graphana Dashboards
Grafana dashboards can be stored as JSON files. There are two sample dashboards in the `dashboards` directory: `prometheus_requests.json` and `container.json`. When you create a new dashboard, you can choose the option to import a JSON file and create the dashboard.

   * `prometheus.json`: Prometheus dashboard for internal metrics, such as HTTP requests, memory usage, CPU usage, etc.
   * `system.json`: Docker container metrics exposed through cAdvisor

## Promethus Metric Names
Metric names and labels need to follow the naming conventions defined in [Prometheus](https://prometheus.io/docs/concepts/data_model/). In summary, you should stick to alphanumeric characters, use underscores to separate words, and avoid special characters (UTF-8 is supported).

## Promethus Metric Types
You can find more detailed explanation and examples [here](https://prometheus.github.io/client_java/getting-started/metric-types/).

There are different types of metrics:
   * `Counter`: A cumulative metric that represents a single value that can only increase or be reset to zero on restart.
   * `Gauge`: A metric that represents a single value that can increase or decrease over time.
   * `Info`: A metric that represents a single value that can be used to store metadata about the application. The value never changes, e.g. version, build, etc.
   * `StateSet`: A less commonly used metric type to represent a set of states. One use case is to log feature flags enabled for the application.
   * `Histogram`: A metric that represents a histogram of values that can be used to calculate percentiles and distributions.
   * `Summary`: A metric that represents a summary of values that can be used to calculate percentiles and distributions.

`Counter` and `Gauge` are easy to understand. Let's add more details for `Histogram` and `Summary`.

### Histogram
`Histogram` is a cumulative metric that represents a histogram of values that can be used to calculate percentiles and distributions. It is a cumulative metric that represents a histogram of values that can be used to calculate percentiles and distributions.

There are two types of histograms:
   * Classic Histogram: fixed buckets defined at metric creation time. This is the aligned with [OpenMetrics](https://github.com/prometheus/OpenMetrics). Resolution is limited by bucket config. Each bucket has a separate time series. Use can define buckets using `classicBuckets(...)` method.
   * Native Histogram: dynamic buckets based on observed values. This is a new feature added in Prometheus v2.40 in November 2022. It provides better resolution across the entire value range. It is based on exponential bucketing There is only one time series for the histogram.

Histogram data maintains both formats (this can be controlled by method `nativeOnly()` and `classicOnly()` when creating histogram), it depends on the Prometheus server scrape configuration to determine which format to use.
   * By default, Prometheus uses classic histogram in OpenMetrics format.
   * To use native histogram, you need to enable the feature flag `--enable-feature=native-histograms` in Prometheus configuration. Metrics will be send in Prometheus protobuf format and Prometheus will use native histogram.
   * You can also enable both histogram types, which is useful for testing and migration purposes. You can use the feature flag `--enable-feature=native-histograms` in Prometheus configuration and set the `scrape_classic_histograms: true` in the scrape config. Metrics will be sent in Prometheus protobuf format and Prometheus will ingest both class and native histograms.

Each observation data point has a complex data structure that contains count, sum and buckets.
   * Classic Histogram:
      * count: `<basename>_count`
      * sum: `<basename>_sum`
      * buckets: `<basename>{le="<upper_bound>"}`
   * Native Histogram:
      * count: `<basename>_count`
      * sum: `<basename>_sum`
      * buckets: `<basename>`

### Summary
`Summary` is similar to histogram for observing distributions. Each observation data point contains count, sum and quantiles (this is one difference from histogram, which maintains buckets).

When you define a quantile, you provide a target quantile and a precision, e.g. `.quantile(0.99, 0.01)`, higher precision requires higher memory usage. Quantile values are updated on the client side over a sliding window of observations (default to 5 minutes).

#### Comparing Histogram and Summary
   * Similarities:
      * They both implements `DistributionDataPoint` interface.
      * They both provide a count and a sum.
   * Differences:
      * Histogram maintains buckets, while summary maintains quantiles.
      * For histogram, quantiles are calculated on the server side, e.g. use `histogram_quantile()` function to calculate p99, p95, p50, etc. For summary, quantiles are calculated on the client side over a sliding time window.
      * Because quantiles in summary is calculated on the client side, they are not aggregatable. But for histogram, quantiles are aggregatable,you can calculate p99 of all the observations, e.g. `histogram_quantile(0.99, sum(rate(http_request_latency_bucket[1m]))) by (le))`.

### Performance Tips
   * Use `DataPoint` to avoid repeated label lookups. For example:
   ```java
   Counter counter = Counter.build().name("counter").help("counter").labelNames("status").register();
   //avoid
   counter.labels("success").inc();
   //recommend
   DataPoint dp = counter.labelValues("success");
   dp.inc();
   ```
   * Enable only one histogram representation: by default, both classic and native histograms are enable. Choose to enable only one to improve performance.
      * in the code, you can use `classicOnly()` or `nativeOnly()` to enable only one histogram representation when you create histogram.
      * pass in property `io.prometheus.metrics.histogram.classicOnly=true` or `io.prometheus.metrics.histogram.nativeOnly=true` to enable only one histogram representation when you start the application, e.g. `java -Dio.prometheus.metrics.histogram.classicOnly=true -jar target/prometheus-java-demo-new-1.0-SNAPSHOT-jar-with-dependencies.jar` or in `prometheus.yml`.


