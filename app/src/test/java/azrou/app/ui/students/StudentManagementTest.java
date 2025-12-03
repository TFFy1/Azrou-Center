package azrou.app.ui.students;

import azrou.app.App;
import azrou.app.config.DatabaseManager;
import azrou.app.model.dto.GroupDto;
import azrou.app.service.GroupService;
import azrou.app.service.ServiceLocator;
import java.io.File;
import java.nio.file.Files;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class StudentManagementTest extends ApplicationTest {

    private static File dbFile;

    @BeforeAll
    static void initDb() throws Exception {
        dbFile = File.createTempFile("azrou_ui_test_", ".db");
        String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        // Ensure we start fresh
        DatabaseManager.shutdown();
        DatabaseManager.initialize(dbUrl);
    }

    @AfterAll
    static void tearDownDb() throws Exception {
        DatabaseManager.shutdown();
        if (dbFile != null && dbFile.exists()) {
            Files.deleteIfExists(dbFile.toPath());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

    @Test
    public void shouldAddStudent() {
        // Navigate to Students (default view)

        // We need a group first.
        // Since we are in the UI thread/context, we can use the service locator if
        // initialized.
        // Or we can use the UI to add a group first?
        // Let's assume we are on the Student view.
        // We can't easily add a group from Student view.
        // Let's inject a group using the service directly.

        interact(() -> {
            GroupService groupService = ServiceLocator.getInstance().get(GroupService.class);
            if (groupService != null) {
                groupService.createGroup("UI Test Group", "Description", 20);
            }
        });

        // Refresh UI? The combo box might not update automatically if it loaded before
        // we added the group.
        // The StudentController loads data in initialize().
        // If we add group after initialize, we might need to refresh.
        // But wait, App.start runs, then controller initializes.
        // So we should add group BEFORE App.start?
        // No, ApplicationTest.start runs App.start.
        // So we should add group in `initDb` or `start` before `new
        // App().start(stage)`?
        // But services are initialized IN App.start.

        // Workaround: Add group via UI or restart controller?
        // Or just navigate to Groups, add group, navigate back.

        // Let's try navigating to Groups (Ctrl+2)
        // press(KeyCode.CONTROL, KeyCode.DIGIT2).release(KeyCode.DIGIT2,
        // KeyCode.CONTROL);
        // But let's keep it simple. We can just use the ServiceLocator after App.start
        // but we need to trigger a refresh.
        // StudentController doesn't have a refresh button exposed easily?
        // Actually, `loadData` is called in `initialize`.

        // Let's try to add the group in a `BeforeEach` but we need services.
        // Let's just try to interact with the UI.

        // Verify we are on login screen first?
        // App.start loads Login.fxml.
        // We need to login first.

        clickOn("#usernameField").write("admin");
        clickOn("#passwordField").write("admin123");
        clickOn("#loginButton");

        // Now we are on Main Dashboard (Students view by default).

        // Add a group via UI to be safe
        // Navigate to Groups
        clickOn("Groups"); // Button text
        clickOn("#nameField").write("UI Test Group");
        clickOn("#addButton");

        // Navigate back to Students
        clickOn("Students");

        // Add Student
        clickOn("#fullNameField").write("Test Student");
        clickOn("#cinField").write("UI12345");
        // Select Group
        clickOn("#groupCombo");
        clickOn("UI Test Group");

        clickOn("#addButton");

        // Verify in table
        verifyThat("#studentsTable", (javafx.scene.control.TableView<azrou.app.model.dto.StudentDto> table) -> {
            return table.getItems().stream().anyMatch(s -> s.cin().equals("UI12345"));
        });
    }
}
