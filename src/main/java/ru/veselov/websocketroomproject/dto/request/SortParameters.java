package ru.veselov.websocketroomproject.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.websocketroomproject.annotation.OrderDirection;
import ru.veselov.websocketroomproject.annotation.SortBy;

@Data
@AllArgsConstructor
public class SortParameters {

    @PositiveOrZero(message = "Page number should be positive or zero")
    private Integer page;

    @SortBy
    private String sort;

    @OrderDirection
    private String order;

}
