package azrou.app.service;

import azrou.app.model.dto.StudentDto;
import azrou.app.model.entity.Group;
import azrou.app.model.entity.Student;
import azrou.app.repo.GroupRepository;
import azrou.app.repo.StudentRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StorageService storageService;

    public StudentService(StudentRepository studentRepository, GroupRepository groupRepository,
            StorageService storageService) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.storageService = storageService;
    }

    public List<StudentDto> getStudentsByGroup(Integer groupId) {
        return studentRepository.findByGroupId(groupId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto createStudent(Integer groupId, String fullName, String cin, String qualifications,
            LocalDate dateOfBirth, String phone, File photoFile) throws IOException {

        if (studentRepository.findByCin(cin).isPresent()) {
            throw new IllegalArgumentException("Student with CIN " + cin + " already exists.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        Student student = new Student();
        student.setGroup(group);
        student.setFullName(fullName);
        student.setCin(cin);
        student.setQualifications(qualifications);
        student.setDateOfBirth(dateOfBirth);
        student.setPhone(phone);

        if (photoFile != null) {
            String photoPath = storageService.storePhoto(photoFile);
            student.setPhotoPath(photoPath);
        }

        studentRepository.save(student);
        logger.info("Created student: {}", cin);
        return toDto(student);
    }

    public void updateStudent(Integer id, Integer groupId, String fullName, String cin, String qualifications,
            LocalDate dateOfBirth, String phone, File photoFile) throws IOException {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        // Check CIN uniqueness if changed
        if (!student.getCin().equals(cin) && studentRepository.findByCin(cin).isPresent()) {
            throw new IllegalArgumentException("Student with CIN " + cin + " already exists.");
        }

        if (!student.getGroup().getId().equals(groupId)) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
            student.setGroup(group);
        }

        student.setFullName(fullName);
        student.setCin(cin);
        student.setQualifications(qualifications);
        student.setDateOfBirth(dateOfBirth);
        student.setPhone(phone);

        if (photoFile != null) {
            // Delete old photo if exists (optional, or rely on retention policy)
            // storageService.deletePhoto(student.getPhotoPath());
            String photoPath = storageService.storePhoto(photoFile);
            student.setPhotoPath(photoPath);
        }

        studentRepository.update(student);
        logger.info("Updated student: {}", cin);
    }

    public void deleteStudent(Integer id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            // Move photo to recycle bin
            if (student.getPhotoPath() != null) {
                storageService.deletePhoto(student.getPhotoPath());
            }
            studentRepository.delete(student);
            logger.info("Deleted student with ID: {}", id);
        }
    }

    private StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getGroup().getId(),
                student.getGroup().getName(),
                student.getFullName(),
                student.getCin(),
                student.getQualifications(),
                student.getDateOfBirth(),
                student.getPhone(),
                student.getPhotoPath(),
                student.getCreatedAt());
    }
}
