package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.dog.DogDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogReadMapper extends BaseViewMapper<DogDto, DogRead> {
    DogReadMapper READ_MAPPER = Mappers.getMapper(DogReadMapper.class);
}
