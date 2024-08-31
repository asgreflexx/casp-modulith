package casp.web.backend.business.logic.layer.events.mappers;

public interface BaseEventMapper<E, D> {
    D documentToDto(E document);

    E dtoToDocument(D dto);
}
