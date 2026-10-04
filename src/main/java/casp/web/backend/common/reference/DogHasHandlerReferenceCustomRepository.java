package casp.web.backend.common.reference;

import java.util.Set;
import java.util.UUID;

public interface DogHasHandlerReferenceCustomRepository {
    /**
     * Find all {@link DogHasHandlerReference} by member id.
     * The {@link DogHasHandlerReference} must be active. This means that both the {@link MemberReference} and the {@link DogReference} must be active.
     *
     * @param memberId the member id
     * @return a set of {@link DogHasHandlerReference} from the respective {@link MemberReference}
     */
    Set<DogHasHandlerReference> findAllByMemberId(UUID memberId);

    /**
     * Find all {@link DogHasHandlerReference} by dog id.
     * The {@link DogHasHandlerReference} must be active. This means that both the {@link MemberReference} and the {@link DogReference} must be active.
     *
     * @param dogId the dog id
     * @return a set of {@link DogHasHandlerReference} from the respective {@link MemberReference}
     */
    Set<DogHasHandlerReference> findAllByDogId(UUID dogId);
}
