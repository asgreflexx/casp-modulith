package casp.web.backend.business.logic.layer.events.mappers;

import casp.web.backend.business.logic.layer.events.dtos.ExamDto;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import org.mapstruct.Mapper;

@Mapper
public interface ExamMapper extends BaseEventMapper<Exam, ExamDto> {
}
