# Azrou Center App

A desktop application for managing students, groups, and academic records at the Azrou Social Center. This application is originally built as a practical implementation for a Database Systems Course, highlighting the integration of a structured relational database into a Java Desktop architecture.

---

## 🌟 Beginner's Guide: How to Run & Contribute 

If you are new to Java, PostgreSQL, or coding in general, don't worry! Follow these simple steps to get the app running on your machine and start contributing:

### 1. Easiest Way to Run the App
To run the app, you don't need to be a Java expert or use complex commands. 
1. **Get the Tools**: Download and install [Apache NetBeans](https://netbeans.apache.org/) (a completely free and beginner-friendly code editor) and [PostgreSQL](https://www.postgresql.org/download/).
2. **Open the Project**: Open NetBeans, go to **File > Open Project**, and select the `AzrouCenterApp` folder you downloaded from GitHub.
3. **Database Setup**: Open the "pgAdmin" tool that came with PostgreSQL. Create a new database named `azrou_center` with the username `postgres` and password `postgres`.
4. **Hit Play!**: In NetBeans, locate the main project icon on the left-hand panel, right-click it, and select **Run**. The app will automatically build and launch on your screen!

### 2. How to Understand the Code
The application is split into simple, logical pieces:
- **`ui` folder**: This is where all the visual screens (buttons, tables, windows) live.
- **`db` folder**: This handles talking to our database (saving and grabbing data).
- **`models` folder**: These are our simple templates (like a `Student` or `Group`).

### 3. Using AI to Contribute
You don't need to write code from scratch! You can safely use AI tools (like ChatGPT, GitHub Copilot, or Cursor) to help you build features. Just explain what you want:
> *"I want to add an Email column to the Students table in this JavaFX application. Provide the PostgreSQL code and the Java code for the Student model."*

Copy the code the AI gives you, paste it into the project, and hit the "Run" button in NetBeans to see if it works. If you get an error, copy the error text and paste it back into the AI to ask for a fix!

---

## Database Architecture (PostgreSQL)

This application is built tightly around a **PostgreSQL** relational database system. The database is the core of the app, ensuring data integrity, complex relational querying, and reliable data storage.

### Key Database Features
- **Relational Schema:** Features normalized tables for `groups`, `students`, `subjects`, `assessments`, `grades`, `admins`, and `absences`.
- **Foreign Key Constraints:** Enforces strict referential integrity across all entities mapping academic tracking and student logic. `ON DELETE CASCADE` is utilized for clean hierarchy management.
- **Data Types & Scaling:** Employs modern PostgreSQL types and `SERIAL` auto-incrementing primary keys for robust capabilities.
- **Standard JDBC Connection:** Uses standard JDBC driver connection logic (`org.postgresql:postgresql`) without an abstracted ORM, demonstrating raw proficiency of SQL queries and prepared statements.
- **ACID Compliant:** Transactions are utilized manually for schema initialization to ensure atomic reliability. 

### Database Setup
To run this application locally without NetBeans, ensure you have a PostgreSQL server running on your machine.
1. Install PostgreSQL and ensure it is running on standard port `5432`.
2. Create a database named `azrou_center`. (The default username and password in config is `postgres`/`postgres`).
3. The schema will be automatically initialized when the application is first run.

---

## Prerequisites (For Command-Line Users)

- **Java 21 JDK** (Ensure `JAVA_HOME` is set)
- **Gradle**
- **PostgreSQL Database**

## Building the Application

### Run Locally
```bash
./gradlew run
