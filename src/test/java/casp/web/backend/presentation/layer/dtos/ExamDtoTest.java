package casp.web.backend.presentation.layer.dtos;


import casp.web.backend.TestFixture;
import casp.web.backend.presentation.layer.dtos.events.types.ExamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static casp.web.backend.presentation.layer.dtos.events.types.ExamMapper.EXAM_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;


class ExamDtoTest {

    private ExamDto examDto;

    @BeforeEach
    void setUp() {
        var exam = TestFixture.createValidExam();
        examDto = EXAM_MAPPER.toDto(exam);
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
