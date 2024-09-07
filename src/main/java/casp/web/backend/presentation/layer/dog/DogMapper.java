package casp.web.backend.presentation.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.presentation.layer.configuration.BaseMapper;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface DogMapper extends BaseMapper<Dog, DogDto> {

}
