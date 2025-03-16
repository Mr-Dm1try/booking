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



