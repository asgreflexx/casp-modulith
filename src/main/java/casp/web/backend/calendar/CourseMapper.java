package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.participants.Space;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper extends BaseEventMapper<Course, CourseDto> {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);

    SpaceDto toSpaceDto(Space space);

    Space toSpace(SpaceDto spaceDto);

    default SpaceDto toSpaceDto(Space space, Course course) {
        var spaceDto = toSpaceDto(space);
        spaceDto.setCourseId(course.getId());
        spaceDto.setCourseName(course.getName());
        return spaceDto;
    }
}
