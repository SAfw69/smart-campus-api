# Postman Testing Guide - Smart Campus API

> **IMPORTANT**: The server must be running before you test!
> To start it, open a terminal in `smart-campus-api` folder and run:
> ```
> java -jar target\smart-campus-api-1.0-SNAPSHOT.jar
> ```

---

## Part 1: Discovery Endpoint (GET)

1. Open Postman
2. Click **"+"** to create a new request tab
3. Set method to **GET**
4. Type URL: `http://localhost:8080/api/v1`
5. Click **Send**
6. ✅ Expected: Status **200 OK**, JSON with version, contact, and collection links

---

## Part 2: Room Management

### 2a. List all Rooms (GET)
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/rooms`
3. Click **Send**
4. ✅ Expected: Status **200 OK**, JSON array with LIB-301 and LAB-101

### 2b. Create a new Room (POST)
1. New tab → **POST**
2. URL: `http://localhost:8080/api/v1/rooms`
3. Click **Body** tab → select **raw** → change dropdown to **JSON**
4. Paste this in the body:
```json
{
    "id": "ENG-201",
    "name": "Engineering Workshop",
    "capacity": 40
}
```
5. Click **Send**
6. ✅ Expected: Status **201 Created**, JSON with the new room

### 2c. Get a specific Room (GET)
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/rooms/LIB-301`
3. Click **Send**
4. ✅ Expected: Status **200 OK**, JSON with LIB-301 details and its sensorIds

### 2d. Delete a Room with NO sensors (DELETE) — should succeed
1. New tab → **DELETE**
2. URL: `http://localhost:8080/api/v1/rooms/ENG-201`
3. Click **Send**
4. ✅ Expected: Status **204 No Content** (room deleted successfully)

### 2e. Delete a Room WITH sensors (DELETE) — should FAIL with 409
1. New tab → **DELETE**
2. URL: `http://localhost:8080/api/v1/rooms/LIB-301`
3. Click **Send**
4. ✅ Expected: Status **409 Conflict**, JSON error message: "Room cannot be deleted as it still has active sensors assigned to it."

---

## Part 3: Sensor Operations & Filtering

### 3a. Create a Sensor with VALID roomId (POST)
1. New tab → **POST**
2. URL: `http://localhost:8080/api/v1/sensors`
3. Click **Body** tab → select **raw** → change dropdown to **JSON**
4. Paste:
```json
{
    "id": "HUM-001",
    "type": "Humidity",
    "status": "ACTIVE",
    "roomId": "LAB-101"
}
```
5. Click **Send**
6. ✅ Expected: Status **201 Created**, JSON with the new sensor

### 3b. Create a Sensor with INVALID roomId (POST) — should FAIL with 422
1. New tab → **POST**
2. URL: `http://localhost:8080/api/v1/sensors`
3. Click **Body** tab → select **raw** → change dropdown to **JSON**
4. Paste:
```json
{
    "id": "BAD-001",
    "type": "Pressure",
    "status": "ACTIVE",
    "roomId": "FAKE-999"
}
```
5. Click **Send**
6. ✅ Expected: Status **422 Unprocessable Entity**, JSON error: "Room ID specified in the sensor does not exist."

### 3c. List all Sensors (GET)
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/sensors`
3. Click **Send**
4. ✅ Expected: Status **200 OK**, JSON array with all sensors

### 3d. Filter Sensors by Type (GET with query param)
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/sensors?type=Temperature`
3. Click **Send**
4. ✅ Expected: Status **200 OK**, JSON array with ONLY the Temperature sensor (TEMP-001)

---

## Part 4: Sub-Resources (Sensor Readings)

### 4a. POST a new Reading to an ACTIVE sensor
1. New tab → **POST**
2. URL: `http://localhost:8080/api/v1/sensors/TEMP-001/readings`
3. Click **Body** tab → select **raw** → change dropdown to **JSON**
4. Paste:
```json
{
    "value": 22.5
}
```
5. Click **Send**
6. ✅ Expected: Status **201 Created**, JSON with auto-generated UUID id, timestamp, and value 22.5

### 4b. GET Readings history for a sensor
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/sensors/TEMP-001/readings`
3. Click **Send**
4. ✅ Expected: Status **200 OK**, JSON array with the reading you just posted

### 4c. Verify the parent sensor's currentValue was updated
1. New tab → **GET**
2. URL: `http://localhost:8080/api/v1/sensors?type=Temperature`
3. Click **Send**
4. ✅ Expected: The TEMP-001 sensor should now show `"currentValue": 22.5`

---

## Part 5: Error Handling

### 5a. POST Reading to MAINTENANCE sensor — should FAIL with 403
1. New tab → **POST**
2. URL: `http://localhost:8080/api/v1/sensors/CO2-001/readings`
3. Click **Body** tab → select **raw** → change dropdown to **JSON**
4. Paste:
```json
{
    "value": 450.0
}
```
5. Click **Send**
6. ✅ Expected: Status **403 Forbidden**, JSON error: "Sensor is currently unavailable to accept new readings."

### 5b. Room conflict 409 (already done in 2e above)

### 5c. Invalid roomId 422 (already done in 3b above)

### 5d. Show server console logs
- Switch to the terminal where the server is running
- You should see INFO logs like:
  ```
  INFO: Incoming Request: GET http://localhost:8080/api/v1/rooms
  INFO: Outgoing Response: GET http://localhost:8080/api/v1/rooms - Status: 200
  ```
- This proves the LoggingFilter is working

---

## Quick Reference - What to say in your video

| Part | What to say |
|------|------------|
| Part 1 | "This is the discovery endpoint. It returns API metadata and links to the main resource collections, following HATEOAS principles." |
| Part 2 | "Here I'm demonstrating full CRUD for rooms. Notice the 409 error when trying to delete a room that still has sensors." |
| Part 3 | "I'm creating sensors with room validation. The 422 error shows that our LinkedResourceNotFoundException mapper is working. The query parameter filter shows @QueryParam in action." |
| Part 4 | "This demonstrates the sub-resource locator pattern. Readings are nested under sensors. Notice how posting a reading also updates the parent sensor's currentValue." |
| Part 5 | "Here you can see our exception mappers returning proper HTTP codes instead of raw stack traces. The logging filter in the console shows every request and response." |
