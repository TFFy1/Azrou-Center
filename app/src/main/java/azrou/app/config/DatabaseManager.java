package azrou.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import javax.sql.DataSource;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static SessionFactory sessionFactory;
    private static DataSource dataSource;

    public static void initialize() {
        if (dataSource == null) {
            initialize(AppConfig.DB_URL);
        } else {
            logger.info("Database already initialized.");
        }
    }

    public static void initialize(String dbUrl) {
        logger.info("Initializing database connection to: {}", dbUrl);
        try {
            // Ensure data directory exists if using default path, but for custom URL we
            // assume caller handles it or it's memory
            if (dbUrl.equals(AppConfig.DB_URL)) {
                AppConfig.initializeDirectories();
            }

            // Configure HikariCP
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(dbUrl);
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setMaximumPoolSize(1); // SQLite is single-threaded for writes mostly
            hikariConfig.setAutoCommit(false);

            // SQLite Optimization PRAGMAs
            hikariConfig.addDataSourceProperty("journal_mode", "WAL");
            hikariConfig.addDataSourceProperty("synchronous", "NORMAL");
            hikariConfig.addDataSourceProperty("foreign_keys", "ON");
            hikariConfig.addDataSourceProperty("busy_timeout", "5000");

            dataSource = new HikariDataSource(hikariConfig);

            // Configure Hibernate
            Properties settings = new Properties();
            settings.put("hibernate.connection.datasource", dataSource);
            settings.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
            settings.put("hibernate.show_sql", "true");
            settings.put("hibernate.format_sql", "true");
            settings.put("hibernate.hbm2ddl.auto", "create-drop"); // For tests we might want create-drop, but for app
                                                                   // update.
            // We need to handle this. For now let's stick to update and maybe force
            // create-drop in test config if possible?
            // Or better, pass hibernate properties.
            // For simplicity, let's use 'update' generally, and tests can manually clear or
            // we use a fresh DB file.
            settings.put("hibernate.hbm2ddl.auto", "update");

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                    .applySettings(settings);

            StandardServiceRegistry registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(azrou.app.model.entity.Admin.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Group.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Student.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Subject.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Assessment.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Grade.class);
            sources.addAnnotatedClass(azrou.app.model.entity.Absence.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();

            logger.info("Database initialized successfully.");

        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
