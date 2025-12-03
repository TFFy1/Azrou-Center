package azrou.app.ui.reports;

import azrou.app.model.dto.GroupDto;
import azrou.app.service.GroupService;
import azrou.app.service.ReportService;
import azrou.app.service.ServiceLocator;
import java.awt.Desktop;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class ReportController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<GroupDto> groupCombo;
    @FXML
    private Button generateGroupReportButton;
    @FXML
    private Label statusLabel;

    private final ReportService reportService;
    private final GroupService groupService;
    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();

    public ReportController() {
        this.reportService = ServiceLocator.getInstance().get(ReportService.class);
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
    }

    @FXML
    public void initialize() {
        setupCombos();
        loadData();
    }

    private void setupCombos() {
        StringConverter<GroupDto> converter = new StringConverter<>() {
            @Override
            public String toString(GroupDto object) {
                return object == null ? "" : object.name();
            }

            @Override
            public GroupDto fromString(String string) {
                return null;
            }
        };
        groupCombo.setConverter(converter);
        groupCombo.setItems(groupsList);
    }

    private void loadData() {
        groupsList.setAll(groupService.getAllGroups());
    }

    @FXML
    private void handleGenerateGroupReport() {
        GroupDto selectedGroup = groupCombo.getValue();
        if (selectedGroup == null) {
            showError("Please select a group.");
            return;
        }

        try {
            File report = reportService.generateGroupListReport(selectedGroup.id());
            statusLabel.setText("Report generated: " + report.getName());

            // Offer to open
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText("Report created successfully.");
            alert.setContentText("Do you want to open it now?");

            if (alert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(report);
                }
            }
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
