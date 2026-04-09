package casp.web.backend.dog;

import casp.web.backend.common.base.BaseMapper;
import casp.web.backend.dog.data.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogMapper extends BaseMapper<Dog, DogDto> {
    DogMapper DOG_MAPPER = Mappers.getMapper(DogMapper.class);
}
