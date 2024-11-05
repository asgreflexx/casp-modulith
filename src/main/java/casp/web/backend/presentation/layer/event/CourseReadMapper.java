package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.CourseDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseReadMapper extends BaseViewMapper<CourseDto, CourseRead> {
    CourseReadMapper COURSE_READ_MAPPER = Mappers.getMapper(CourseReadMapper.class);
}
