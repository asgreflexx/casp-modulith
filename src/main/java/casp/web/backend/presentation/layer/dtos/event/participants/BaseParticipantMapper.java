package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.commons.BaseDocument;
import org.mapstruct.Mapping;

import java.util.Set;


public interface BaseParticipantMapper<E extends BaseDocument, D extends BaseParticipantDto> {
    D toDto(E document);

    @Mapping(target = "id", ignore = true)
    E toDocument(D dto);

    Set<D> toDtoSet(Set<E> documentSet);

    Set<E> toDocumentSet(Set<D> dtoSet);
}
