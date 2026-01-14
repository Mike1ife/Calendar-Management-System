# Calendar Management System

## Description

The **Calendar Management System** is a Java-based application that allows users to create, manage, and interact with digital calendars and events. The system supports **multiple calendars**, **timezone-aware scheduling**, **single and recurring events**, and **event copying across calendars**.

The application can be used in three different modes:

- **Graphical User Interface (GUI)** built with Java Swing  
- **Interactive text-based mode**
- **Headless (script-driven) mode**

All modes operate on the same underlying model and controller logic, following a strict **Model–View–Controller (MVC)** architecture.

---

## Table of Contents

- [Features](#features)
- [Execution Modes](#execution-modes)
- [Usage](#usage)
- [Design Overview](#design-overview)
- [Testing](#testing)
- [License](#license)

---

## Features

- Support for **multiple calendars**, each with a unique name and timezone
- Timezone-aware event scheduling and copying
- Creation of:
  - Single events
  - All-day events
  - Recurring event series
- Editing events:
  - Single instance
  - Partial series
  - Entire series
- Querying events by date or date range
- Checking availability at a specific date and time
- Exporting calendars to:
  - **CSV**
  - **iCal**
- Graceful handling of invalid input in both GUI and text-based modes

---

## Execution Modes

The application supports the following execution modes:

### 1. GUI Mode
Launches a graphical calendar interface.
```bash
java -jar build/libs/calendar-1.0.jar
```

### 2. Interactive Mode
Allows users to enter commands one at a time.
```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
```

### 3. Headless Mode
Executes commands from a script file and exits.
```bash
java -jar build/libs/calendar-1.0.jar --mode headless public/commands.txt
```

---

## Usage

Detailed instructions for building and running the application, including:

- Step-by-step GUI usage
- Examples for interactive and headless modes
- Supported command syntax
- Input formats and constraints

are provided in **`USEME.md`**.

Please refer to that file before running the application.

---

## Design Overview

The system follows the **MVC design pattern**:

- **Model**  
  Represents calendars, events, recurring series, and timezone-aware date/time logic.

- **View**  
  Includes the Swing-based GUI and text-based interfaces.

- **Controller**  
  Interprets user actions, validates input, and coordinates interactions between the view and model.

This separation ensures scalability, testability, and support for multiple user interfaces without duplicating business logic.

---

## Testing

- Unit tests are provided for the model and controller layers.
- JUnit is used for all testing.
- GUI components are not directly tested; instead, controller behavior in response to user actions is validated.

---

## License

This project is licensed under the MIT License.  
See the [LICENSE](https://github.com/Mike1ife/Evolutionary-Computation-Project/blob/main/LICENSE) file for more details.
