package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
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
