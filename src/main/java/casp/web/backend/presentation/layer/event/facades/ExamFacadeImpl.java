package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.business.logic.layer.event.types.ExamService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantMapper.EXAM_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.ExamMapper.EXAM_MAPPER;

@Service
class ExamFacadeImpl implements ExamFacade {
    private static final Logger LOG = LoggerFactory.getLogger(ExamFacadeImpl.class);
    private final ExamParticipantService examParticipantService;
    private final CalendarService calendarService;
    private final ExamService examService;

    @Autowired
    ExamFacadeImpl(final ExamParticipantService examParticipantService,
                   final CalendarService calendarService,
                   final ExamService examService) {
        this.examParticipantService = examParticipantService;
        this.calendarService = calendarService;
        this.examService = examService;
    }

    @Override
    public ExamDto mapDocumentToDto(final BaseEvent baseEvent) {
        if (baseEvent instanceof Exam exam) {
            var examDto = EXAM_MAPPER.toDto(exam);
            setExamParticipants(examDto);
            return examDto;
        } else {
            var msg = "The parameter %s is not an exam".formatted(baseEvent.getEventType());
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void save(final ExamDto examDto) {
        var exam = EXAM_MAPPER.toDocument(examDto);

        calendarService.replaceCalendarEntries(exam, CALENDAR_MAPPER.toDocumentList(examDto.getCalendarEntries()));
        examParticipantService.replaceParticipants(exam, examDto.getParticipantsIdToWrite());

        examService.save(exam);
    }

    @Override
    public ExamDto getOneById(final UUID id) {
        var exam = examService.getOneById(id);
        return mapDocumentToDto(exam);
    }

    @Override
    public void deleteById(final UUID id) {
        examService.deleteById(id);
    }

    @Override
    public Page<ExamDto> getAllByYear(final int year, final Pageable pageable) {
        var examPage = examService.getAllByYear(year, pageable);
        return EXAM_MAPPER.toDtoPage(examPage);
    }

    private void setExamParticipants(final ExamDto examDto) {
        var examParticipantDtoSet = examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(examDto.getId())
                .stream()
                .map(EXAM_PARTICIPANT_MAPPER::toDto)
                .collect(Collectors.toSet());

        examDto.setParticipantsToRead(examParticipantDtoSet);
    }
}
