package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Register resources and providers
        packages("com.smartcampus.resource", "com.smartcampus.exception", "com.smartcampus.filter");
        // Explicitly register Jackson for JSON serialization
        register(JacksonFeature.class);
    }
}
