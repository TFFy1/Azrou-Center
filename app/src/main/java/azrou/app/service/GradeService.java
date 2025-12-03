package azrou.app.service;

import azrou.app.model.dto.GradeDto;
import azrou.app.model.entity.Assessment;
import azrou.app.model.entity.Grade;
import azrou.app.model.entity.Student;
import azrou.app.repo.AssessmentRepository;
import azrou.app.repo.GradeRepository;
import azrou.app.repo.StudentRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradeService {
    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final AssessmentRepository assessmentRepository;

    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository,
            AssessmentRepository assessmentRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public List<GradeDto> getGradesByAssessment(Integer assessmentId) {
        return gradeRepository.findByAssessmentId(assessmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void saveGrade(Integer studentId, Integer assessmentId, Double score) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with ID: " + assessmentId));

        if (score < 0 || score > assessment.getMaxScore()) {
            throw new IllegalArgumentException("Score must be between 0 and " + assessment.getMaxScore());
        }

        Optional<Grade> existingGrade = gradeRepository.findByStudentAndAssessment(studentId, assessmentId);

        Grade grade;
        if (existingGrade.isPresent()) {
            grade = existingGrade.get();
            grade.setScore(score);
        } else {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

            grade = new Grade();
            grade.setStudent(student);
            grade.setAssessment(assessment);
            grade.setScore(score);
        }

        gradeRepository.saveOrUpdate(grade);
        logger.info("Saved grade for student {} on assessment {}", studentId, assessmentId);
    }

    private GradeDto toDto(Grade grade) {
        return new GradeDto(
                grade.getId(),
                grade.getStudent().getId(),
                grade.getStudent().getFullName(),
                grade.getAssessment().getId(),
                grade.getAssessment().getName(),
                grade.getScore(),
                grade.getRecordedAt(),
                grade.getCreatedAt());
    }
}
