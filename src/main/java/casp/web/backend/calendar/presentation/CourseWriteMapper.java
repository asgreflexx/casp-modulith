package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.CourseDto;
import casp.web.backend.calendar.SpaceDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseWriteMapper extends BaseViewMapper<CourseDto, CourseWrite> {
    CourseWriteMapper COURSE_WRITE_MAPPER = Mappers.getMapper(CourseWriteMapper.class);

    SpaceDto toSpaceDto(SpaceWrite spaceWriteWrite);
}
