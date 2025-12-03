package azrou.app.ui.subjects;

import azrou.app.model.dto.AbsenceDto;
import azrou.app.model.dto.AssessmentDto;
import azrou.app.model.dto.GradeDto;
import azrou.app.model.dto.StudentDto;
import azrou.app.model.dto.SubjectDto;
import azrou.app.service.AbsenceService;
import azrou.app.service.AssessmentService;
import azrou.app.service.GradeService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.StudentService;

import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class SubjectDetailsController {

    @FXML
    private Label subjectTitleLabel;
    @FXML
    private Label groupLabel;

    // Assessments Tab
    @FXML
    private TextField assessmentNameField;
    @FXML
    private DatePicker assessmentDatePicker;
    @FXML
    private TextField maxScoreField;
    @FXML
    private TextField weightField;
    @FXML
    private Button addAssessmentButton;
    @FXML
    private Button updateAssessmentButton;
    @FXML
    private Button deleteAssessmentButton;
    @FXML
    private TableView<AssessmentDto> assessmentsTable;
    @FXML
    private TableColumn<AssessmentDto, String> assessmentNameColumn;
    @FXML
    private TableColumn<AssessmentDto, String> assessmentDateColumn;
    @FXML
    private TableColumn<AssessmentDto, Number> maxScoreColumn;
    @FXML
    private TableColumn<AssessmentDto, Number> weightColumn;

    // Grades Tab
    @FXML
    private ComboBox<AssessmentDto> assessmentCombo;
    @FXML
    private TableView<StudentGradeEntry> gradesTable;
    @FXML
    private TableColumn<StudentGradeEntry, String> studentNameColumn;
    @FXML
    private TableColumn<StudentGradeEntry, Double> gradeScoreColumn;
    @FXML
    private Button saveGradesButton;

    // Absences Tab
    @FXML
    private ComboBox<StudentDto> studentCombo;
    @FXML
    private DatePicker absenceDatePicker;
    @FXML
    private TextField reasonField;
    @FXML
    private Button addAbsenceButton;
    @FXML
    private Button deleteAbsenceButton;
    @FXML
    private TableView<AbsenceDto> absencesTable;
    @FXML
    private TableColumn<AbsenceDto, String> absenceDateColumn;
    @FXML
    private TableColumn<AbsenceDto, String> absenceStudentColumn;
    @FXML
    private TableColumn<AbsenceDto, String> absenceReasonColumn;
    @FXML
    private TableColumn<AbsenceDto, String> absenceJustifiedColumn;

    private SubjectDto currentSubject;
    private final AssessmentService assessmentService;
    private final GradeService gradeService;
    private final StudentService studentService;
    private final AbsenceService absenceService;

    private final ObservableList<AssessmentDto> assessmentsList = FXCollections.observableArrayList();
    private final ObservableList<StudentGradeEntry> studentGradesList = FXCollections.observableArrayList();
    private final ObservableList<AbsenceDto> absencesList = FXCollections.observableArrayList();
    private final ObservableList<StudentDto> studentsList = FXCollections.observableArrayList();

    private AssessmentDto selectedAssessment;
    private AbsenceDto selectedAbsence;

    public SubjectDetailsController() {
        this.assessmentService = ServiceLocator.getInstance().get(AssessmentService.class);
        this.gradeService = ServiceLocator.getInstance().get(GradeService.class);
        this.studentService = ServiceLocator.getInstance().get(StudentService.class);
        this.absenceService = ServiceLocator.getInstance().get(AbsenceService.class);
    }

    public void setSubject(SubjectDto subject) {
        this.currentSubject = subject;
        subjectTitleLabel.setText("Subject: " + subject.name());
        groupLabel.setText("Group: " + subject.groupName());
        loadData();
    }

    @FXML
    public void initialize() {
        setupAssessmentsTable();
        setupGradesTable();
        setupAbsencesTable();
        setupCombos();
    }

    private void loadData() {
        if (currentSubject == null)
            return;
        loadAssessments();
        loadStudents();
        loadAbsences();
    }

    private void loadStudents() {
        studentsList.setAll(studentService.getStudentsByGroup(currentSubject.groupId()));
    }

    // --- Assessments ---

    private void setupAssessmentsTable() {
        assessmentNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        assessmentDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().date() != null ? data.getValue().date().toString() : ""));
        maxScoreColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().maxScore()));
        weightColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().weight()));

        assessmentsTable.setItems(assessmentsList);
        assessmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                selectAssessment(newVal);
        });
    }

    private void loadAssessments() {
        assessmentsList.setAll(assessmentService.getAssessmentsBySubject(currentSubject.id()));
        assessmentCombo.setItems(assessmentsList);
    }

    private void selectAssessment(AssessmentDto assessment) {
        selectedAssessment = assessment;
        assessmentNameField.setText(assessment.name());
        assessmentDatePicker.setValue(assessment.date());
        maxScoreField.setText(String.valueOf(assessment.maxScore()));
        weightField.setText(String.valueOf(assessment.weight()));

        addAssessmentButton.setDisable(true);
        updateAssessmentButton.setDisable(false);
        deleteAssessmentButton.setDisable(false);
    }

    @FXML
    private void handleAddAssessment() {
        try {
            assessmentService.createAssessment(
                    currentSubject.id(),
                    assessmentNameField.getText(),
                    assessmentDatePicker.getValue(),
                    Double.parseDouble(maxScoreField.getText()),
                    Double.parseDouble(weightField.getText()));
            loadAssessments();
            handleClearAssessment();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateAssessment() {
        if (selectedAssessment == null)
            return;
        try {
            assessmentService.updateAssessment(
                    selectedAssessment.id(),
                    currentSubject.id(),
                    assessmentNameField.getText(),
                    assessmentDatePicker.getValue(),
                    Double.parseDouble(maxScoreField.getText()),
                    Double.parseDouble(weightField.getText()));
            loadAssessments();
            handleClearAssessment();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAssessment() {
        if (selectedAssessment == null)
            return;
        try {
            assessmentService.deleteAssessment(selectedAssessment.id());
            loadAssessments();
            handleClearAssessment();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleClearAssessment() {
        selectedAssessment = null;
        assessmentNameField.clear();
        assessmentDatePicker.setValue(null);
        maxScoreField.clear();
        weightField.clear();
        assessmentsTable.getSelectionModel().clearSelection();
        addAssessmentButton.setDisable(false);
        updateAssessmentButton.setDisable(true);
        deleteAssessmentButton.setDisable(true);
    }

    // --- Grades ---

    private void setupGradesTable() {
        studentNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStudentName()));
        gradeScoreColumn.setCellValueFactory(data -> data.getValue().scoreProperty().asObject());

        // Make score editable
        gradeScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        gradeScoreColumn.setOnEditCommit(event -> {
            StudentGradeEntry entry = event.getRowValue();
            entry.setScore(event.getNewValue());
        });

        gradesTable.setItems(studentGradesList);

        assessmentCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                loadGradesForAssessment(newVal);
        });
    }

    private void loadGradesForAssessment(AssessmentDto assessment) {
        studentGradesList.clear();
        List<GradeDto> existingGrades = gradeService.getGradesByAssessment(assessment.id());

        for (StudentDto student : studentsList) {
            Optional<GradeDto> grade = existingGrades.stream()
                    .filter(g -> g.studentId().equals(student.id()))
                    .findFirst();

            studentGradesList.add(new StudentGradeEntry(
                    student.id(),
                    student.fullName(),
                    grade.map(GradeDto::score).orElse(0.0)));
        }
    }

    @FXML
    private void handleSaveGrades() {
        AssessmentDto assessment = assessmentCombo.getValue();
        if (assessment == null) {
            showError("Please select an assessment");
            return;
        }

        try {
            for (StudentGradeEntry entry : studentGradesList) {
                gradeService.saveGrade(entry.getStudentId(), assessment.id(), entry.getScore());
            }
            showInfo("Grades saved successfully");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // --- Absences ---

    private void setupAbsencesTable() {
        absenceDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().date().toString()));
        absenceStudentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().studentName()));
        absenceReasonColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().reason()));
        absenceJustifiedColumn
                .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().justified() ? "Yes" : "No"));

        absencesTable.setItems(absencesList);
        absencesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedAbsence = newVal;
                deleteAbsenceButton.setDisable(false);
            }
        });
    }

    private void setupCombos() {
        studentCombo.setItems(studentsList);
        studentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudentDto object) {
                return object == null ? "" : object.fullName();
            }

            @Override
            public StudentDto fromString(String string) {
                return null;
            }
        });

        assessmentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(AssessmentDto object) {
                return object == null ? "" : object.name();
            }

            @Override
            public AssessmentDto fromString(String string) {
                return null;
            }
        });
    }

    private void loadAbsences() {
        absencesList.setAll(absenceService.getAbsencesBySubject(currentSubject.id()));
    }

    @FXML
    private void handleAddAbsence() {
        try {
            StudentDto student = studentCombo.getValue();
            if (student == null)
                throw new IllegalArgumentException("Select a student");
            if (absenceDatePicker.getValue() == null)
                throw new IllegalArgumentException("Select a date");

            absenceService.createAbsence(
                    student.id(),
                    currentSubject.id(),
                    absenceDatePicker.getValue(),
                    false, // Default unjustified
                    reasonField.getText());
            loadAbsences();
            reasonField.clear();
            absenceDatePicker.setValue(null);
            studentCombo.setValue(null);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAbsence() {
        if (selectedAbsence == null)
            return;
        try {
            absenceService.deleteAbsence(selectedAbsence.id());
            loadAbsences();
            deleteAbsenceButton.setDisable(true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper class for Grades Table
    public static class StudentGradeEntry {
        private final Integer studentId;
        private final String studentName;
        private final SimpleDoubleProperty score;

        public StudentGradeEntry(Integer studentId, String studentName, Double score) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.score = new SimpleDoubleProperty(score);
        }

        public Integer getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public Double getScore() {
            return score.get();
        }

        public void setScore(Double score) {
            this.score.set(score);
        }

        public SimpleDoubleProperty scoreProperty() {
            return score;
        }
    }
}
