package casp.web.backend.presentation.layer.dtos;

import casp.web.backend.data.access.layer.commons.BaseDocument;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;


public interface BaseMapper<E extends BaseDocument, D> {
    D toDto(E document);

    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "id", conditionExpression = "java(null != dto.getId())")
    @Mapping(target = "version", conditionExpression = "java(0 < dto.getVersion())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "created", conditionExpression = "java(null != dto.getCreated())")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modified", conditionExpression = "java(null != dto.getModified())")
    E toDocument(D dto);

    List<D> toDtoList(List<E> documentList);

    default Page<D> toDtoPage(Page<E> documentPage) {
        return documentPage.map(this::toDto);
    }

    Set<D> toDtoSet(Set<E> documentSet);
}
