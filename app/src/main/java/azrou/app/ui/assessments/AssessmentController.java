package azrou.app.ui.assessments;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.AssessmentDto;
import azrou.app.model.dto.SubjectDto;
import azrou.app.service.AssessmentService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.SubjectService;
import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class AssessmentController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<SubjectDto> subjectFilterCombo;
    @FXML
    private TableView<AssessmentDto> assessmentsTable;
    @FXML
    private TableColumn<AssessmentDto, String> nameColumn;
    @FXML
    private TableColumn<AssessmentDto, LocalDate> dateColumn;
    @FXML
    private TableColumn<AssessmentDto, Double> maxScoreColumn;
    @FXML
    private TableColumn<AssessmentDto, Double> weightColumn;
    @FXML
    private TableColumn<AssessmentDto, String> subjectColumn;

    @FXML
    private ComboBox<SubjectDto> subjectCombo;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField maxScoreField;
    @FXML
    private TextField weightField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final AssessmentService assessmentService;
    private final SubjectService subjectService;
    private final ObservableList<AssessmentDto> assessmentsList = FXCollections.observableArrayList();
    private final ObservableList<SubjectDto> subjectsList = FXCollections.observableArrayList();

    private AssessmentDto selectedAssessment;

    public AssessmentController() {
        this.assessmentService = ServiceLocator.getInstance().get(AssessmentService.class);
        this.subjectService = ServiceLocator.getInstance().get(SubjectService.class);
    }

    @FXML
    public void initialize() {
        try {
            setupTable();
            setupCombos();
            setupBindings();

            // Defer data loading
            javafx.application.Platform.runLater(this::loadData);

            assessmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectAssessment(newVal);
                }
            });

            subjectFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadAssessments(newVal);
            });
        } catch (Exception e) {
            showError("Error initializing Assessment view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().date()));
        maxScoreColumn
                .setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().maxScore()).asObject());
        weightColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().weight()).asObject());
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subjectName()));

        assessmentsTable.setItems(assessmentsList);
    }

    private void setupCombos() {
        StringConverter<SubjectDto> converter = new StringConverter<>() {
            @Override
            public String toString(SubjectDto object) {
                return object == null ? "" : object.name() + " (" + object.groupName() + ")";
            }

            @Override
            public SubjectDto fromString(String string) {
                return null;
            }
        };

        subjectCombo.setConverter(converter);
        subjectFilterCombo.setConverter(converter);

        subjectCombo.setItems(subjectsList);
        subjectFilterCombo.setItems(subjectsList);
    }

    private void loadData() {
        subjectsList.setAll(subjectService.getAllSubjects());
        loadAssessments(null);
    }

    private void loadAssessments(SubjectDto subjectFilter) {
        if (subjectFilter != null) {
            assessmentsList.setAll(assessmentService.getAssessmentsBySubject(subjectFilter.id()));
        } else {
            assessmentsList.setAll(assessmentService.getAllAssessments());
        }
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.assessments"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectAssessment(AssessmentDto assessment) {
        selectedAssessment = assessment;
        nameField.setText(assessment.name());
        datePicker.setValue(assessment.date());
        maxScoreField.setText(String.valueOf(assessment.maxScore()));
        weightField.setText(String.valueOf(assessment.weight()));

        for (SubjectDto s : subjectsList) {
            if (s.id().equals(assessment.subjectId())) {
                subjectCombo.setValue(s);
                break;
            }
        }

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        try {
            SubjectDto subject = subjectCombo.getValue();
            if (subject == null)
                throw new IllegalArgumentException("Please select a subject");

            assessmentService.createAssessment(
                    subject.id(),
                    nameField.getText(),
                    datePicker.getValue(),
                    Double.parseDouble(maxScoreField.getText()),
                    Double.parseDouble(weightField.getText()));

            loadAssessments(subjectFilterCombo.getValue());
            handleClear();
        } catch (NumberFormatException e) {
            showError("Invalid number format for Max Score or Weight");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedAssessment == null)
            return;
        try {
            SubjectDto subject = subjectCombo.getValue();
            if (subject == null)
                throw new IllegalArgumentException("Please select a subject");

            assessmentService.updateAssessment(
                    selectedAssessment.id(),
                    subject.id(),
                    nameField.getText(),
                    datePicker.getValue(),
                    Double.parseDouble(maxScoreField.getText()),
                    Double.parseDouble(weightField.getText()));

            loadAssessments(subjectFilterCombo.getValue());
            handleClear();
        } catch (NumberFormatException e) {
            showError("Invalid number format for Max Score or Weight");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedAssessment == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Assessment " + selectedAssessment.name() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                assessmentService.deleteAssessment(selectedAssessment.id());
                loadAssessments(subjectFilterCombo.getValue());
                handleClear();
            } catch (Exception e) {
                showError("Error deleting assessment", e);
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedAssessment = null;
        nameField.clear();
        datePicker.setValue(null);
        maxScoreField.clear();
        weightField.clear();
        subjectCombo.setValue(null);
        assessmentsTable.getSelectionModel().clearSelection();

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
