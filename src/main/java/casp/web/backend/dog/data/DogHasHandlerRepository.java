package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
public interface DogHasHandlerRepository extends BaseRepository<DogHasHandler>, DogHasHandlerCustomRepository {

    Page<DogHasHandler> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Set<DogHasHandler> findAllByIdInAndEntityStatus(Set<UUID> ids, EntityStatus entityStatus);
}
