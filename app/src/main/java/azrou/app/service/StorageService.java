package azrou.app.service;

import azrou.app.config.AppConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    public String storePhoto(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IllegalArgumentException("Source file does not exist");
        }

        String extension = getFileExtension(sourceFile.getName());
        if (!isValidImageExtension(extension)) {
            throw new IllegalArgumentException("Invalid image format. Allowed: jpg, jpeg, png");
        }

        String filename = UUID.randomUUID().toString() + "." + extension;
        Path targetPath = AppConfig.PHOTOS_ORIGINALS_DIR.resolve(filename);
        Path thumbnailPath = AppConfig.PHOTOS_THUMBNAILS_DIR.resolve(filename);

        // Atomic move (copy then delete source is safer across file systems, but here
        // we copy)
        // Actually requirement says "write to temp then atomic move".
        // Since we are copying from user selection, we copy to temp then move.

        Path tempPath = Files.createTempFile("upload_", "." + extension);
        Files.copy(sourceFile.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);

        // Move to originals
        Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE);

        // Generate thumbnail
        try {
            Thumbnails.of(targetPath.toFile())
                    .size(250, 250)
                    .toFile(thumbnailPath.toFile());
        } catch (IOException e) {
            logger.error("Failed to generate thumbnail for {}", filename, e);
            // Don't fail the whole operation, just log
        }

        return filename;
    }

    public void deletePhoto(String filename) {
        if (filename == null)
            return;

        // Soft delete or move to recycle bin as per requirement "keep old file for 7
        // days"
        // For now, let's just implement a move to a "deleted" folder inside backups or
        // similar
        // Or just leave it as is for now and implement cleanup job later.
        // Requirement: "If photo update: keep old file for 7 days... deletions go to a
        // recycle area"

        try {
            Path original = AppConfig.PHOTOS_ORIGINALS_DIR.resolve(filename);
            Path thumbnail = AppConfig.PHOTOS_THUMBNAILS_DIR.resolve(filename);

            Path recycleBin = AppConfig.BACKUPS_DIR.resolve("recycle_bin");
            if (!Files.exists(recycleBin))
                Files.createDirectories(recycleBin);

            if (Files.exists(original)) {
                Files.move(original, recycleBin.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
            if (Files.exists(thumbnail)) {
                Files.deleteIfExists(thumbnail); // Thumbnails can be regenerated, no need to recycle
            }
        } catch (IOException e) {
            logger.error("Failed to delete photo {}", filename, e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isValidImageExtension(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
    }
}
