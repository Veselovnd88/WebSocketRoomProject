package ru.veselov.websocketroomproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.annotation.OrderDirection;
import ru.veselov.websocketroomproject.annotation.SortBy;

@Data
@AllArgsConstructor

public class SortParameters {

    @Schema(name = "page")
    @PositiveOrZero(message = "Page number should be positive or zero")
    private Integer page;


    @Schema(name = "sort")
    @SortBy
    private String sort;

    @Schema(name = "order")
    @OrderDirection
    private String order;

}
