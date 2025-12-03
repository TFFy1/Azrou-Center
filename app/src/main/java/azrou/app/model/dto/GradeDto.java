package azrou.app.model.dto;

import java.time.LocalDateTime;

public record GradeDto(
        Integer id,
        Integer studentId,
        String studentName,
        Integer assessmentId,
        String assessmentName,
        Double score,
        LocalDateTime recordedAt,
        LocalDateTime createdAt) {
}
