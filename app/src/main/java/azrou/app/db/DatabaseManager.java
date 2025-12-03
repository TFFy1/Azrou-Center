package azrou.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import azrou.app.config.AppConfig;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = AppConfig.DB_URL;
    private static DatabaseManager instance;

    static {
        logger.info("Database URL: {}", DB_URL);
    }

    private DatabaseManager() {
        try {
            // Ensure driver is loaded
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new DataAccessException("SQLite JDBC Driver not found", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            // Enforce foreign keys and WAL mode for reliability
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.execute("PRAGMA journal_mode = WAL;");
            }
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection", e);
            throw new DataAccessException("Could not connect to database", e);
        }
    }

    public void initialize() {
        logger.info("Initializing database schema...");
        String[] schema = {
                """
                        CREATE TABLE IF NOT EXISTS groups (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL UNIQUE,
                            description TEXT,
                            capacity INTEGER NOT NULL DEFAULT 25,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS students (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            group_id INTEGER NOT NULL,
                            full_name TEXT NOT NULL,
                            cin TEXT UNIQUE NOT NULL,
                            qualifications TEXT,
                            date_of_birth DATE,
                            phone TEXT,
                            photo_path TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS subjects (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            group_id INTEGER NOT NULL,
                            name TEXT NOT NULL,
                            code TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
                            UNIQUE(group_id, name)
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS assessments (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            subject_id INTEGER NOT NULL,
                            name TEXT NOT NULL,
                            date DATE,
                            max_score REAL NOT NULL DEFAULT 100.0,
                            weight REAL NOT NULL DEFAULT 1.0,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS grades (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            student_id INTEGER NOT NULL,
                            assessment_id INTEGER NOT NULL,
                            score REAL,
                            recorded_at TIMESTAMP,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                            FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
                            UNIQUE(student_id, assessment_id)
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS admins (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL UNIQUE,
                            password_hash TEXT NOT NULL,
                            full_name TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        );
                        """,
                """
                        CREATE TABLE IF NOT EXISTS absences (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            student_id INTEGER NOT NULL,
                            subject_id INTEGER NOT NULL,
                            date DATE NOT NULL,
                            justified BOOLEAN NOT NULL DEFAULT 0,
                            reason TEXT,
                            recorded_by INTEGER,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                            FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                            FOREIGN KEY (recorded_by) REFERENCES admins(id),
                            UNIQUE(student_id, subject_id, date)
                        );
                        """
        };

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            for (String sql : schema) {
                stmt.execute(sql);
            }
            conn.commit();
            logger.info("Database schema initialized successfully.");
        } catch (SQLException e) {
            logger.error("Failed to initialize database schema", e);
            throw new DataAccessException("Database initialization failed", e);
        }
    }

    public void shutdown() {
        // Since we are not pooling connections and each request opens/closes its own,
        // there is no central connection to close.
        // We rely on callers to close their connections.
        logger.info("Database shutdown requested (no-op as connections are managed per request).");
    }
}
