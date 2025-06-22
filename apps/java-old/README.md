# Demo app: java-old
This demo app uses the old Java client library for Prometheus: `Simpleclient` and `Simpleclient_httpserver`. It is a simple HTTP server that exposes metrics on the `/metrics` endpoint. The application is a simple Java application that uses the old client library to expose metrics.

The new Java client library 1.0.0 is a complete rewrite and is not backward compatible with the old client library. The new client library is available at [client_java](https://prometheus.github.io/client_java/).

If you are running the old `0.16.0` Java client, you can use `Simpleclient Bridge` to keep using the old code when you migrate to the new client library. You can read [this documentation](https://prometheus.github.io/client_java/migration/simpleclient/) for more details on how to use the bridge.

## Usage
```shell
mvn clean package
java -jar target/prometheus-java-demo-1.0-SNAPSHOT-jar-with-dependencies.jar
```
The application will be running and exposing metrics on `http://localhost:8081/metrics`.