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

### Database Setup & Mock Data Injection
To run this application, every teammate must configure their own local PostgreSQL server and inject the unified mock data.

1. **Install PostgreSQL** on your local machine and make sure a server is running on the standard port `5432`.
2. **Create the Database:** Open your PostgreSQL tool (like pgAdmin or the `psql` console) and cleanly run:
   ```sql
   CREATE DATABASE azrou_center;
   ```
3. **Change Your Credentials:** Your local PostgreSQL database password is specific to *your* hardware installation. Before running the app, open `app/src/main/java/azrou/app/config/AppConfig.java` in your code editor and forcefully update the variables to match your personal database password:
   ```java
   public static final String DB_USER = "postgres"; // Almost always postgres by default
   public static final String DB_PASSWORD = "your_actual_password_here";
   ```
4. **Load the Mock Data:** A `mock_data.sql` file is included in our project featuring exactly 5 Groups, 15 Teachers, 30 Subjects, and 150 Students cleanly bound by PostgreSQL Foreign Keys. To pull this data into your newly configured database, open Command Prompt or PowerShell, navigate securely to the project's root folder, and execute:
   ```bash
   psql -U postgres -d azrou_center -f mock_data.sql
   ```
   *(When the script says `INSERT 0 1` successfully at the bottom, hit **Run** in NetBeans! The Java app will dynamically pull all 150 generated records instantly.)*

---

## Prerequisites (For Command-Line Users)

- **Java 21 JDK** (Ensure `JAVA_HOME` is set)
- **Gradle**
- **PostgreSQL Database**

## Building the Application

### Run Locally
```bash
./gradlew run
