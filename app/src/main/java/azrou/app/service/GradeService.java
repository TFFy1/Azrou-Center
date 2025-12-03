package azrou.app.service;

import azrou.app.dao.AssessmentDAO;
import azrou.app.dao.GradeDAO;
import azrou.app.dao.StudentDAO;
import azrou.app.model.dto.GradeDto;
import azrou.app.model.entity.Assessment;
import azrou.app.model.entity.Grade;
import azrou.app.model.entity.Student;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradeService {
    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final AssessmentDAO assessmentDAO;

    public GradeService() {
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.assessmentDAO = new AssessmentDAO();
    }

    public List<GradeDto> getGradesByAssessment(Integer assessmentId) {
        return gradeDAO.findByAssessmentId(assessmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void saveGrade(Integer studentId, Integer assessmentId, Double score) {
        Assessment assessment = assessmentDAO.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with ID: " + assessmentId));

        if (score < 0 || score > assessment.getMaxScore()) {
            throw new IllegalArgumentException("Score must be between 0 and " + assessment.getMaxScore());
        }

        Optional<Grade> existingGrade = gradeDAO.findByStudentAndAssessment(studentId, assessmentId);

        Grade grade;
        if (existingGrade.isPresent()) {
            grade = existingGrade.get();
            grade.setScore(score);
        } else {
            Student student = studentDAO.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

            grade = new Grade();
            grade.setStudent(student);
            grade.setAssessment(assessment);
            grade.setScore(score);
        }

        if (grade.getId() != null) {
            gradeDAO.update(grade);
        } else {
            gradeDAO.save(grade);
        }
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
