package casp.web.backend.presentation.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DogMapper {

    DogDto toDto(Dog document);

    @Mapping(target = "entityStatus", ignore = true)
    Dog toDocument(DogDto dto);

    List<DogDto> toDtoList(List<Dog> documentList);

    default Page<DogDto> toDtoPage(Page<Dog> documentPage) {
        return documentPage.map(this::toDto);
    }
}
