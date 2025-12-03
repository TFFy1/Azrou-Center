package azrou.app.model.dto;

import java.time.LocalDateTime;

public record AdminDto(
        Integer id,
        String username,
        String fullName,
        LocalDateTime createdAt) {
}
