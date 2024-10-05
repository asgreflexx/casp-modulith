package casp.web.backend.presentation.layer.dog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GetByOwnerCustomValidatorTest {

    @InjectMocks
    private GetByOwnerCustomValidator validator;

    @Test
    void valuesIsNull() {
        assertThat(validator.isValid(null, null)).isFalse();
    }

    @Test
    void valuesHasOnlyOneValue() {
        assertThat(validator.isValid(new String[1], null)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void valuesAreBlank(String value) {
        var values = new String[]{value, value, value};

        assertThat(validator.isValid(values, null)).isFalse();
    }

    @Test
    void nameIsNotBlank() {
        var values = new String[]{null, "XXX", null};

        assertThat(validator.isValid(values, null)).isFalse();
    }

    @Test
    void ownerNameIsNotBlank() {
        var values = new String[]{null, null, "XXX"};

        assertThat(validator.isValid(values, null)).isFalse();
    }

    @Test
    void nameAndOwnerNameAreNotBlank() {
        var values = new String[]{null, "XXX", "XXX"};

        assertThat(validator.isValid(values, null)).isTrue();
    }

    @Test
    void chipNumberIsNotBlank() {
        var values = new String[]{"XXXX", null, null};

        assertThat(validator.isValid(values, null)).isTrue();
    }
}
