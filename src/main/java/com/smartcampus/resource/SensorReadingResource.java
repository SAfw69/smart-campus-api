package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        List<SensorReading> readings = DataStore.getSensorReadings().get(sensorId);
        if (readings == null) {
            return new ArrayList<>();
        }
        return readings;
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensors().get(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is currently unavailable to accept new readings.");
        }

        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.addReading(sensorId, reading);
        
        // Side Effect: update current value
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
