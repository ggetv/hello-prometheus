package com.example;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    static final Random rand = new Random(123456789);

    static final String METRIC_PREFIX = "java_old_";

    static final Counter requests = Counter.build()
        .name(METRIC_PREFIX + "requests_total")
        .help("Total requests.")
        .labelNames("status")
        .register();

    static final Gauge inprogressRequests = Gauge.build()
        .name(METRIC_PREFIX + "inprogress_requests")
        .help("Inprogress requests.")
        .register();

    static final Histogram requestLatency = Histogram.build()
        .name(METRIC_PREFIX + "request_latency_miliseconds")
        .unit("miliseconds")
        .buckets(0.01, 0.1, 0.5, 1, 2, 5, 10, 20, 50, 100)
        .help("Request latency in miliseconds.")
        .register();

    public static void main(String[] args) throws IOException {
        HTTPServer server = new HTTPServer(8081);
        System.out.println("Prometheus metrics server started on port 8081");

        // Simulate some work
        while (true) {
            processRequest();
        }
    }

    static void processRequest() {
        try {
            long startTime = Instant.now().toEpochMilli();
            requests.labels("success").inc();
            inprogressRequests.set(rand.nextInt(100));
            Thread.sleep(rand.nextInt(100));
            requestLatency.observe((Instant.now().toEpochMilli() - startTime) / 1000.0);
            //Another way to observe latency is using Histogram.Timer
        } catch (InterruptedException e) {
            requests.labels("failure").inc();
            e.printStackTrace();
        }
    }
}
