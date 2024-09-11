package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamParticipantMapper extends BaseMapper<ExamParticipant, ExamParticipantDto> {
    ExamParticipantMapper EXAM_PARTICIPANT_MAPPER = Mappers.getMapper(ExamParticipantMapper.class);
}
