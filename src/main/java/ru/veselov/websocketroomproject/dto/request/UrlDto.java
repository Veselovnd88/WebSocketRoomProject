package ru.veselov.websocketroomproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @Schema(description = "Current URL that already playing in room", example = "https://youtube.com")
    @NotEmpty(message = "Url cannot be empty")
    @URL(message = "Should be URL format")
    private String url;

}
