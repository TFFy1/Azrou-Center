package azrou.app.ui.grades;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.AssessmentDto;
import azrou.app.model.dto.GradeDto;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.dto.StudentDto;
import azrou.app.model.dto.SubjectDto;
import azrou.app.service.AssessmentService;
import azrou.app.service.GradeService;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.StudentService;
import azrou.app.service.SubjectService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class GradeController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<GroupDto> groupCombo;
    @FXML
    private ComboBox<SubjectDto> subjectCombo;
    @FXML
    private ComboBox<AssessmentDto> assessmentCombo;

    @FXML
    private TableView<StudentGradeRow> gradesTable;
    @FXML
    private TableColumn<StudentGradeRow, String> studentNameColumn;
    @FXML
    private TableColumn<StudentGradeRow, String> cinColumn;
    @FXML
    private TableColumn<StudentGradeRow, Double> scoreColumn;

    @FXML
    private Button saveButton;

    private final GroupService groupService;
    private final SubjectService subjectService;
    private final AssessmentService assessmentService;
    private final StudentService studentService;
    private final GradeService gradeService;

    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();
    private final ObservableList<SubjectDto> subjectsList = FXCollections.observableArrayList();
    private final ObservableList<AssessmentDto> assessmentsList = FXCollections.observableArrayList();
    private final ObservableList<StudentGradeRow> studentGradesList = FXCollections.observableArrayList();

    public GradeController() {
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
        this.subjectService = ServiceLocator.getInstance().get(SubjectService.class);
        this.assessmentService = ServiceLocator.getInstance().get(AssessmentService.class);
        this.studentService = ServiceLocator.getInstance().get(StudentService.class);
        this.gradeService = ServiceLocator.getInstance().get(GradeService.class);
    }

    @FXML
    public void initialize() {
        setupCombos();
        setupTable();
        loadGroups();
        setupBindings();

        groupCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadSubjects(newVal);
            assessmentsList.clear();
            studentGradesList.clear();
        });

        subjectCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadAssessments(newVal);
            studentGradesList.clear();
        });

        assessmentCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadStudentGrades(newVal);
        });
    }

    private void setupCombos() {
        groupCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(GroupDto o) {
                return o == null ? "" : o.name();
            }

            @Override
            public GroupDto fromString(String s) {
                return null;
            }
        });

        subjectCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(SubjectDto o) {
                return o == null ? "" : o.name();
            }

            @Override
            public SubjectDto fromString(String s) {
                return null;
            }
        });

        assessmentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(AssessmentDto o) {
                return o == null ? "" : o.name() + " (Max: " + o.maxScore() + ")";
            }

            @Override
            public AssessmentDto fromString(String s) {
                return null;
            }
        });

        groupCombo.setItems(groupsList);
        subjectCombo.setItems(subjectsList);
        assessmentCombo.setItems(assessmentsList);
    }

    private void setupTable() {
        studentNameColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));
        cinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCin()));

        scoreColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getScore()));
        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        scoreColumn.setOnEditCommit(event -> {
            StudentGradeRow row = event.getRowValue();
            row.setScore(event.getNewValue());
        });

        gradesTable.setItems(studentGradesList);
        gradesTable.setEditable(true);
    }

    private void loadGroups() {
        groupsList.setAll(groupService.getAllGroups());
    }

    private void loadSubjects(GroupDto group) {
        if (group != null) {
            subjectsList.setAll(subjectService.getSubjectsByGroup(group.id()));
        } else {
            subjectsList.clear();
        }
    }

    private void loadAssessments(SubjectDto subject) {
        if (subject != null) {
            assessmentsList.setAll(assessmentService.getAssessmentsBySubject(subject.id()));
        } else {
            assessmentsList.clear();
        }
    }

    private void loadStudentGrades(AssessmentDto assessment) {
        if (assessment == null) {
            studentGradesList.clear();
            return;
        }

        GroupDto group = groupCombo.getValue();
        if (group == null)
            return;

        List<StudentDto> students = studentService.getStudentsByGroup(group.id());
        List<GradeDto> grades = gradeService.getGradesByAssessment(assessment.id());

        Map<Integer, Double> gradeMap = new HashMap<>();
        for (GradeDto g : grades) {
            gradeMap.put(g.studentId(), g.score());
        }

        studentGradesList.clear();
        for (StudentDto s : students) {
            Double score = gradeMap.get(s.id());
            studentGradesList.add(new StudentGradeRow(s.id(), s.fullName(), s.cin(), score));
        }
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.grades"));
        saveButton.textProperty().bind(I18n.createStringBinding("common.save"));
    }

    @FXML
    private void handleSave() {
        AssessmentDto assessment = assessmentCombo.getValue();
        if (assessment == null)
            return;

        try {
            for (StudentGradeRow row : studentGradesList) {
                if (row.getScore() != null) {
                    gradeService.saveGrade(row.getStudentId(), assessment.id(), row.getScore());
                }
            }
            showInfo("Grades saved successfully!");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(I18n.get("common.info"));
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StudentGradeRow {
        private final Integer studentId;
        private final String studentName;
        private final String cin;
        private Double score;

        public StudentGradeRow(Integer studentId, String studentName, String cin, Double score) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.cin = cin;
            this.score = score;
        }

        public Integer getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getCin() {
            return cin;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }
    }
}
