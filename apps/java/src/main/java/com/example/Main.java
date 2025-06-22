package com.example;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.core.metrics.Summary;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.prometheus.metrics.model.snapshots.Unit;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public class Main {
    static final Random rand = new Random();
    
    // Prefix for all metrics to distinguish from other applications
    static final String METRIC_PREFIX = "java_";
    
    // Counter example - tracks total requests with status label
    static final Counter requests = Counter.builder()
        .name(METRIC_PREFIX + "requests_total")
        .help("Total number of requests.")
        .labelNames("status")
        .register();
    
    // Gauge example - tracks current value that can go up and down
    static final Gauge inprogressRequests = Gauge.builder()
        .name(METRIC_PREFIX + "inprogress_requests")
        .help("Number of requests currently in progress.")
        .register();
    
    // Histogram example - tracks distribution of request durations
    static final Histogram requestLatency = Histogram.builder()
        .name(METRIC_PREFIX + "request_duration_seconds")
        .help("Request duration in seconds.")
        .unit(Unit.SECONDS)
        .register();
    
    // Summary example - tracks quantiles of request sizes
    static final Summary requestSize = Summary.builder()
        .name(METRIC_PREFIX + "request_size_bytes")
        .help("Request size in bytes.")
        .quantile(0.5, 0.05)   // Add 50th percentile with 5% error margin
        .quantile(0.9, 0.01)   // Add 90th percentile with 1% error margin
        .register();

    public static void main(String[] args) throws IOException {
        // Initialize default JVM metrics
        JvmMetrics.builder().register();
        
        // Start HTTP server to expose metrics
        HTTPServer server = HTTPServer.builder()
            .port(8082)
            .buildAndStart();
        System.out.println("Prometheus metrics server started on port 8082");
        System.out.println("Metrics available at http://localhost:8082/metrics");
        
        // Simulate processing requests
        while (true) {
            processRequest();
        }
    }
    
    static void processRequest() {
        final long startTime = Instant.now().getEpochSecond();
        try {
            // Increment in-progress gauge
            inprogressRequests.inc();
            
            // Simulate request processing
            if (rand.nextInt(10) < 8) {
                // 80% success rate
                requests.labelValues("success").inc();
                
                // Simulate variable processing time
                Thread.sleep(rand.nextInt(200));
            } else {
                // 20% failure rate
                requests.labelValues("failure").inc();
                
                // Failures are faster
                Thread.sleep(rand.nextInt(50));
            }

            // Record request size
            requestSize.observe(rand.nextInt(10000));
            
        } catch (InterruptedException e) {
            requests.labelValues("error").inc();
            e.printStackTrace();
        } finally {
            // Decrement in-progress gauge
            inprogressRequests.dec();

            // Record request latency
            requestLatency.observe((Instant.now().getEpochSecond() - startTime));
        }
    }
}