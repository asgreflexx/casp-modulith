package casp.web.backend.dog;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;


public interface DogHasHandlerService {

    /**
     * It sets the member and dog to the dogHasHandler, before saving it.
     *
     * @param dogHasHandler instance of DogHasHandler
     * @return saved instance of DogHasHandler
     */
    DogHasHandlerDto saveDogHasHandler(DogHasHandlerDto dogHasHandler);

    /**
     * Search for an active DogHasHandler by its id.
     *
     * @param id of the DogHasHandler
     * @return an instance of DogHasHandler with the given id, or throws an exception if not found
     */
    DogHasHandlerDto getDogHasHandlerById(UUID id);

    void deleteDogHasHandlersByMemberId(UUID memberId);

    void deleteDogHasHandlersByDogId(UUID dogId);

    void deleteDogHasHandlerById(UUID id);

    /**
     * Search for an active DogHasHandler by name in members and dogs.
     *
     * @param name of the DogHasHandler
     * @return a set of DogHasHandler with the given name, or an empty set if not found
     */
    Page<DogHasHandlerDto> searchByName(@Nullable String name, Pageable pageable);

    Page<DogHasHandlerDto> getAllDogHasHandlers(Pageable pageable);

    Set<DogHasHandlerDto> getDogHasHandlersByIds(Set<UUID> ids);

    Set<String> getEmailsByDogHasHandlersIds(Set<UUID> ids);

    void deactivateDogHasHandlersByMemberId(UUID memberId);

    void activateDogHasHandlersByMemberId(UUID memberId);

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();
}
