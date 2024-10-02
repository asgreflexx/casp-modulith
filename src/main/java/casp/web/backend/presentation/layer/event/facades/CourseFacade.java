package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;

import java.util.Set;
import java.util.UUID;

public interface CourseFacade extends BaseEventFacade<CourseDto> {
    Set<String> getSpacesEmail(UUID id);
}
