package casp.web.backend.dog.data;


import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByDogIdAndNotDeleted(UUID dogId);

    Set<DogHasHandler> findAllByMemberIdAndNotDeleted(UUID memberId);

    Set<DogHasHandler> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByDogIdAndEntityStatus(UUID dogId, EntityStatus entityStatus);

    Page<DogHasHandler> findAllByValue(@Nullable String value, Pageable pageable);

    Optional<DogHasHandler> findByDogIdAndMemberId(UUID dogId, UUID memberId);
}
