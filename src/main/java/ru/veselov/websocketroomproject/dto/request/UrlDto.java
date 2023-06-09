package ru.veselov.websocketroomproject.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @NotEmpty(message = "Url cannot be empty")
    @URL
    private String url;

}
