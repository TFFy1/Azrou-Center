package azrou.app.repo;

import azrou.app.config.DatabaseManager;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

    private static File dbFile;
    private StudentRepository studentRepository;
    private GroupRepository groupRepository;

    @BeforeAll
    static void initDb() throws IOException {
        dbFile = File.createTempFile("azrou_test_", ".db");
        String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        DatabaseManager.initialize(dbUrl);
    }

    @AfterAll
    static void closeDb() throws IOException {
        DatabaseManager.shutdown();
        if (dbFile.exists()) {
            Files.delete(dbFile.toPath());
        }
    }

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository();
        groupRepository = new GroupRepository();

        // Clean up data? Or rely on create-drop?
        // Since we used 'update' in DatabaseManager, we might need to manually clean.
        // But for a simple test suite, we can just create fresh data.
    }

    @Test
    void saveAndFindStudent() {
        // Arrange
        Group group = new Group();
        group.setName("Integration Group");
        group.setCreatedAt(LocalDateTime.now());
        groupRepository.save(group);

        Student student = new Student();
        student.setGroup(group);
        student.setFullName("Integration Student");
        student.setCin("INT123");
        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
        student.setCreatedAt(LocalDateTime.now());

        // Act
        studentRepository.save(student);
        Optional<Student> found = studentRepository.findByCin("INT123");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Integration Student", found.get().getFullName());
        assertEquals(group.getId(), found.get().getGroup().getId());
    }

    @Test
    void findByGroupId() {
        // Arrange
        Group group = new Group();
        group.setName("Group A");
        group.setCreatedAt(LocalDateTime.now());
        groupRepository.save(group);

        Student s1 = new Student();
        s1.setGroup(group);
        s1.setFullName("S1");
        s1.setCin("G1S1");
        s1.setCreatedAt(LocalDateTime.now());
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setGroup(group);
        s2.setFullName("S2");
        s2.setCin("G1S2");
        s2.setCreatedAt(LocalDateTime.now());
        studentRepository.save(s2);

        // Act
        List<Student> students = studentRepository.findByGroupId(group.getId());

        // Assert
        assertEquals(2, students.size());
    }
}
