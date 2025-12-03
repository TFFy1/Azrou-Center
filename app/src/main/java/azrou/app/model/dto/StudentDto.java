package azrou.app.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentDto(
        Integer id,
        Integer groupId,
        String groupName,
        String fullName,
        String cin,
        String qualifications,
        LocalDate dateOfBirth,
        String phone,
        String photoPath,
        LocalDateTime createdAt) {
}
