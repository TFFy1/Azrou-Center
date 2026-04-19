package azrou.app.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String APP_NAME = "AzrouCenterApp";
    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    public static final Path APP_DIR = USER_HOME.resolve(APP_NAME);
    public static final Path DATA_DIR = APP_DIR.resolve("data");
    public static final Path PHOTOS_DIR = APP_DIR.resolve("photos");
    public static final Path PHOTOS_ORIGINALS_DIR = PHOTOS_DIR.resolve("originals");
    public static final Path PHOTOS_THUMBNAILS_DIR = PHOTOS_DIR.resolve("thumbnails");
    public static final Path BACKUPS_DIR = APP_DIR.resolve("backups");
    public static final Path EXPORTS_DIR = APP_DIR.resolve("exports");
    public static final Path LOGS_DIR = APP_DIR.resolve("logs");

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/azrou_center";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "admin";

    public static void initializeDirectories() {
        try {
            createDirIfNotExists(APP_DIR);
            createDirIfNotExists(DATA_DIR);
            createDirIfNotExists(PHOTOS_DIR);
            createDirIfNotExists(PHOTOS_ORIGINALS_DIR);
            createDirIfNotExists(PHOTOS_THUMBNAILS_DIR);
            createDirIfNotExists(BACKUPS_DIR);
            createDirIfNotExists(EXPORTS_DIR);
            createDirIfNotExists(LOGS_DIR);
        } catch (IOException e) {
            logger.error("Failed to initialize application directories", e);
            throw new RuntimeException("Could not initialize application directories", e);
        }
    }

    private static void createDirIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("Created directory: {}", path);
        }
    }
}
