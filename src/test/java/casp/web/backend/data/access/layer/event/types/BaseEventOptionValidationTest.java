package casp.web.backend.data.access.layer.event.types;


import casp.web.backend.data.access.layer.event.options.DailyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseEventOptionValidationTest {

    private static final BaseEventOptionValidation VALIDATION = new BaseEventOptionValidation();

    @ParameterizedTest
    @MethodSource
    void isValid(BaseEvent baseEvent) {
        assertTrue(VALIDATION.isValid(baseEvent, null));
    }

    static Stream<BaseEvent> isValid() {
        return Stream.of(createBaseEvent(null, null),
                createBaseEvent(new DailyEventOption(), null),
                createBaseEvent(null, new WeeklyEventOption()));
    }

    @Test
    void isInvalid() {
        assertFalse(VALIDATION.isValid(createBaseEvent(new DailyEventOption(), new WeeklyEventOption()), null));
    }

    private static BaseEvent createBaseEvent(final DailyEventOption d, final WeeklyEventOption w) {
        var baseEvent = new BaseEvent();
        baseEvent.setDailyOption(d);
        baseEvent.setWeeklyOption(w);
        return baseEvent;
    }
}
