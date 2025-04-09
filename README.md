# Module05-Mastery-Project-Dont-Wreck-My-House

 
## Project Architecture
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
├── models       -> POJOs (Guest, Host, Reservation)
│
├── ui           -> Console UI & Menu handling (@Component)
│   ├── Controller
│   └── View
│
└── App.java     -> Main class (launch Spring context)
