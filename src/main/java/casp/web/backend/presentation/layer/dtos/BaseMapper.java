package casp.web.backend.presentation.layer.dtos;

import casp.web.backend.data.access.layer.documents.commons.BaseDocument;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;


public interface BaseMapper<E extends BaseDocument, D> {
    D toDto(E document);

    @Mapping(target = "entityStatus", ignore = true)
    E toDocument(D dto);

    List<D> toDtoList(List<E> documentList);

    default Page<D> toDtoPage(Page<E> documentPage) {
        return documentPage.map(this::toDto);
    }

    Set<D> toDtoSet(Set<E> documentSet);
}
