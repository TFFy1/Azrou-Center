package azrou.app.service;

import azrou.app.config.AppConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupService {
    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    public void createBackup(File destination) throws IOException {
        if (!Files.exists(AppConfig.DB_PATH)) {
            throw new IOException("Database file not found at " + AppConfig.DB_PATH);
        }
        Files.copy(AppConfig.DB_PATH, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.info("Backup created at {}", destination.getAbsolutePath());
    }

    public void restoreBackup(File source) throws IOException {
        if (!Files.exists(source.toPath())) {
            throw new IOException("Backup file not found at " + source.getAbsolutePath());
        }
        // Note: This assumes the database lock is not held or will be released.
        // In a real app, we should close the connection pool first.
        // For this implementation, we rely on the user restarting the app after this
        // operation if it succeeds,
        // or the OS allowing the overwrite if the file is not exclusively locked
        // (SQLite WAL mode might help or hinder).
        // A safer way is to shutdown the DB manager first.

        azrou.app.db.DatabaseManager.getInstance().shutdown();

        Files.copy(source.toPath(), AppConfig.DB_PATH, StandardCopyOption.REPLACE_EXISTING);
        logger.info("Database restored from {}", source.getAbsolutePath());
    }

    public File generateDefaultBackupFile() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "azrou_backup_" + timestamp + ".db";
        return AppConfig.BACKUPS_DIR.resolve(filename).toFile();
    }
}
