package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.participants.Space;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface CourseMapper extends BaseEventMapper<Course, CourseDto> {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);

    SpaceDto toSpaceDto(Space space);

    Space toSpace(SpaceDto spaceDto);

    Set<Space> toSpaces(Set<SpaceDto> spaceDtos);
}
