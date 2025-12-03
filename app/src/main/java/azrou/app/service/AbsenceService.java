package azrou.app.service;

import azrou.app.model.dto.AbsenceDto;
import azrou.app.model.entity.Absence;
import azrou.app.model.entity.Student;
import azrou.app.model.entity.Subject;
import azrou.app.repo.AbsenceRepository;
import azrou.app.repo.StudentRepository;
import azrou.app.repo.SubjectRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsenceService {
        private static final Logger logger = LoggerFactory.getLogger(AbsenceService.class);
        private final AbsenceRepository absenceRepository;
        private final StudentRepository studentRepository;
        private final SubjectRepository subjectRepository;

        public AbsenceService(AbsenceRepository absenceRepository, StudentRepository studentRepository,
                        SubjectRepository subjectRepository) {
                this.absenceRepository = absenceRepository;
                this.studentRepository = studentRepository;
                this.subjectRepository = subjectRepository;
        }

        public List<AbsenceDto> getAbsencesByGroup(Integer groupId) {
                return absenceRepository.findByGroupId(groupId).stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        public AbsenceDto createAbsence(Integer studentId, Integer subjectId, LocalDate date, boolean justified,
                        String reason) {
                Student student = studentRepository.findById(studentId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Student not found with ID: " + studentId));

                Subject subject = subjectRepository.findById(subjectId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Subject not found with ID: " + subjectId));

                Absence absence = new Absence();
                absence.setStudent(student);
                absence.setSubject(subject);
                absence.setDate(date);
                absence.setJustified(justified);
                absence.setReason(reason);

                absenceRepository.save(absence);
                logger.info("Created absence for student {} on {}", student.getFullName(), date);
                return toDto(absence);
        }

        public void updateAbsence(Integer id, Integer studentId, Integer subjectId, LocalDate date, boolean justified,
                        String reason) {
                Absence absence = absenceRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Absence not found with ID: " + id));

                if (!absence.getStudent().getId().equals(studentId)) {
                        Student student = studentRepository.findById(studentId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Student not found with ID: " + studentId));
                        absence.setStudent(student);
                }

                if (!absence.getSubject().getId().equals(subjectId)) {
                        Subject subject = subjectRepository.findById(subjectId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Subject not found with ID: " + subjectId));
                        absence.setSubject(subject);
                }

                absence.setDate(date);
                absence.setJustified(justified);
                absence.setReason(reason);

                absenceRepository.update(absence);
                logger.info("Updated absence ID: {}", id);
        }

        public void deleteAbsence(Integer id) {
                Optional<Absence> absenceOpt = absenceRepository.findById(id);
                if (absenceOpt.isPresent()) {
                        absenceRepository.delete(absenceOpt.get());
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
