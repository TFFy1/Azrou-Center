package azrou.app.service;

import azrou.app.config.AppConfig;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupService {
    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    public void createBackup(File destination) throws IOException {
        String dbName = "azrou_center";
        
        // Execute pg_dump to create a PostgreSQL backup
        ProcessBuilder pb = new ProcessBuilder(
            "pg_dump", 
            "-U", AppConfig.DB_USER, 
            "-F", "c", // Custom format, appropriate for pg_restore
            "-f", destination.getAbsolutePath(), 
            dbName
        );
        
        // Set the password in the environment variables securely
        pb.environment().put("PGPASSWORD", AppConfig.DB_PASSWORD);
        
        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("pg_dump exited with code " + exitCode + ". Make sure PostgreSQL bin directory is in your system PATH.");
            }
            logger.info("Backup created at {}", destination.getAbsolutePath());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Backup interrupted", e);
        }
    }

    public void restoreBackup(File source) throws IOException {
        String dbName = "azrou_center";
        
        // Disconnect any active connections before restoring if possible
        azrou.app.db.DatabaseManager.getInstance().shutdown();

        // Execute pg_restore to recover the custom format backup
        ProcessBuilder pb = new ProcessBuilder(
            "pg_restore", 
            "-U", AppConfig.DB_USER, 
            "-d", dbName, 
            "-1", // single transaction
            "--clean", // drop existing database objects before recreating
            source.getAbsolutePath()
        );
        
        pb.environment().put("PGPASSWORD", AppConfig.DB_PASSWORD);
        
        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("pg_restore exited with code " + exitCode + ". Make sure PostgreSQL bin directory is in your system PATH.");
            }
            logger.info("Database restored from {}", source.getAbsolutePath());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Restore interrupted", e);
        }
    }

    public File generateDefaultBackupFile() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "azrou_backup_" + timestamp + ".backup";
        return AppConfig.BACKUPS_DIR.resolve(filename).toFile();
    }
}
