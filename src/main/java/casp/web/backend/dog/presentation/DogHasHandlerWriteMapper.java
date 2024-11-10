package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.dog.DogHasHandlerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogHasHandlerWriteMapper extends BaseViewMapper<DogHasHandlerDto, DogHasHandlerWrite> {
    DogHasHandlerWriteMapper WRITE_MAPPER = Mappers.getMapper(DogHasHandlerWriteMapper.class);
}
