package azrou.app.ui.imports;

import azrou.app.i18n.I18n;
import azrou.app.service.CsvImportService;
import azrou.app.service.ServiceLocator;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class CsvImportController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label fileLabel;
    @FXML
    private Button selectFileButton;
    @FXML
    private Button importButton;
    @FXML
    private TextArea logArea;

    private final CsvImportService csvImportService;
    private File selectedFile;

    public CsvImportController() {
        this.csvImportService = ServiceLocator.getInstance().get(CsvImportService.class);
    }

    @FXML
    public void initialize() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.import"));
        selectFileButton.setText("Select CSV File");
        importButton.setText("Start Import");
        importButton.setDisable(true);
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(titleLabel.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            fileLabel.setText(file.getName());
            importButton.setDisable(false);
            logArea.clear();
            logArea.appendText("Selected file: " + file.getAbsolutePath() + "\n");
        }
    }

    @FXML
    private void handleImport() {
        if (selectedFile == null)
            return;

        importButton.setDisable(true);
        selectFileButton.setDisable(true);
        logArea.appendText("Starting import...\n");

        new Thread(() -> {
            try {
                CsvImportService.ImportResult result = csvImportService.importStudents(selectedFile);

                javafx.application.Platform.runLater(() -> {
                    logArea.appendText("Import completed.\n");
                    logArea.appendText("Imported: " + result.getImportedCount() + "\n");
                    logArea.appendText("Created Groups: " + result.getCreatedGroups().size() + " "
                            + result.getCreatedGroups() + "\n");

                    if (!result.getSkipped().isEmpty()) {
                        logArea.appendText("\nSkipped:\n");
                        result.getSkipped().forEach(s -> logArea.appendText("- " + s + "\n"));
                    }

                    if (!result.getErrors().isEmpty()) {
                        logArea.appendText("\nErrors:\n");
                        result.getErrors().forEach(e -> logArea.appendText("- " + e + "\n"));
                    }

                    importButton.setDisable(false);
                    selectFileButton.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    logArea.appendText("Critical Error: " + e.getMessage() + "\n");
                    importButton.setDisable(false);
                    selectFileButton.setDisable(false);
                });
            }
        }).start();
    }
}
