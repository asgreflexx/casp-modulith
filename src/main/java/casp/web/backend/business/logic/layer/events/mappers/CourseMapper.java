package casp.web.backend.business.logic.layer.events.mappers;

import casp.web.backend.business.logic.layer.events.dtos.CourseDto;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import org.mapstruct.Mapper;

@Mapper
public interface CourseMapper extends BaseEventMapper<Course, CourseDto> {
}
