# Project Timeline and Completed Task Schedule - Don't Wreck My House

## Phase 1: Project Setup & Planning

| Task ID | Task Description                                    | Est. Time | Status |
|---------|-----------------------------------------------------|-----------|--------|
| P1.1    | Create a Maven project with proper folder structure | 15 mins   | Done   |
| P1.2    | Add Spring dependency (core only, not Boot)         | 10 mins   | Done   |
| P1.3    | Add .gitignore, initial commit, and setup pom.xml   | 15 mins   | Done   |
| P1.4    | Create package structure: models, data, domain, ui  | 20 mins   | Done   |
| P1.5    | Define placeholder files for 13 core classes        | 30 mins   | Done   |

---

## Phase 2: Model Layer Implementation

| Task ID | Task Description                                                       | Est. Time | Status |
|---------|------------------------------------------------------------------------|-----------|--------|
| M2.1    | Implement Guest, Host, and Reservation classes with fields & accessors | 1.5 hrs   | Done   |
| M2.2    | Add BigDecimal, LocalDate, and null-safe handling                      | 30 mins   | Done   |

---

## Phase 3: Repository Layer (File I/O)
| Task ID | Task Description                                                     | Est. Time | Status |
|---------|----------------------------------------------------------------------|-----------|--------|
| R3.1    | Implement GuestRepository with findAll, findByEmail, findById        | 2 hrs     | Done   |
| R3.2    | Implement HostRepository with findAll, findByEmail                   | 1.5 hrs   | Done   |
| R3.3    | Implement ReservationRepository with findByHost, add, update, delete | 3 hrs     | Done   |
| R3.4    | Build custom CSV deserialization/serialization methods               | 2 hrs     | Done   |
| R3.5    | Add tests for all three Repositories (including invalid cases)       | 4 hrs     | Done   |

---

## Phase 4: Service Layer(Business Logic)
| Task ID | Task Description                                                      | Est. Time | Status |
|---------|-----------------------------------------------------------------------|-----------|--------|
| S4.1    | Implement GuestService and HostService                                | 1 hr      | Done   |
| S4.2    | Implement ReservationService with make/edit/cancel logic              | 3 hrs     | Done   |
| S4.3    | Add BigDecimal total calculation and weekend/day rate distinction     | 2 hrs     | Done   |
| S4.4    | Inject repositories via constructor manually, later changed to @Value | 1 hr      | Done   |

---

## Phase 5: Repository + Service Testing

| Task ID | Task Description                                                 | Est. Time | Status |
|---------|------------------------------------------------------------------|-----------|--------|
| T5.1    | Write unit tests for all Guest, Host, Reservation Repos          | 3 hrs     | Done   |
| T5.2    | Write unit tests for all Service classes                         | 3.5 hrs   | Done   |
| T5.3    | Resolve constructor path vs @Value issues and fix test paths     | 2 hrs     | Done   |
| T5.4    | Write ViewTest and simulate console I/O with Scanner/InputStream | 2.5 hrs   | Done   |
| T5.5    | Convert 64+ test cases to green after @Value migration           | 1.5 hrs   | Done   |

---

## Phase 6: UI Layer (view + MainController)
| Task ID | Task Description                                        | Est. Time | Status      |
|---------|---------------------------------------------------------|-----------|-------------|
| U6.1    | Implement View with input/output helpers & menu display | 2 hrs     | Done        |
| U6.2    | Create MainController to handle app logic/flow          | 2 hrs     | Done        |
| U6.3    | Integrate View + Controller + Service                   | 1 hr      | Done        |
| U6.4    | Finalize menu loop, navigation & input handling         | 1 hr      | Done        |
| U6.5    | Refactor UI for clean visual separators                 | 2 hr      | in progress |

---

## Phase 7: Spring DI & App Launch
| Task ID | Task Description                                           | Est. Time | Status |
|---------|------------------------------------------------------------|-----------|--------|
| DI7.1   | Add Spring annotations (@Component, @Service, @Repository) | 45 mins   | Done   |
| DI7.2   | Create AppConfig.java for @Configuration & property scan   | 30 mins   | Done   |
| DI7.3   | Use @Value in constructors for file path injection         | 1 hr      | Done   |
| DI7.4   | Modify test files to adapt to String path constructor      | 1.5 hrs   | Done   |
| DI7.5   | Run with AnnotationConfigApplicationContext in Main        | 20 mins   | Done   |

---

## Phase 8: Diagrams, Docs, Flowchart
| Task ID | Task Description                                 | Est. Time | Status |
|---------|--------------------------------------------------|-----------|--------|
| D8.1    | Create Class Diagram                             | 1 hr      | Done   |
| D8.2    | Application Sequence Diagram                     | 1.5 hr    | Done   |

---

## Total Time Estimate for Work Completed
| Category               | Total Hours |
|------------------------|-------------|
| Project Setup + Models | ~2 hrs      |
| Repository Layer       | ~8.5 hrs    |
| Service Layer          | ~9.5 hrs    |
| UI + Controller        | ~6 hrs      |
| Testing                | ~12 hrs     |
| DI + Spring Wiring     | ~5.5 hrs    |
| Diagrams + Docs        | ~3 hrs      |
| Total                  | ~49 hrs     |