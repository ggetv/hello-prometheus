# hello-prometheus

Follow the instructions in [this guide](https://signoz.io/guides/how-to-install-prometheus-and-grafana-on-docker/) from [SigNoz](signoz.io). BTW, SigNoz is a open source APM and observability platform, worth trying as well.

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
   8. Restart Promethus and Grafana: `docker compose up -d`
   9. Access Grafana at `http://localhost:3000`, default username and password is defined in `docker-compose.yml` `GF_USERS_DEFAULT_NAME` and `GF_USERS_DEFAULT_PASSWORD`.
   10. Add Prometheus data source in Grafana. Note: since we are running Prometheus and Grafana in the same Docker network, use `http://host.docker.internal:9090` or `http://prometheus:9090` as Prometheus server URL for the connection.
   11. Add cAdvisor to monitor Docker containers. Update `docker-compose.yml` to add cAdvisor.
   12. Update `prometheus.yml` to add cAdvisor. Note: since we are running cAdvisor and Prometheus in the same Docker network, use `cadvisor:8080` as cAdvisor server URL for the connection.
   13. Restart Promethus and Grafana: `docker compose up -d`, you can see cAdvisor metrics at `http://localhost:8080/metrics`, there is also a Web UI at `http://localhost:8080`
   