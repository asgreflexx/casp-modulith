package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamDto;
import casp.web.backend.calendar.ExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static casp.web.backend.calendar.presentation.ExamReadMapper.EXAM_READ_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ExamRestControllerTest {
    @Mock
    private ExamService examService;

    @InjectMocks
    private ExamRestController examRestController;

    private ExamDto examDto;

    @BeforeEach
    void setUp() {
        examDto = new ExamDto();
        examDto.setName("Test Exam");
    }

    @Test
    void save() {
        var examWrite = new ExamWrite();
        examWrite.setName("exam");

        var response = examRestController.save(examWrite);

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(examService).save(argThat(actualExamDto -> examWrite.getName().equals(actualExamDto.getName())));
    }

    @Test
    void deleteById() {
        var response = examRestController.deleteById(examDto.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(examService).deleteById(examDto.getId());
    }

    @Test
    void getCalendarEntry() {
        var calendarEntryId = UUID.randomUUID();
        when(examService.getOneByIdAndCalendarEntryId(examDto.getId(), calendarEntryId)).thenReturn(examDto);

        var response = examRestController.getCalendarEntry(examDto.getId(), calendarEntryId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertEquals(EXAM_READ_MAPPER.toTarget(examDto), response.getBody());
    }

    @Test
    void getExamsByDogHasHandlerId() {
        var dogHasHandlerId = UUID.randomUUID();
        when(examService.getExamsByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(examDto)));

        var response = examRestController.getExamsByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(EXAM_READ_MAPPER.toTarget(examDto));
    }

    @Test
    void migrateDataToV2() {
        var response = examRestController.migrateDataToV2();

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(examService).migrateDataToV2();
    }
}
