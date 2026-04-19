# Azrou Center App

A desktop application for managing students, groups, and academic records at the Azrou Social Center. This application is originally built as a practical implementation for a Database Systems Course, highlighting the integration of a structured relational database into a Java Desktop architecture.

## Database Architecture (PostgreSQL)

This application is built tightly around a **PostgreSQL** relational database system. The database is the core of the app, ensuring data integrity, complex relational querying, and reliable data storage.

### Key Database Features
- **Relational Schema:** Features normalized tables for `groups`, `students`, `subjects`, `assessments`, `grades`, `admins`, and `absences`.
- **Foreign Key Constraints:** Enforces strict referential integrity across all entities mapping academic tracking and student logic. `ON DELETE CASCADE` is utilized for clean hierarchy management.
- **Data Types & Scaling:** Employs modern PostgreSQL types and `SERIAL` auto-incrementing primary keys for robust capabilities.
- **Standard JDBC Connection:** Uses standard JDBC driver connection logic (`org.postgresql:postgresql`) without an abstracted ORM, demonstrating raw proficiency of SQL queries and prepared statements.
- **ACID Compliant:** Transactions are utilized manually for schema initialization to ensure atomic reliability. 

### Database Setup
To run this application locally, ensure you have a PostgreSQL server running on your machine.
1. Install PostgreSQL and ensure it is running on standard port `5432`.
2. Create a database named `azrou_center`. (The default username and password in config is `postgres`/`postgres`).
3. The schema will be automatically initialized when the application is first run.

---

## Prerequisites

- **Java 21 JDK** (Ensure `JAVA_HOME` is set)
- **Gradle**
- **PostgreSQL Database**

## Building the Application

### Run Locally
```bash
./gradlew run
```

### Run Tests
```bash
./gradlew test
```

### Create Windows Installer
To create a Windows MSI installer:

```bash
./gradlew jpackage
```

The installer will be generated in: `app/build/jpackage/`

## Project Structure
- `app/src/main/java`: Source code including structured DAOs and DB handlers.
- `app/src/main/resources`: FXML, CSS, and UI components.
- `app/src/test/java`: Unit and UI tests.

## Features
- **Student Management**: Add, update, delete students with photo support.
- **Group Management**: Organize students intuitively using SQL constraints.
- **Academic Tracking**: Manage subjects, assessments, and complex grades accurately.
- **Attendance**: Track student absences.
- **Reporting**: Generate PDF reports for group lists efficiently pulled from DB views.
- **Data Management**: Batch insert configurations from CSV to PostgreSQL.
