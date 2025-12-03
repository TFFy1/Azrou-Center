package azrou.app.ui.subjects;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.dto.SubjectDto;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.SubjectService;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.util.StringConverter;

public class SubjectController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<GroupDto> groupFilterCombo;
    @FXML
    private TableView<SubjectDto> subjectsTable;
    @FXML
    private TableColumn<SubjectDto, String> nameColumn;
    @FXML
    private TableColumn<SubjectDto, String> codeColumn;
    @FXML
    private TableColumn<SubjectDto, String> groupColumn;

    @FXML
    private ComboBox<GroupDto> groupCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button manageButton;

    private final SubjectService subjectService;
    private final GroupService groupService;
    private final ObservableList<SubjectDto> subjectsList = FXCollections.observableArrayList();
    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();

    private SubjectDto selectedSubject;

    public SubjectController() {
        this.subjectService = ServiceLocator.getInstance().get(SubjectService.class);
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
    }

    @FXML
    public void initialize() {
        try {
            setupTable();
            setupCombos();
            setupBindings();

            // Defer data loading to ensure UI is ready and to catch DB errors safely
            javafx.application.Platform.runLater(this::loadData);

            subjectsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectSubject(newVal);
                }
            });

            groupFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadSubjects(newVal);
            });
        } catch (Exception e) {
            showError("Error initializing Subject view", e);
            e.printStackTrace();
        }
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
        codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().code()));
        groupColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().groupName()));

        subjectsTable.setItems(subjectsList);
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
        groupFilterCombo.setConverter(converter);

        groupCombo.setItems(groupsList);
        groupFilterCombo.setItems(groupsList);
    }

    private void loadData() {
        groupsList.setAll(groupService.getAllGroups());
        loadSubjects(null);
    }

    private void loadSubjects(GroupDto groupFilter) {
        if (groupFilter != null) {
            subjectsList.setAll(subjectService.getSubjectsByGroup(groupFilter.id()));
        } else {
            subjectsList.setAll(subjectService.getAllSubjects());
        }
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.subjects"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectSubject(SubjectDto subject) {
        selectedSubject = subject;
        nameField.setText(subject.name());
        codeField.setText(subject.code());

        for (GroupDto g : groupsList) {
            if (g.id().equals(subject.groupId())) {
                groupCombo.setValue(g);
                break;
            }
        }

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        manageButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        try {
            GroupDto group = groupCombo.getValue();
            if (group == null)
                throw new IllegalArgumentException("Please select a group");

            subjectService.createSubject(
                    group.id(),
                    nameField.getText(),
                    codeField.getText());

            loadSubjects(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError("Error creating subject", e);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedSubject == null)
            return;
        try {
            GroupDto group = groupCombo.getValue();
            if (group == null)
                throw new IllegalArgumentException("Please select a group");

            subjectService.updateSubject(
                    selectedSubject.id(),
                    group.id(),
                    nameField.getText(),
                    codeField.getText());

            loadSubjects(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError("Error updating subject", e);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedSubject == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Subject " + selectedSubject.name() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                subjectService.deleteSubject(selectedSubject.id());
                loadSubjects(groupFilterCombo.getValue());
                handleClear();
            } catch (Exception e) {
                showError("Error deleting subject", e);
            }
        }
    }

    @FXML
    private void handleManage() {
        if (selectedSubject == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/azrou/app/ui/subjects/subject_details.fxml"));
            Parent root = loader.load();

            SubjectDetailsController controller = loader.getController();
            controller.setSubject(selectedSubject);

            Stage stage = new Stage();
            stage.setTitle("Subject Details: " + selectedSubject.name());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showError("Failed to open details view", e);
        }
    }

    @FXML
    private void handleClear() {
        selectedSubject = null;
        nameField.clear();
        codeField.clear();
        groupCombo.setValue(null);
        subjectsTable.getSelectionModel().clearSelection();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        manageButton.setDisable(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setHeaderText("Error");

        // Unpack the cause if available to show the real SQL error
        if (message.contains("Error") || message.contains("Exception")) {
            // This is a bit hacky, but we want to see the underlying error
            // The caller usually passes e.getMessage() which might be the wrapper
        }

        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String context, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setHeaderText(context);

        String msg = e.getMessage();
        if (e.getCause() != null) {
            msg += "\nCaused by: " + e.getCause().getMessage();
        }

        alert.setContentText(msg);
        alert.showAndWait();
    }
}
