# Azrou Center App

A desktop application for managing students, groups, and academic records at the Azrou Social Center.

## Prerequisites

- **Java 21 JDK** (Ensure `JAVA_HOME` is set)
- **Gradle** (Wrapper is included, but if missing, install Gradle 8.5+)

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
- `app/src/main/java`: Source code
- `app/src/main/resources`: FXML, CSS, and config files
- `app/src/test/java`: Unit and UI tests

## Features
- **Student Management**: Add, update, delete students with photo support.
- **Group Management**: Organize students into groups.
- **Academic Tracking**: Manage subjects, assessments, and grades.
- **Attendance**: Track student absences.
- **Reporting**: Generate PDF reports for group lists.
- **Data Management**: CSV Import, Database Backup/Restore.
