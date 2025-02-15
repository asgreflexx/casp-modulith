package casp.web.backend.dog;

import casp.web.backend.common.base.BaseMapper;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.dog.data.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface DogMapper extends BaseMapper<Dog, DogDto> {
    DogMapper DOG_MAPPER = Mappers.getMapper(DogMapper.class);

    @Mapping(target = "memberId", source = "dogHasHandler.member.id")
    @Mapping(target = "firstName", source = "dogHasHandler.member.firstName")
    @Mapping(target = "lastName", source = "dogHasHandler.member.lastName")
    DogHasHandler toDogHasHandler(DogHasHandlerReference dogHasHandler);

    Set<DogHasHandler> toDogHasHandlerSet(Set<DogHasHandlerReference> dogHasHandlerSet);
}
