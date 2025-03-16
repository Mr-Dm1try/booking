# Booking System

A microservices-based booking system consisting of three services:
- booking-manage: Manages bloks and booking deletion from property owner
- booking-read: Handles read operations and queries throughout all bookings
- booking-create: Handles creation and updates of bookings from users

## Prerequisites

- Java 21
- Docker and Docker Compose
- Maven

## Getting Started

1. Start the PostgreSQL database:
```bash
docker-compose up -d
```

2. Build all services:
```bash
./mvnw -s pom.xml clean install
```

3. Start each service (in separate terminals):
```bash
# Manage Service (Port 8080)
./mvnw spring-boot:run -pl booking-manage

# Read Service (Port 8081)
./mvnw spring-boot:run -pl booking-read

# Create Service (Port 8082)
./mvnw spring-boot:run -pl booking-create
```

## Testing the Services

You can test all services using the provided HTTP requests in the [requests.http](requests.http) file. 
If you're using VS Code, install the "REST Client" extension to execute these requests directly from the editor.

The requests file includes examples for:
- Creating new bookings
- Retrieving all bookings
- Getting a specific booking
- Checking for overlapping bookings
- Updating booking status
- Deleting bookings

## Database Schema

The database schema is managed by Liquibase in the booking-manage service and includes:
- bookings
- blocks
- property_lock


## Service Ports

- Manage Service: http://localhost:8080
- Read Service: http://localhost:8081
- Create Service: http://localhost:8082
- PostgreSQL: localhost:5432

## Database Configuration

- Host: localhost
- Port: 5432
- Database: booking
- Username: postgres
- Password: postgres

### Tests
Each service uses its own H2 in-memory database for testing, configured in their respective `src/test/resources/application.yaml` files.

# Java Technical Test

RESTful webservice

## Terminology
A booking is when a guest selects a start and end date and submits a reservation on a property.

A block is when the property owner or manager selects a range of days during which no guest can make
a booking (e.g. the owner wants to use the property for themselves, or the property manager needs to
schedule the repainting of a few rooms).

## Backend
Java 21. The REST API should allow users to:
- Create a booking
- Update booking dates and guest details
- Cancel a booking
- Rebook a canceled booking
- Delete a booking from the system
- Get a booking
- Create, update and delete a block

Proper validation to ensure data integrity. Logic to prevent bookings from overlapping
(in terms of dates and property) with non-canceled bookings or blocks.

## Database
Use of in-memory volatile DB is recommended



