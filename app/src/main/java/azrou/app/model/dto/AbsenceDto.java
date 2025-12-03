package azrou.app.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AbsenceDto(
        Integer id,
        Integer studentId,
        String studentName,
        Integer subjectId,
        String subjectName,
        LocalDate date,
        Boolean justified,
        String reason,
        Integer recordedById,
        String recordedByName,
        LocalDateTime createdAt) {
}
