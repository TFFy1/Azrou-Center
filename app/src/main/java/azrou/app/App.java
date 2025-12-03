package azrou.app;

import azrou.app.db.DatabaseManager;
import azrou.app.service.AbsenceService;
import azrou.app.service.AssessmentService;
import azrou.app.service.AuthService;
import azrou.app.service.BackupService;
import azrou.app.service.CsvImportService;
import azrou.app.service.GradeService;
import azrou.app.service.GroupService;
import azrou.app.service.ReportService;
import azrou.app.service.ServiceLocator;
import azrou.app.service.StorageService;
import azrou.app.service.StudentService;
import azrou.app.service.SubjectService;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting Azrou Center App...");

        // Initialize Directories
        azrou.app.config.AppConfig.initializeDirectories();

        // Initialize Database
        DatabaseManager.getInstance().initialize();

        // Initialize Services
        initializeServices();

        // Load Login View
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/azrou/app/ui/login/login.fxml"));
        Parent root = loader.load();

        // Apply global styles
        root.getStylesheets().add(getClass().getResource("/azrou/app/ui/css/styles.css").toExternalForm());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Azrou Center App");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initializeServices() {
        // AdminRepository adminRepo = new AdminRepository(); // TODO: Implement
        // AdminDAO if needed
        AuthService authService = new AuthService(); // Updated constructor

        // Create default admin if needed
        authService.createInitialAdminIfNotExists();

        ServiceLocator.getInstance().register(AuthService.class, authService);

        GroupService groupService = new GroupService();
        ServiceLocator.getInstance().register(GroupService.class, groupService);

        StorageService storageService = new StorageService();
        ServiceLocator.getInstance().register(StorageService.class, storageService);

        StudentService studentService = new StudentService(storageService);
        ServiceLocator.getInstance().register(StudentService.class, studentService);

        SubjectService subjectService = new SubjectService();
        ServiceLocator.getInstance().register(SubjectService.class, subjectService);

        AssessmentService assessmentService = new AssessmentService();
        ServiceLocator.getInstance().register(AssessmentService.class, assessmentService);

        GradeService gradeService = new GradeService();
        ServiceLocator.getInstance().register(GradeService.class, gradeService);

        AbsenceService absenceService = new AbsenceService();
        ServiceLocator.getInstance().register(AbsenceService.class, absenceService);

        CsvImportService csvImportService = new CsvImportService();
        ServiceLocator.getInstance().register(CsvImportService.class, csvImportService);

        BackupService backupService = new BackupService();
        ServiceLocator.getInstance().register(BackupService.class, backupService);

        ReportService reportService = new ReportService();
        ServiceLocator.getInstance().register(ReportService.class, reportService);
    }
}
