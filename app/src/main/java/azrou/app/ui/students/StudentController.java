package azrou.app.ui.students;

import azrou.app.config.AppConfig;
import azrou.app.i18n.I18n;
import azrou.app.model.dto.GroupDto;
import azrou.app.model.dto.StudentDto;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.StudentService;
import java.io.File;
import java.util.Optional;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

public class StudentController {
    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<GroupDto> groupFilterCombo;
    @FXML
    private TableView<StudentDto> studentsTable;
    @FXML
    private TableColumn<StudentDto, String> cinColumn;
    @FXML
    private TableColumn<StudentDto, String> nameColumn;
    @FXML
    private TableColumn<StudentDto, String> groupColumn;
    @FXML
    private TableColumn<StudentDto, String> phoneColumn;

    @FXML
    private ComboBox<GroupDto> groupCombo;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField cinField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea qualificationsField;
    @FXML
    private ImageView photoView;
    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final StudentService studentService;
    private final GroupService groupService;
    private final ObservableList<StudentDto> studentsList = FXCollections.observableArrayList();
    private final ObservableList<GroupDto> groupsList = FXCollections.observableArrayList();

    private StudentDto selectedStudent;
    private File selectedPhotoFile;

    public StudentController() {
        this.studentService = ServiceLocator.getInstance().get(StudentService.class);
        this.groupService = ServiceLocator.getInstance().get(GroupService.class);
    }

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadData();
        setupBindings();

        studentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectStudent(newVal);
            }
        });

        groupFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadStudents(newVal);
        });
    }

    private void setupTable() {
        cinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().cin()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fullName()));
        groupColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().groupName()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().phone()));

        studentsTable.setItems(studentsList);
    }

    private void setupCombos() {
        StringConverter<GroupDto> converter = new StringConverter<>() {
            @Override
            public String toString(GroupDto object) {
                return object == null ? "" : object.name();
            }

            @Override
            public GroupDto fromString(String string) {
                return null; // Not needed
            }
        };

        groupCombo.setConverter(converter);
        groupFilterCombo.setConverter(converter);

        groupCombo.setItems(groupsList);
        groupFilterCombo.setItems(groupsList);
    }

    private void loadData() {
        groupsList.setAll(groupService.getAllGroups());
        loadStudents(null);
    }

    private void loadStudents(GroupDto groupFilter) {
        if (groupFilter != null) {
            studentsList.setAll(studentService.getStudentsByGroup(groupFilter.id()));
        } else {
            studentsList.setAll(studentService.getAllStudents());
        }
    }

    private void setupBindings() {
        titleLabel.textProperty().bind(I18n.createStringBinding("menu.students"));
        addButton.textProperty().bind(I18n.createStringBinding("common.add"));
        updateButton.textProperty().bind(I18n.createStringBinding("common.edit"));
        deleteButton.textProperty().bind(I18n.createStringBinding("common.delete"));
        clearButton.textProperty().bind(I18n.createStringBinding("common.cancel"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void selectStudent(StudentDto student) {
        selectedStudent = student;
        fullNameField.setText(student.fullName());
        cinField.setText(student.cin());
        phoneField.setText(student.phone());
        qualificationsField.setText(student.qualifications());
        dobPicker.setValue(student.dateOfBirth());

        // Find group in combo
        for (GroupDto g : groupsList) {
            if (g.id().equals(student.groupId())) {
                groupCombo.setValue(g);
                break;
            }
        }

        // Load photo if exists
        if (student.photoPath() != null) {
            try {
                File file = AppConfig.PHOTOS_THUMBNAILS_DIR.resolve(student.photoPath()).toFile();
                if (file.exists()) {
                    photoView.setImage(new Image(file.toURI().toString()));
                } else {
                    // Try original if thumbnail missing
                    file = AppConfig.PHOTOS_ORIGINALS_DIR.resolve(student.photoPath()).toFile();
                    if (file.exists()) {
                        photoView.setImage(new Image(file.toURI().toString()));
                    } else {
                        photoView.setImage(null);
                    }
                }
            } catch (Exception e) {
                photoView.setImage(null);
            }
        } else {
            photoView.setImage(null);
        }

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(titleLabel.getScene().getWindow());
        if (file != null) {
            selectedPhotoFile = file;
            try {
                Image image = new Image(file.toURI().toString());
                photoView.setImage(image);
            } catch (Exception e) {
                showError("Failed to load image preview");
            }
        }
    }

    @FXML
    private void handleAdd() {
        try {
            GroupDto group = groupCombo.getValue();
            if (group == null)
                throw new IllegalArgumentException("Please select a group");

            studentService.createStudent(
                    group.id(),
                    fullNameField.getText(),
                    cinField.getText(),
                    qualificationsField.getText(),
                    dobPicker.getValue(),
                    phoneField.getText(),
                    selectedPhotoFile);

            loadStudents(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null)
            return;
        try {
            GroupDto group = groupCombo.getValue();
            if (group == null)
                throw new IllegalArgumentException("Please select a group");

            studentService.updateStudent(
                    selectedStudent.id(),
                    group.id(),
                    fullNameField.getText(),
                    cinField.getText(),
                    qualificationsField.getText(),
                    dobPicker.getValue(),
                    phoneField.getText(),
                    selectedPhotoFile);

            loadStudents(groupFilterCombo.getValue());
            handleClear();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedStudent == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18n.get("common.confirm"));
        alert.setHeaderText("Delete Student " + selectedStudent.fullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                studentService.deleteStudent(selectedStudent.id());
                loadStudents(groupFilterCombo.getValue());
                handleClear();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedStudent = null;
        selectedPhotoFile = null;
        fullNameField.clear();
        cinField.clear();
        phoneField.clear();
        qualificationsField.clear();
        dobPicker.setValue(null);
        groupCombo.setValue(null);
        photoView.setImage(null);
        studentsTable.getSelectionModel().clearSelection();

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
