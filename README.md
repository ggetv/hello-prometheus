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

## Sample Graphana Dashboards
Prometheus is a time series database, it stores metrics in the form of time series. It supports dimensions called labels, which are key-value pairs that can be used to filter and aggregate metrics. You can learn more about Prometheus [here](https://prometheus.io/docs/introduction/overview/)

Grafana is the UI for observability platforms, for the impatient, [this documentation](https://grafana.com/docs/grafana/latest/fundamentals/dashboards-overview/) is a good short overview to get you started with Dashboards. It covers basic concepts like: data sources, panels, dashboards, queries, transforms, plugins, etc. You can also deep dive into topics like different types of visualizations, variables, alerts, etc.

![Grafana Dashboard Component Architecture](https://grafana.com/media/docs/grafana/dashboards-overview/dashboard-component-architecture.png?w=600)

Grafana dashboards can be stored as JSON files. There are two sample dashboards in the `dashboards` directory: `prometheus_requests.json` and `container.json`. When you create a new dashboard, you can choose the option to import a JSON file and create the dashboard.

   * `prometheus_requests.json`: Prometheus HTTP requests dashboard
   * `container.json`: Docker container metrics exposed through cAdvisor

