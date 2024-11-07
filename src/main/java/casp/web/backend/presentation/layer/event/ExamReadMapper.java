package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.ExamDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamReadMapper extends BaseViewMapper<ExamDto, ExamRead> {
    ExamReadMapper EXAM_READ_MAPPER = Mappers.getMapper(ExamReadMapper.class);
}
