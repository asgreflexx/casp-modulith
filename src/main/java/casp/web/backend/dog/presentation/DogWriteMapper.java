package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.dog.DogDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogWriteMapper extends BaseViewMapper<DogDto, DogWrite> {
    DogWriteMapper WRITE_MAPPER = Mappers.getMapper(DogWriteMapper.class);
}
