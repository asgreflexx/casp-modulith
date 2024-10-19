package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.base.BaseDtoMapper;
import casp.web.backend.data.access.layer.event.types.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper extends BaseDtoMapper<Course, CourseDto> {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);
}
