package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        // Create a resource config that registers the SmartCampusApplication configuration
        final ResourceConfig rc = new SmartCampusApplication();

        // Create and start a new instance of grizzly http server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            System.out.println(String.format("Smart Campus API Server started.\n" +
                    "WADL available at " + BASE_URI + "application.wadl\n" +
                    "Discovery endpoint available at " + BASE_URI + "api/v1\n" +
                    "Hit enter to stop it..."));
            System.in.read();
            server.shutdownNow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
