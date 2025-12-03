package azrou.app.model.dto;

import java.time.LocalDateTime;

public record GroupDto(
        Integer id,
        String name,
        String description,
        Integer capacity,
        LocalDateTime createdAt) {
}
