package casp.web.backend.deprecated.dog;

import casp.web.backend.data.access.layer.dog.DogHasHandler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Mapper
public interface DogHasHandlerV2Mapper {
    DogHasHandlerV2Mapper DOG_HAS_HANDLER_V2_MAPPER = Mappers.getMapper(DogHasHandlerV2Mapper.class);

    @Mapping(target = "dog", ignore = true)
    @Mapping(target = "member", ignore = true)
    DogHasHandler toDogHasHandler(casp.web.backend.deprecated.dog.DogHasHandler dogHasHandler);
}
