package casp.web.backend.presentation.layer.configuration;

import casp.web.backend.data.access.layer.documents.commons.BaseEntity;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;


public interface BaseMapper<E extends BaseEntity, D> {
    D toDto(E document);

    @Mapping(target = "entityStatus", ignore = true)
    E toDocument(D dto);

    List<D> toDtoList(List<E> documentList);

    default Page<D> toDtoPage(Page<E> documentPage) {
        return documentPage.map(this::toDto);
    }
}
