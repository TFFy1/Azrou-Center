package azrou.app.service;

import azrou.app.dao.AssessmentDAO;
import azrou.app.dao.SubjectDAO;
import azrou.app.model.dto.AssessmentDto;
import azrou.app.model.entity.Assessment;
import azrou.app.model.entity.Subject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentService {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);
    private final AssessmentDAO assessmentDAO;
    private final SubjectDAO subjectDAO;

    public AssessmentService() {
        this.assessmentDAO = new AssessmentDAO();
        this.subjectDAO = new SubjectDAO();
    }

    public List<AssessmentDto> getAssessmentsBySubject(Integer subjectId) {
        return assessmentDAO.findBySubjectId(subjectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AssessmentDto> getAllAssessments() {
        return assessmentDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AssessmentDto createAssessment(Integer subjectId, String name, LocalDate date, Double maxScore,
            Double weight) {
        Subject subject = subjectDAO.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));

        Assessment assessment = new Assessment();
        assessment.setSubject(subject);
        assessment.setName(name);
        assessment.setDate(date);
        assessment.setMaxScore(maxScore);
        assessment.setWeight(weight);

        assessmentDAO.save(assessment);
        logger.info("Created assessment: {} for subject {}", name, subject.getName());
        return toDto(assessment);
    }

    public void updateAssessment(Integer id, Integer subjectId, String name, LocalDate date, Double maxScore,
            Double weight) {
        Assessment assessment = assessmentDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with ID: " + id));

        if (!assessment.getSubject().getId().equals(subjectId)) {
            Subject subject = subjectDAO.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));
            assessment.setSubject(subject);
        }

        assessment.setName(name);
        assessment.setDate(date);
        assessment.setMaxScore(maxScore);
        assessment.setWeight(weight);

        assessmentDAO.update(assessment);
        logger.info("Updated assessment: {}", name);
    }

    public void deleteAssessment(Integer id) {
        Optional<Assessment> assessmentOpt = assessmentDAO.findById(id);
        if (assessmentOpt.isPresent()) {
            assessmentDAO.delete(id);
            logger.info("Deleted assessment with ID: {}", id);
        }
    }

    private AssessmentDto toDto(Assessment assessment) {
        return new AssessmentDto(
                assessment.getId(),
                assessment.getSubject().getId(),
                assessment.getSubject().getName(),
                assessment.getName(),
                assessment.getDate(),
                assessment.getMaxScore(),
                assessment.getWeight(),
                assessment.getCreatedAt());
    }
}
