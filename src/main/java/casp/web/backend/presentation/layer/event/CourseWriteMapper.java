package casp.web.backend.presentation.layer.event;

import casp.web.backend.business.logic.layer.event.types.CourseDto;
import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import casp.web.backend.common.base.BaseViewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseWriteMapper extends BaseViewMapper<CourseDto, CourseWrite> {
    CourseWriteMapper COURSE_WRITE_MAPPER = Mappers.getMapper(CourseWriteMapper.class);

    SpaceDto toSpaceDto(SpaceWrite spaceWriteWrite);
}
