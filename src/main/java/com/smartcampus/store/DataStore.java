package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    // Initialize with some dummy data for testing
    static {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab 1", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "MAINTENANCE", "LIB-301");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
    }

    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }

    public static void addReading(String sensorId, SensorReading reading) {
        sensorReadings.putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
        sensorReadings.get(sensorId).add(reading);
    }
}
