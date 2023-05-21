package cinema.lib;

import java.lang.reflect.Field;
import javax.validation.ConstraintValidatorContext;
import cinema.dto.request.UserRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FieldsValueMatchValidatorTest {
    private FieldsValueMatchValidator matchValidator;
    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        matchValidator = new FieldsValueMatchValidator();
        Field field = matchValidator.getClass().getDeclaredField("field");
        Field fieldMatch = matchValidator.getClass().getDeclaredField("fieldMatch");
        field.setAccessible(true);
        field.set(matchValidator, "password");
        fieldMatch.setAccessible(true);
        fieldMatch.set(matchValidator, "repeatPassword");
    }

    @Test
    void isValid_validValue_ok() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("bob@gmail.com");
        userRequestDto.setPassword("12345678");
        userRequestDto.setRepeatPassword("12345678");

        boolean actual = matchValidator.isValid(userRequestDto, context);
        Assertions.assertTrue(actual, "Method should return true for password "
                + userRequestDto.getPassword());
    }

    @Test
    void isValid_nullValue_notOk() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("bob@gmail.com");
        userRequestDto.setPassword(null);
        userRequestDto.setRepeatPassword("12345678");

        boolean actual = matchValidator.isValid(userRequestDto, context);
        Assertions.assertFalse(actual, "Method should return false for null value password");
    }

    @Test
    void isValid_passwordsDontMatch_notOk() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("bob@gmail.com");
        userRequestDto.setPassword("12345677");
        userRequestDto.setRepeatPassword("12345678");

        boolean actual = matchValidator.isValid(userRequestDto, context);
        Assertions.assertFalse(actual,
                "Method should return false when password and repeatPassword don't match");
    }

}