package azrou.app.service;

import azrou.app.dao.TeacherDAO;
import azrou.app.model.dto.TeacherDto;
import azrou.app.model.entity.Teacher;
import java.util.List;
import java.util.stream.Collectors;

public class TeacherService {
    private final TeacherDAO teacherDAO;

    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
    }

    public List<TeacherDto> getAllTeachers() {
        return teacherDAO.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TeacherDto createTeacher(String fullName, String email, String phone) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher full name cannot be empty");
        }

        Teacher teacher = new Teacher();
        teacher.setFullName(fullName.trim());
        teacher.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        teacher.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);

        Teacher saved = teacherDAO.save(teacher);
        return mapToDto(saved);
    }

    public void updateTeacher(Integer id, String fullName, String email, String phone) {
        if (id == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher full name cannot be empty");
        }

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setFullName(fullName.trim());
        teacher.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        teacher.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);

        teacherDAO.update(teacher);
    }

    public void deleteTeacher(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        teacherDAO.delete(id);
    }

    private TeacherDto mapToDto(Teacher teacher) {
        return new TeacherDto(
                teacher.getId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.getPhone(),
                teacher.getCreatedAt());
    }
}
