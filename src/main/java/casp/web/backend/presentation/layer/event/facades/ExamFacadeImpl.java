package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.ExamParticipantMapper.EXAM_PARTICIPANT_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.ExamMapper.EXAM_MAPPER;

@Service
class ExamFacadeImpl implements ExamFacade {
    private final ExamParticipantService examParticipantService;

    @Autowired
    ExamFacadeImpl(final ExamParticipantService examParticipantService) {
        this.examParticipantService = examParticipantService;
    }

    @Override
    public ExamDto mapBaseEventToDto(final BaseEvent baseEvent) {
        var examDto = EXAM_MAPPER.toDto((Exam) baseEvent);
        setExamParticipants(examDto);
        return examDto;
    }

    private void setExamParticipants(final ExamDto examDto) {
        var examParticipantDtoSet = examParticipantService.getActiveExamParticipantsIfDogHasHandlersAreActive(examDto.getId())
                .stream()
                .map(pd -> {
                    var examParticipantDto = EXAM_PARTICIPANT_MAPPER.toDto((ExamParticipant) pd.participant());
                    examParticipantDto.setDogHasHandler(DOG_HAS_HANDLER_MAPPER.toDto(pd.dogHasHandler()));
                    examParticipantDto.setBaseEvent(null);
                    return examParticipantDto;
                })
                .collect(Collectors.toSet());

        examDto.setParticipants(examParticipantDtoSet);
    }
}
