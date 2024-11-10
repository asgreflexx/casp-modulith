package casp.web.backend.dog;

import casp.web.backend.common.base.BaseDtoMapper;
import casp.web.backend.dog.data.DogHasHandler;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerMapper extends BaseDtoMapper<DogHasHandler, DogHasHandlerDto> {
    DogHasHandlerMapper DOG_HAS_HANDLER_MAPPER = Mappers.getMapper(DogHasHandlerMapper.class);
}
