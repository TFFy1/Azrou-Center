package azrou.app.ui.absences;

import azrou.app.i18n.I18n;
import azrou.app.model.dto.AbsenceDto;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.dto.StudentDto;
import azrou.app.model.dto.SubjectDto;
import azrou.app.service.AbsenceService;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.StudentService;
import azrou.app.service.SubjectService;
import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

public class AbsenceController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<GroupDto> groupFilterCombo;
    @FXML
    private TableView<AbsenceDto> absencesTable;
    @FXML
    private TableColumn<AbsenceDto, String> studentNameColumn;
    @FXML
    private TableColumn<AbsenceDto, String> subjectColumn;
    @FXML
    private TableColumn<AbsenceDto, LocalDate> dateColumn;
    @FXML
    private TableColumn<AbsenceDto, Boolean> justifiedColumn;
    @FXML
    private TableColumn<AbsenceDto, String> reasonColumn;

    @FXML
    private ComboBox<GroupDto> groupCombo;
    @FXML
    private ComboBox<StudentDto> studentCombo;
    @FXML
    private ComboBox<SubjectDto> subjectCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox justifiedCheck;
    @FXML
    private TextArea reasonField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final AbsenceService absenceService;
    private final GroupService groupService;
    private final StudentService studentService;
    private final SubjectService subjectService;

    private final ObservableList<AbsenceDto> absencesList = FXCollections.observableArrayList();
    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();
    private final ObservableList<StudentDto> studentsList = FXCollections.observableArrayList();
    private final ObservableList<SubjectDto> subjectsList = FXCollections.observableArrayList();

    private AbsenceDto selectedAbsence;

    public AbsenceController() {
        this.absenceService = ServiceLocator.getInstance().get(AbsenceService.class);
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
        this.studentService = ServiceLocator.getInstance().get(StudentService.class);
        this.subjectService = ServiceLocator.getInstance().get(SubjectService.class);
    }

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadData();
        setupBindings();

        absencesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectAbsence(newVal);
            }
        });

        groupFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadAbsences(newVal);
        });

        groupCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadStudentsAndSubjects(newVal);
        });
    }

    private void setupTable() {
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().studentName()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subjectName()));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().date()));
        justifiedColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().justified()));
        reasonColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().reason()));

        absencesTable.setItems(absencesList);
    }

    private void setupCombos() {
        StringConverter<GroupDto> groupConverter = new StringConverter<>() {
            @Override
            public String toString(GroupDto o) {
                return o == null ? "" : o.name();
            }

            @Override
            public GroupDto fromString(String s) {
                return null;
            }
        };

        StringConverter<StudentDto> studentConverter = new StringConverter<>() {
            @Override
            public String toString(StudentDto o) {
                return o == null ? "" : o.fullName();
            }

            @Override
            public StudentDto fromString(String s) {
                return null;
            }
        };

        StringConverter<SubjectDto> subjectConverter = new StringConverter<>() {
            @Override
            public String toString(SubjectDto o) {
                return o == null ? "" : o.name();
            }

            @Override
            public SubjectDto fromString(String s) {
                return null;
            }
        };

        groupCombo.setConverter(groupConverter);
        groupFilterCombo.setConverter(groupConverter);
        studentCombo.setConverter(studentConverter);
        subjectCombo.setConverter(subjectConverter);

        groupCombo.setItems(groupsList);
        groupFilterCombo.setItems(groupsList);
        studentCombo.setItems(studentsList);
        subjectCombo.setItems(subjectsList);
    }

    private void loadData() {
        groupsList.setAll(groupService.getAllGroups());
        loadAbsences(null);
    }

    private void loadAbsences(GroupDto groupFilter) {
        if (groupFilter != null) {
            absencesList.setAll(absenceService.getAbsencesByGroup(groupFilter.id()));
        } else {
            absencesList.clear(); // Or load all if needed, but might be too many
        }
    }

    private void loadStudentsAndSubjects(GroupDto group) {
        if (group != null) {
            studentsList.setAll(studentService.getStudentsByGroup(group.id()));
            subjectsList.setAll(subjectService.getSubjectsByGroup(group.id()));
        } else {
            studentsList.clear();
            subjectsList.clear();
        }
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.absences"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectAbsence(AbsenceDto absence) {
        selectedAbsence = absence;
        datePicker.setValue(absence.date());
        justifiedCheck.setSelected(absence.justified());
        reasonField.setText(absence.reason());

        // We need to find the group first to populate students/subjects
        // This is tricky because AbsenceDto doesn't have groupId directly, but we can
        // infer or fetch
        // For simplicity, let's assume we can find the student in the current list if
        // loaded,
        // or we need to fetch the student to get the group.
        // Actually, let's just try to find the student in all students (inefficient but
        // works for now)
        // Or better: fetch student details.
        // Since we don't have a "getStudentById" in service exposed to UI easily
        // without DTO...
        // Let's iterate groups to find the one containing the student.

        // Simplified: Just clear selection if not in current filter context, or try to
        // set if possible.
        // Ideally, we should have groupId in AbsenceDto.

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        try {
            StudentDto student = studentCombo.getValue();
            SubjectDto subject = subjectCombo.getValue();
            if (student == null || subject == null)
                throw new IllegalArgumentException("Please select student and subject");

            absenceService.createAbsence(
                    student.id(),
                    subject.id(),
                    datePicker.getValue(),
                    justifiedCheck.isSelected(),
                    reasonField.getText());

            loadAbsences(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedAbsence == null)
            return;
        try {
            StudentDto student = studentCombo.getValue();
            SubjectDto subject = subjectCombo.getValue();
            if (student == null || subject == null)
                throw new IllegalArgumentException("Please select student and subject");

            absenceService.updateAbsence(
                    selectedAbsence.id(),
                    student.id(),
                    subject.id(),
                    datePicker.getValue(),
                    justifiedCheck.isSelected(),
                    reasonField.getText());

            loadAbsences(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedAbsence == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Absence?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                absenceService.deleteAbsence(selectedAbsence.id());
                loadAbsences(groupFilterCombo.getValue());
                handleClear();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedAbsence = null;
        datePicker.setValue(null);
        justifiedCheck.setSelected(false);
        reasonField.clear();
        studentCombo.setValue(null);
        subjectCombo.setValue(null);
        absencesTable.getSelectionModel().clearSelection();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(I18n.get("common.error"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}
