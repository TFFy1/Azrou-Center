package azrou.app.ui.main;

import azrou.app.i18n.I18n;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private BorderPane mainLayout;
    @FXML
    private VBox sideMenu;
    @FXML
    private Label userLabel;
    @FXML
    private Button studentsButton;
    @FXML
    private Button groupsButton;
    @FXML
    private Button subjectsButton;
    @FXML
    private Button teachersButton;
    @FXML
    private Button assessmentsButton;
    @FXML
    private Button gradesButton;
    @FXML
    private Button absencesButton;
    @FXML
    private Button importButton;
    @FXML
    private Button backupButton;
    @FXML
    private Button reportsButton;
    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        bindI18n();
        // Load default view
        showStudents();

        // Setup keyboard shortcuts once scene is ready
        mainLayout.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setupKeyboardShortcuts(newScene);
            }
        });
    }

    private void setupKeyboardShortcuts(javafx.scene.Scene scene) {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case DIGIT1 -> showStudents();
                    case DIGIT2 -> showGroups();
                    case DIGIT3 -> showSubjects();
                    case DIGIT4 -> showAssessments();
                    case DIGIT5 -> showGrades();
                    case DIGIT6 -> showAbsences();
                    case DIGIT7 -> showImport();
                    case DIGIT8 -> showBackup();
                    case DIGIT9 -> showReports();
                    default -> {
                    }
                }
            }
        });
    }

    private void bindI18n() {
        studentsButton.textProperty().bind(I18n.createStringBinding("menu.students"));
        groupsButton.textProperty().bind(I18n.createStringBinding("menu.groups"));
        subjectsButton.textProperty().bind(I18n.createStringBinding("menu.subjects"));
        teachersButton.textProperty().bind(I18n.createStringBinding("menu.teachers"));
        // Add other bindings as keys become available
        assessmentsButton.setText("Assessments");
        gradesButton.setText("Grades");
        absencesButton.setText("Absences");
        importButton.setText("Import");
        backupButton.setText("Backup & Restore");
        reportsButton.setText("Reports");
        logoutButton.setText("Logout");
    }

    @FXML
    private void showStudents() {
        loadView("/azrou/app/ui/students/student.fxml");
    }

    @FXML
    private void showGroups() {
        loadView("/azrou/app/ui/groups/groups.fxml");
    }

    @FXML
    private void showSubjects() {
        loadView("/azrou/app/ui/subjects/subject.fxml");
    }

    @FXML
    private void showTeachers() {
        loadView("/azrou/app/ui/teachers/teacher.fxml");
    }

    @FXML
    private void showAssessments() {
        loadView("/azrou/app/ui/assessments/assessment.fxml");
    }

    @FXML
    private void showGrades() {
        loadView("/azrou/app/ui/grades/grade.fxml");
    }

    @FXML
    private void showAbsences() {
        loadView("/azrou/app/ui/absences/absence.fxml");
    }

    @FXML
    private void showImport() {
        loadView("/azrou/app/ui/imports/csv_import.fxml");
    }

    @FXML
    private void showBackup() {
        loadView("/azrou/app/ui/backup/backup.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/azrou/app/ui/reports/reports.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/azrou/app/ui/login/login.fxml"));
            mainLayout.getScene().setRoot(loginView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Show error in center
            Label errorLabel = new Label("Error loading view: " + e.getMessage());
            mainLayout.setCenter(errorLabel);
        }
    }
}
