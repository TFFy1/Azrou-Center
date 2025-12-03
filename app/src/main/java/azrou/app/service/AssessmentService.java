package azrou.app.service;

import azrou.app.model.dto.AssessmentDto;
import azrou.app.model.entity.Assessment;
import azrou.app.model.entity.Subject;
import azrou.app.repo.AssessmentRepository;
import azrou.app.repo.SubjectRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentService {
    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);
    private final AssessmentRepository assessmentRepository;
    private final SubjectRepository subjectRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, SubjectRepository subjectRepository) {
        this.assessmentRepository = assessmentRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<AssessmentDto> getAssessmentsBySubject(Integer subjectId) {
        return assessmentRepository.findBySubjectId(subjectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AssessmentDto> getAllAssessments() {
        return assessmentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AssessmentDto createAssessment(Integer subjectId, String name, LocalDate date, Double maxScore,
            Double weight) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));

        Assessment assessment = new Assessment();
        assessment.setSubject(subject);
        assessment.setName(name);
        assessment.setDate(date);
        assessment.setMaxScore(maxScore);
        assessment.setWeight(weight);

        assessmentRepository.save(assessment);
        logger.info("Created assessment: {} for subject {}", name, subject.getName());
        return toDto(assessment);
    }

    public void updateAssessment(Integer id, Integer subjectId, String name, LocalDate date, Double maxScore,
            Double weight) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with ID: " + id));

        if (!assessment.getSubject().getId().equals(subjectId)) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));
            assessment.setSubject(subject);
        }

        assessment.setName(name);
        assessment.setDate(date);
        assessment.setMaxScore(maxScore);
        assessment.setWeight(weight);

        assessmentRepository.update(assessment);
        logger.info("Updated assessment: {}", name);
    }

    public void deleteAssessment(Integer id) {
        Optional<Assessment> assessmentOpt = assessmentRepository.findById(id);
        if (assessmentOpt.isPresent()) {
            assessmentRepository.delete(assessmentOpt.get());
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
