# Project Plan - Don't Wreck My House

## PHASE 1: Project Setup
- [x] Create Maven project & folder structure (0.5h)
- [x] Add Spring + JUnit dependencies to `pom.xml` (0.5h)
- [x] Set up `plans.md` for progress tracking (0.25h)

## PHASE 2: UML Diagrams + Models
- [ ] Class Diagrams (2 hrs)
- [ ] Create `Guest` model (0.5h)
- [ ] Create `Host` model (0.5h)
- [ ] Create `Reservation` model (0.5h)

## PHASE 3: Repositories (File-based)
- [ ] `GuestRepository` → read from guests.csv (2h)
- [ ] `HostRepository` → read from hosts.csv (2h)
- [ ] `ReservationRepository` → read/write from host-specific files (2h)
- [ ] Add unit tests for all repos using test data (2h)

## PHASE 4: Domain Services (Business Logic)
- [ ] `GuestService` (2h)
- [ ] `HostService` (2h)
- [ ] `ReservationService` (2h)
- [ ] Unit tests for domain services using test doubles (2h)

## PHASE 5: UI Layer (Console)
- [ ] `Controller` (menu, flow logic) (2h)
- [ ] `View` (input/output handling) (2h)
- [ ] Integration with services and validation (2h)

## PHASE 6: Features & Use Cases
- [ ] View reservations for host (1h)
- [ ] Make a reservation (2h)
- [ ] Edit a reservation (2.5h)
- [ ] Cancel a reservation (1h)

## Final Polish
- [ ] Add more tests for edge cases (3.5h)
- [ ] Manual testing with sample CSVs (2h)
- [ ] Refactor for clean code + logging (2h)

---

### Total Est. Time: ~40–50 hours
### Dev Style: Clean, test-driven, layered, consistent
