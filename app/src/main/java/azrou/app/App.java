package azrou.app;

import azrou.app.config.DatabaseManager;
import azrou.app.repo.AdminRepository;
import azrou.app.service.AuthService;
import azrou.app.repo.AbsenceRepository;
import azrou.app.repo.AssessmentRepository;
import azrou.app.repo.GradeRepository;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import azrou.app.repo.SubjectRepository;
import azrou.app.service.AbsenceService;
import azrou.app.service.AssessmentService;
import azrou.app.service.BackupService;
import azrou.app.service.CsvImportService;
import azrou.app.service.ReportService;
import azrou.app.service.GradeService;
import azrou.app.service.GroupService;
import azrou.app.service.StorageService;
import azrou.app.service.StudentService;
import azrou.app.service.SubjectService;
import azrou.app.service.ServiceLocator;
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

        // Initialize Database
        DatabaseManager.initialize();

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
        AdminRepository adminRepo = new AdminRepository();
        AuthService authService = new AuthService(adminRepo);

        // Create default admin if needed
        authService.createInitialAdminIfNotExists();

        ServiceLocator.getInstance().register(AuthService.class, authService);

        GroupRepository groupRepo = new GroupRepository();
        GroupService groupService = new GroupService(groupRepo);
        ServiceLocator.getInstance().register(GroupService.class, groupService);

        StorageService storageService = new StorageService();
        ServiceLocator.getInstance().register(StorageService.class, storageService);

        StudentRepository studentRepo = new StudentRepository();
        StudentService studentService = new StudentService(studentRepo, groupRepo, storageService);
        ServiceLocator.getInstance().register(StudentService.class, studentService);

        SubjectRepository subjectRepo = new SubjectRepository();
        SubjectService subjectService = new SubjectService(subjectRepo, groupRepo);
        ServiceLocator.getInstance().register(SubjectService.class, subjectService);

        AssessmentRepository assessmentRepo = new AssessmentRepository();
        AssessmentService assessmentService = new AssessmentService(assessmentRepo, subjectRepo);
        ServiceLocator.getInstance().register(AssessmentService.class, assessmentService);

        GradeRepository gradeRepo = new GradeRepository();
        GradeService gradeService = new GradeService(gradeRepo, studentRepo, assessmentRepo);
        ServiceLocator.getInstance().register(GradeService.class, gradeService);

        AbsenceRepository absenceRepo = new AbsenceRepository();
        AbsenceService absenceService = new AbsenceService(absenceRepo, studentRepo, subjectRepo);
        ServiceLocator.getInstance().register(AbsenceService.class, absenceService);

        CsvImportService csvImportService = new CsvImportService(studentRepo, groupRepo);
        ServiceLocator.getInstance().register(CsvImportService.class, csvImportService);

        BackupService backupService = new BackupService();
        ServiceLocator.getInstance().register(BackupService.class, backupService);

        ReportService reportService = new ReportService(studentRepo, groupRepo);
        ServiceLocator.getInstance().register(ReportService.class, reportService);
    }
}
