package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.dog.DogHasHandlerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerReadMapper extends BaseViewMapper<DogHasHandlerDto, DogHasHandlerRead> {
    DogHasHandlerReadMapper READ_MAPPER = Mappers.getMapper(DogHasHandlerReadMapper.class);
}
