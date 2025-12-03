package azrou.app.service;

import azrou.app.dao.AbsenceDAO;
import azrou.app.dao.StudentDAO;
import azrou.app.dao.SubjectDAO;
import azrou.app.model.dto.AbsenceDto;
import azrou.app.model.entity.Absence;
import azrou.app.model.entity.Student;
import azrou.app.model.entity.Subject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsenceService {
        private static final Logger logger = LoggerFactory.getLogger(AbsenceService.class);
        private final AbsenceDAO absenceDAO;
        private final StudentDAO studentDAO;
        private final SubjectDAO subjectDAO;

        public AbsenceService() {
                this.absenceDAO = new AbsenceDAO();
                this.studentDAO = new StudentDAO();
                this.subjectDAO = new SubjectDAO();
        }

        public List<AbsenceDto> getAbsencesByGroup(Integer groupId) {
                // AbsenceDAO does not have findByGroupId, but we can filter or add it.
                // Or we can get students by group and then absences for each student.
                // Let's assume we need to add findByGroupId to AbsenceDAO or use a custom
                // query.
                // For now, let's use a workaround or add it to DAO.
                // I'll add findByGroupId to AbsenceDAO in a separate step or just use what I
                // have.
                // Wait, I implemented findByStudentId.
                // I can get all students in group, then all absences for those students.
                // Or better, add findByGroupId to AbsenceDAO.
                // For now, I'll comment this out or fix it.
                // Actually, the original code used absenceRepository.findByGroupId(groupId).
                // I should add findByGroupId to AbsenceDAO.
                return absenceDAO.findByGroupId(groupId).stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        public List<AbsenceDto> getAbsencesBySubject(Integer subjectId) {
                return absenceDAO.findBySubjectId(subjectId).stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        public AbsenceDto createAbsence(Integer studentId, Integer subjectId, LocalDate date, boolean justified,
                        String reason) {
                Student student = studentDAO.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Student not found with ID: " + studentId));

                Subject subject = subjectDAO.findById(subjectId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Subject not found with ID: " + subjectId));

                Absence absence = new Absence();
                absence.setStudent(student);
                absence.setSubject(subject);
                absence.setDate(date);
                absence.setJustified(justified);
                absence.setReason(reason);

                absenceDAO.save(absence);
                logger.info("Created absence for student {} on {}", student.getFullName(), date);
                return toDto(absence);
        }

        public void updateAbsence(Integer id, Integer studentId, Integer subjectId, LocalDate date, boolean justified,
                        String reason) {
                Absence absence = absenceDAO.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Absence not found with ID: " + id));

                if (!absence.getStudent().getId().equals(studentId)) {
                        Student student = studentDAO.findById(studentId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Student not found with ID: " + studentId));
                        absence.setStudent(student);
                }

                if (!absence.getSubject().getId().equals(subjectId)) {
                        Subject subject = subjectDAO.findById(subjectId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Subject not found with ID: " + subjectId));
                        absence.setSubject(subject);
                }

                absence.setDate(date);
                absence.setJustified(justified);
                absence.setReason(reason);

                absenceDAO.update(absence);
                logger.info("Updated absence ID: {}", id);
        }

        public void deleteAbsence(Integer id) {
                Optional<Absence> absenceOpt = absenceDAO.findById(id);
                if (absenceOpt.isPresent()) {
                        absenceDAO.delete(id);
                        logger.info("Deleted absence ID: {}", id);
                }
        }

        private AbsenceDto toDto(Absence absence) {
                return new AbsenceDto(
                                absence.getId(),
                                absence.getStudent().getId(),
                                absence.getStudent().getFullName(),
                                absence.getSubject().getId(),
                                absence.getSubject().getName(),
                                absence.getDate(),
                                absence.getJustified(),
                                absence.getReason(),
                                absence.getRecordedBy() != null ? absence.getRecordedBy().getId() : null,
                                absence.getRecordedBy() != null ? absence.getRecordedBy().getUsername() : null,
                                absence.getCreatedAt());
        }
}
