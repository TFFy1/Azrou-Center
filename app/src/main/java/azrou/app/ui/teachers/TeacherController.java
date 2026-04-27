package azrou.app.ui.teachers;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.TeacherDto;
import azrou.app.service.ServiceLocator;
import azrou.app.service.TeacherService;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TeacherController {
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<TeacherDto> teachersTable;
    @FXML
    private TableColumn<TeacherDto, String> fullNameColumn;
    @FXML
    private TableColumn<TeacherDto, String> emailColumn;
    @FXML
    private TableColumn<TeacherDto, String> phoneColumn;

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final TeacherService teacherService;
    private final ObservableList<TeacherDto> teachersList = FXCollections.observableArrayList();

    private TeacherDto selectedTeacher;

    public TeacherController() {
        this.teacherService = ServiceLocator.getInstance().get(TeacherService.class);
    }

    @FXML
    public void initialize() {
        try {
            setupTable();
            setupBindings();

            javafx.application.Platform.runLater(this::loadData);

            teachersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectTeacher(newVal);
                }
            });
        } catch (Exception e) {
            showError("Error initializing Teacher view", e);
            e.printStackTrace();
        }
    }

    private void setupTable() {
        fullNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fullName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().email()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().phone()));

        teachersTable.setItems(teachersList);
    }

    private void loadData() {
        teachersList.setAll(teacherService.getAllTeachers());
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.teachers"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectTeacher(TeacherDto teacher) {
        selectedTeacher = teacher;
        fullNameField.setText(teacher.fullName());
        emailField.setText(teacher.email());
        phoneField.setText(teacher.phone());

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        try {
            teacherService.createTeacher(
                    fullNameField.getText(),
                    emailField.getText(),
                    phoneField.getText());

            loadData();
            handleClear();
        } catch (Exception e) {
            showError("Error creating teacher", e);
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedTeacher == null)
            return;
        try {
            teacherService.updateTeacher(
                    selectedTeacher.id(),
                    fullNameField.getText(),
                    emailField.getText(),
                    phoneField.getText());

            loadData();
            handleClear();
        } catch (Exception e) {
            showError("Error updating teacher", e);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedTeacher == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Teacher " + selectedTeacher.fullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                teacherService.deleteTeacher(selectedTeacher.id());
                loadData();
                handleClear();
            } catch (Exception e) {
                showError("Error deleting teacher", e);
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedTeacher = null;
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        teachersTable.getSelectionModel().clearSelection();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setHeaderText("Error");
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
