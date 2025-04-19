## Mastery Project: Don't Wreck My House - Stretch Goals

## Objective
The goal of the stretch phase in the **Don't Wreck My House** project is to deepen mastery of core Java and Spring Concepts
by implementing advanced features that go beyond base functionalities. Specifically, this version stretch to:
- Ensure reservation identifiers are globally unique across the entire application.
- Enable users to create, edit, and delete guests through the UI
- Enable users to create, edit, and delete hosts through the UI
- Provide functionality to display all reservations associated with a guest.
- Allow the user to view reservations filtered by state, city, or zip code.
- Explore an alternative repository implementation that stores data in JSON format instead of CSV, with a focus of safely
migrating existing data.
---

## Stretch Goal #1: Global Unique Reservation IDs
- Right now, reservation Ids are unique per host. That means host X can have reservation ID 1, and host Y can also have 
reservation ID 1.
- But we want to switch to **globally unique IDs** - No two reservations across the system should share the same ID.

### Plan:
- Move away from per-host ID generation based on one host's file.
- Implement centralized ID trackers across all reservation files.
- Add a generateGlobalId() method that scans all reservation files
- Add tests

---

## Stretch Goal #2: Guest CRUD
Objective is to allow users to Create, Read, Update, and Delete guests via console.

### Plan:
- Add CRUD methods to perform CRUD operations for Guest
- Add new menu option for this operation
- Add tests 
---

## Stretch Goal #3: Host CRUD
Objective is to allow users to Create, Read, Update, and Delete hosts via console.

### Plan:
- Add CRUD methods to perform CRUD operations for Host
- Add new menu option for this operation
- Add tests

## Stretch Goal #4: View Reservations by Guest, State,City, or Zip
Objective is to filter and view reservation based on:
- Guest: See all reservations made by a guest
- State: See all reservations for hosts in a specific state
- City: See all reservations for hosts in a specific city
- Zip code: See all reservations for hosts in specific zip code

### Plan:
- Add a new option in the menu
- Add a submenu
- Implement each filter
- Add tests
---

## Stretch Goal #5: JSON Repositories
Objective is to build new repository classes that save data to JSON instead of CSV. Store all reservations in one
file, not per host and Migrate existing CSV data safely into this new format.

### Plan:
- Create parallel repositories
    - GuestJsonRepository
    - HostJsonRepository
    - ReservationJsonRepository
- Implement JSON repositories: use ObjectMapper
- Migrate CSV to JSON
