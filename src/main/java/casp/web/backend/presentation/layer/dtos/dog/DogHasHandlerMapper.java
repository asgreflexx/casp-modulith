package casp.web.backend.presentation.layer.dtos.dog;

import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DogHasHandlerMapper extends BaseMapper<DogHasHandler, DogHasHandlerDto> {
    DogHasHandlerMapper DOG_HAS_HANDLER_MAPPER = Mappers.getMapper(DogHasHandlerMapper.class);
}