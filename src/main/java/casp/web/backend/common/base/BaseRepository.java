package casp.web.backend.common.base;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends BaseDocument> extends MongoRepository<T, UUID> {
    private static <T extends BaseDocument> void setCreatedAndCreatedBy(final T source, final T target) {
        if (EntityStatus.ACTIVE != source.getEntityStatus()) {
            throw new IllegalStateException("The %s with id %s is not active".formatted(target.getClass().getSimpleName(),
                    target.getId()));
        }
        target.setCreated(source.getCreated());
        target.setCreatedBy(source.getCreatedBy());
    }

    Optional<T> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    /**
     * Set the created and created by values if:
     * <ul>
     *     <li>the instance exists</li>
     *     <li>and it is active</li>
     * </ul>
     * <p>
     * If the instance is new, it will be saved.<br>
     * If the instance exists but it is not active, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * @param document an instance of type {@link T}
     * @return the saved instance of type {@link T}
     */
    default T setMetadataAndSave(T document) {
        this.findById(document.getId())
                .ifPresent(t -> setCreatedAndCreatedBy(t, document));
        return this.save(document);
    }
}
