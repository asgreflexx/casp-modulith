package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.presentation.layer.dtos.events.CourseDto;

public interface CourseService extends BaseEventService<Course, Space, CourseDto> {

}
