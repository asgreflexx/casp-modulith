package casp.web.backend.business.logic.layer.events.dtos;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.mappers.ExamMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ExamDtoTest {

    private ExamDto examDto;

    @BeforeEach
    void setUp() {
        var exam = TestFixture.createValidExam();
        examDto = new ExamMapperImpl().documentToDto(exam);
        examDto.setCalendarEntries(List.of(TestFixture.createValidCalendarEntry(exam)));
    }

    @Test
    void happyPath() {
        assertThat(TestFixture.getViolations(examDto)).isEmpty();
    }

    @Test
    void participantsIsNull() {
        examDto.setParticipants(null);

        assertThat(TestFixture.getViolations(examDto)).hasSize(1);
    }
}
