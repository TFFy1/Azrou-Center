package azrou.app.ui.backup;

import azrou.app.service.BackupService;
import azrou.app.service.ServiceLocator;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class BackupController {
    @FXML
    private Label statusLabel;
    @FXML
    private Button backupButton;
    @FXML
    private Button restoreButton;

    private final BackupService backupService;

    public BackupController() {
        this.backupService = ServiceLocator.getInstance().get(BackupService.class);
    }

    @FXML
    private void handleBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Backup");
        fileChooser.setInitialFileName(backupService.generateDefaultBackupFile().getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite Database", "*.db"));

        File file = fileChooser.showSaveDialog(statusLabel.getScene().getWindow());
        if (file != null) {
            try {
                backupService.createBackup(file);
                showInfo("Backup created successfully at:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showError("Backup failed: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRestore() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Restore");
        confirm.setHeaderText("Warning: This will overwrite current data!");
        confirm.setContentText("Are you sure you want to restore? The application will close after restore.");

        if (confirm.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Backup File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite Database", "*.db"));

            File file = fileChooser.showOpenDialog(statusLabel.getScene().getWindow());
            if (file != null) {
                try {
                    backupService.restoreBackup(file);
                    showInfo("Restore successful. The application will now close.");
                    System.exit(0);
                } catch (Exception e) {
                    showError("Restore failed: " + e.getMessage());
                }
            }
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
