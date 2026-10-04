package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamMapper extends BaseEventMapper<Exam, ExamDto> {
    ExamMapper EXAM_MAPPER = Mappers.getMapper(ExamMapper.class);
}
