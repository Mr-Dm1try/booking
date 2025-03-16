package inc.pomoika.booking.common.validation;

import inc.pomoika.booking.common.model.dto.DateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(DateRange value, ConstraintValidatorContext context) {
        return value != null && value.isValid();
    }
} 