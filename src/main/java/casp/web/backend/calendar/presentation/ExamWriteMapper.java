package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamWriteMapper extends BaseViewMapper<ExamDto, ExamWrite> {
    ExamWriteMapper EXAM_WRITE_MAPPER = Mappers.getMapper(ExamWriteMapper.class);
}
