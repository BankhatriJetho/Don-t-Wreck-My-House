## Project Plan: Don't Wreck My House
---------------------------------------
## Project Objective
Develop a console-based Java reservation system for booking accommodations. The app allows an administrator to:
- View reservations for a host
- Add, edit, and cancel reservations
-  Validate booking rules (dates, overlap, guest/host match)
----------------------------------------
## Architecture Overview
````
Three-Layer Architecture
com.dwmyhouse
├── data         -> All file read/write logic (@Repository)
│   ├── GuestRepository
│   ├── HostRepository
│   └── ReservationRepository
│
├── domain       -> Business logic (@Service)
│   ├── GuestService
│   ├── HostService
│   └── ReservationService
│
├── models       -> Model classes
│   ├── Guest
│   ├── Host
│   ├── Reservation
│
├── ui           -> Console UI & Menu handling (@Component)
│   ├── Controller
│   └── View
│
├── Main        -> Main class (launch Spring context)
└── AppConfig     
````
----------------------------------------
## Technologies Used
- Java 17
- Spring (Core + DI Annotations)
- Maven (Build and Dependency Management)
- JUnit5 (Testing)
- CSV-based File I/O

----------------------------------------
## Implementation Strategy
### Menu Features
- 0 : Exit
- 1 : View Reservations
- 2 : Make/Add Reservation
- 3 : Edit Reservation
- 4 : Cancel Reservation

### Core Operations
Each operation is triggered through `MainController` and calls into services and repositories as needed.
- **Make Reservation:** Validates date, checks for overlap, calculates cost (weekday/weekend rates)
- **Edit Reservation:** Allows date change only if no conflict exists
- **Cancel Reservation:** Only future reservations allowed to be cancelled

----------------------------------------

## Validation Rules
### Date Validations
- Start Date must be in the future
- End Date must be after the start date
- flexible parsing supports (e.g.: 2025-4-15)

### Email Validations
- Must follow standard email format (e.g.: abc@example.com)
- Invalid emails like `123` or `abc` are re-prompted

### Business Rules
- No Overlapping reservations for the same host
- Guests and Hosts must exist in their respective files

## Data Design Provided
### Guest CSV Format
````
guest_id,first_name,last_name,email,phone,state

````

### Host CSV Format
````
id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate

````
### Reservation CSV Format
One file per host: `reservations/hostId.csv`
````
id,start_date,end_date,guest_id,total

````

## Testing Strategy
- Unit tests for each repository and service
- Tests include:
  - invalid emails, date formats
  - overlapping dates
  - editing and cancelling edge cases
  - corrupt CSV handling

## Deliverables 
- Java Maven Project with structured packages
- Fully functional app with UI interactions
- Documentation: 
  - README
  - Detailed Project Plan
  - Estimated completion time for each task
  - Class Diagram
  - Sequence Diagram
- Java Idioms
- Test Suite

## Timeline and Task Schedule
Refer to [Schedule_and_Timeline.md](Schedule_and_Timeline.md)


