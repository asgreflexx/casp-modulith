package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.ExamDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamReadMapper extends BaseViewMapper<ExamDto, ExamRead> {
    ExamReadMapper EXAM_READ_MAPPER = Mappers.getMapper(ExamReadMapper.class);
}
