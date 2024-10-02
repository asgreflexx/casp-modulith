package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamParticipantMapper extends BaseParticipantMapper<ExamParticipant, ExamParticipantDto> {
    ExamParticipantMapper EXAM_PARTICIPANT_MAPPER = Mappers.getMapper(ExamParticipantMapper.class);
}
