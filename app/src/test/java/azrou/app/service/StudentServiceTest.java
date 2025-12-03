package azrou.app.service;

import azrou.app.model.dto.StudentDto;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private StorageService storageService;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, groupRepository, storageService);
    }

    @Test
    void createStudent_ShouldCreateStudent_WhenValidData() throws IOException {
        // Arrange
        Integer groupId = 1;
        String cin = "AB123456";
        Group group = new Group();
        group.setId(groupId);
        group.setName("Test Group");

        when(studentRepository.findByCin(cin)).thenReturn(Optional.empty());
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(storageService.storePhoto(any(File.class))).thenReturn("path/to/photo.jpg");

        // Act
        StudentDto result = studentService.createStudent(groupId, "John Doe", cin, "B1", LocalDate.of(2000, 1, 1),
                "0600000000", new File("photo.jpg"));

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.fullName());
        assertEquals(cin, result.cin());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_ShouldThrowException_WhenCinExists() {
        // Arrange
        String cin = "AB123456";
        when(studentRepository.findByCin(cin)).thenReturn(Optional.of(new Student()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudent(1, "John Doe", cin, "B1", LocalDate.now(), "0600000000", null);
        });
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudent_ShouldDeleteStudent_WhenExists() {
        // Arrange
        Integer studentId = 1;
        Student student = new Student();
        student.setId(studentId);
        student.setPhotoPath("path/to/photo.jpg");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(storageService).deletePhoto("path/to/photo.jpg");
        verify(studentRepository).delete(student);
    }
}
