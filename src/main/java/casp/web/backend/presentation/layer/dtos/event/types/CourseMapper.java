package casp.web.backend.presentation.layer.dtos.event.types;

import casp.web.backend.data.access.layer.event.types.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper extends BaseEventMapper<Course, CourseDto> {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);
}
