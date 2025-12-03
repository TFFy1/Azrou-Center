package azrou.app.model.dto;

import java.time.LocalDateTime;

public record SubjectDto(
        Integer id,
        Integer groupId,
        String groupName,
        String name,
        String code,
        LocalDateTime createdAt) {
}
