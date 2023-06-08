package ru.veselov.websocketroomproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.websocketroomproject.annotations.SupportedPlayer;
import ru.veselov.websocketroomproject.entity.PlayerType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerTypeValidator implements ConstraintValidator<SupportedPlayer, Object> {

    private List<String> supportedPlayerNames;

    @Override
    public void initialize(SupportedPlayer constraintAnnotation) {
        supportedPlayerNames = Stream.of(constraintAnnotation.playerType().getEnumConstants())
                .map(PlayerType::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        String playerType = (String) value;
        return supportedPlayerNames.contains(playerType);
    }
}
