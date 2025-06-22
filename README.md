# Quick Start Guide with Prometheus and Grafana

## Prometheus and Grafana Setup
These steps are from [this guide](https://signoz.io/guides/how-to-install-prometheus-and-grafana-on-docker/) from [SigNoz Blog](signoz.io). SigNoz is another open source APM and observability platform worth trying.

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

Summary:
   * Prometheus UI: http://localhost:9090
   * Metrics exposed through Prometheus: http://localhost:9090/metrics
   * cAdvisor UI: http://localhost:8080
   * cAdvisor metrics: http://localhost:8080/metrics
   * Grafana UI: http://localhost:3000

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
There are four types of metrics:
   * Counter: A cumulative metric that represents a single value that can only increase or be reset to zero on restart.
   * Gauge: A metric that represents a single value that can increase or decrease over time.
   * Histogram: A metric that represents a histogram of values that can be used to calculate percentiles and distributions.
   * Summary: A metric that represents a summary of values that can be used to calculate percentiles and distributions.

Notes on `Histogram`:
   * Classic Histogram: 
      * fixed buckets defined at metric creation time
      * each bucket contains a cumulative count of observations
      * resolution is limited by bucket config
      * multiple time series, each bucket has a separate time series, count and sum. Expose metric names like `<basename>{le="<upper_bound>"}`, `<basename>_count`, `<basename>_sum`.
   * Native Histogram: starting Prometheus v2.40 in November 2022, a new native histogram was added as experimental feature. You can use the feature flag `--enable-feature=native-histograms` to enable that. For those interested, you can dive into [Native Histogram Specification](https://prometheus.io/docs/specs/native_histograms/).
      * dynamic buckets based on observed values
      * good resolution across the entire value range
      * exponential bucketing
      * single time serie. Expose metric names like `<basename>`, `<basename>_count`, `<basename>_sum`.
   * By default, Prometheus uses classic histogram. You can enable native histogram by setting the feature flag `--enable-feature=native-histograms` in Prometheus configuration. You can also enable both histogram types, which is useful for testing and migration purposes.
   * More comprehensive history on histogram can be found [here](https://prometheus.io/docs/specs/native_histograms/), this is very interesting content!
   * each bucket has a `le` label, meaning observations under that value.
   * histogram is cumulative, e.g. `le="10"` bucket contains `le="5"` bucket.
   * φ-quantile can be calculated using [`histogram_quantile()` function](https://prometheus.io/docs/prometheus/latest/querying/functions/#histogram_quantile), e.g. `histogram_quantile(0.99, rate(http_request_latency_bucket[1m]))` for a classic histogram, `histogram_quantile(0.99, rate(http_request_latency[1m]))` for a native histogram.

Notes on `Summary`:
   * Calculates quantiles on the client side over a sliding time window.
   * Expose metric names like `<basename>{quantile="<quantile, e.g. 0.95>"}` and `<basename>_count` and `<basename>_sum`.

Comparing `Histogram` and `Summary`, [see this](https://prometheus.io/docs/practices/histograms/) for more details.
   * they both provide a count (with suffix `_count`) and a sum (with suffix `_sum`), which can be used to calculate average value.
   * quatile in summary is not aggregable, i.e. averaging over a set of p99 values do not get you p99 of all the observations. But you can achieve that with histogram, e.g. `histogram_quantile(0.99, sum(rate(http_request_latency_bucket[1m]))) by (le))`.
   * for histogram, quatiles are calculated on the server side; for summary, client needs to run streaming quantile algorithm to calculate quantiles.
   * check out the table on [comparison documentation](https://prometheus.io/docs/practices/histograms/) for more details.

## Integration with Applications
Integration with applications is done through client libraries, e.g. [Go](https://github.com/prometheus/client_golang), [Python](https://github.com/prometheus/client_python), [Java](https://github.com/prometheus/client_java), [Node.js](https://github.com/prometheus/client_node), etc. You can use the client library to expose metrics in your application.

We provide several quick demo applications in the `apps` directory:
   * `java-old`: a demo Java application using the old Prometheus Java client library. The application will be running and exposing metrics on `http://localhost:8081/metrics`.
   * `java`: a demo Java application using the Prometheus Java client library. The application exposes various types of metrics (Counter, Gauge, Histogram, Summary) and JVM metrics on `http://localhost:8082/metrics`.

