package azrou.app.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssessmentDto(
                Integer id,
                Integer subjectId,
                String subjectName,
                String name,
                LocalDate date,
                Double maxScore,
                Double weight,
                LocalDateTime createdAt) {
}
