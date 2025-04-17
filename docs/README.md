# Module05-Mastery-Project-Don't-Wreck-My-House
A console-based reservation system for hosts and guests, built with Java and Spring Dependency Injection. 


## Project Architecture
````
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

## Tech Stack
- Java 17
- Spring (Core + DI Annotations)
- Maven (Build and Dependency Management)
- JUnit5 (Testing)
- CSV-based File I/O

## Features
- View Reservations for a host
- Make a new reservation (with weekend/weekday rates)
- Edit an existing reservation
- Cancel a future reservation
- Validations for:
    - Proper date format
    - Overlapping reservations
    - Future-only booking
    - Email format verification
- Fully tested functionalities
- Modular 3-layer architecture
- Annotation based Spring Dependency Injection

## Testing
- Test data located in `src/test/resources/test-data`
- Includes:
    - Repository tests with edge cases
    - Service tests for each operation (make/add, edit, cancel)
    - View I/O tests

## Sample UI
````
========================================
        VIEW RESERVATIONS FOR HOST
========================================
Host Email: example@host.com

 ID: 02  Dates: 2025-06-10 to 2025-06-14   Guest: John Doe (john@email.com)   Total: $750.00
````