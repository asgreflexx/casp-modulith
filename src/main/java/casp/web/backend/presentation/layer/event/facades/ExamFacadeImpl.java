package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantMapper.EXAM_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.ExamMapper.EXAM_MAPPER;

@Service
class ExamFacadeImpl implements ExamFacade {
    private static final Logger LOG = LoggerFactory.getLogger(ExamFacadeImpl.class);
    private final ExamParticipantService examParticipantService;

    @Autowired
    ExamFacadeImpl(final ExamParticipantService examParticipantService) {
        this.examParticipantService = examParticipantService;
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

    private void setExamParticipants(final ExamDto examDto) {
        var examParticipantDtoSet = examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(examDto.getId())
                .stream()
                .map(EXAM_PARTICIPANT_MAPPER::toDto)
                .collect(Collectors.toSet());

        examDto.setParticipants(examParticipantDtoSet);
    }
}
