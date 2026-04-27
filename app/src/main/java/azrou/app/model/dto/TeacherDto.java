package azrou.app.model.dto;

import java.time.LocalDateTime;

public record TeacherDto(
        Integer id,
        String fullName,
        String email,
        String phone,
        LocalDateTime createdAt) {
}
