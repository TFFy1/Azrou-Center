package azrou.app.service;

import azrou.app.model.dto.GroupDto;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GroupRepository groupRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(studentRepository, groupRepository);
    }

    @Test
    void generateGroupListReport_ShouldGenerateFile_WhenGroupExists() throws IOException {
        // Arrange
        Integer groupId = 1;
        Group group = new Group();
        group.setId(groupId);
        group.setName("Test Group");
        group.setCreatedAt(LocalDateTime.now());

        Student student = new Student();
        student.setId(1);
        student.setGroup(group);
        student.setFullName("John Doe");
        student.setCin("AB123456");
        student.setCreatedAt(LocalDateTime.now());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(studentRepository.findByGroupId(groupId)).thenReturn(List.of(student));

        // Act
        File report = reportService.generateGroupListReport(groupId);

        // Assert
        assertNotNull(report);
        assertTrue(report.getName().startsWith("Group_Test Group_List"));
        // Note: We can't easily verify PDF content without more complex setup,
        // but we verify the file creation logic and interactions.
    }

    @Test
    void generateGroupListReport_ShouldThrowException_WhenGroupNotFound() {
        // Arrange
        Integer groupId = 99;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reportService.generateGroupListReport(groupId);
        });
    }
}
