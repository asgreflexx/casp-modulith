package casp.web.backend.common.base;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends BaseDocument> extends MongoRepository<T, UUID> {
    Optional<T> findOneByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
