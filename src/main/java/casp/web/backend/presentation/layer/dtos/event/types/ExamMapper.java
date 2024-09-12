package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamMapper extends BaseMapper<Exam, ExamDto> {
    ExamMapper EXAM_MAPPER = Mappers.getMapper(ExamMapper.class);
}
