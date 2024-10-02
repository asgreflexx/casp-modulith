package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.member.Member;
import jakarta.annotation.Nullable;

import java.util.Set;
import java.util.UUID;


public interface DogHasHandlerService {

    /**
     * It sets the member and dog to the dogHasHandler, before saving it.
     *
     * @param dogHasHandler instance of DogHasHandler
     * @return saved instance of DogHasHandler
     */
    DogHasHandler saveDogHasHandler(DogHasHandler dogHasHandler);

    /**
     * Search for an active DogHasHandler by its id.
     *
     * @param id of the DogHasHandler
     * @return an instance of DogHasHandler with the given id, or throws an exception if not found
     */
    DogHasHandler getDogHasHandlerById(UUID id);

    /**
     * Set all DogHasHandlers with the given memberId to deleted status.
     * This is used when a member is deleted.
     *
     * @param memberId the id of the member
     */
    void deleteDogHasHandlersByMemberId(UUID memberId);

    /**
     * Set all DogHasHandlers with the given dogId to deleted status.
     * This is used when a dog is deleted.
     *
     * @param dogId the id of the dog
     */
    void deleteDogHasHandlersByDogId(UUID dogId);

    Set<Dog> getDogsByMemberId(UUID memberId);

    Set<Member> getMembersByDogId(UUID dogId);

    Set<DogHasHandler> getDogHasHandlersByMemberId(UUID memberId);

    Set<DogHasHandler> getDogHasHandlersByDogId(UUID dogId);

    /**
     * Search for an active DogHasHandler by name in members and dogs.
     *
     * @param name of the DogHasHandler
     * @return a set of DogHasHandler with the given name, or an empty set if not found
     */
    Set<DogHasHandler> searchByName(@Nullable String name);

    Set<DogHasHandler> getAllDogHasHandler();

    Set<DogHasHandler> getDogHasHandlersByIds(Set<UUID> handlerIds);

    Set<UUID> getDogHasHandlerIdsByMemberId(UUID memberId);

    Set<UUID> getDogHasHandlerIdsByDogId(UUID dogId);

    Set<String> getMembersEmailByIds(Set<UUID> handlerIds);

    /**
     * Set all DogHasHandlers with the given memberId to inactive status.
     * This is used when a member is deactivated.
     *
     * @param memberId the id of the member
     */
    void deactivateDogHasHandlersByMemberId(UUID memberId);

    /**
     * Set all DogHasHandlers with the given memberId to active status.
     * This is used when a member is activated.
     *
     * @param memberId the id of the member
     */
    void activateDogHasHandlersByMemberId(UUID memberId);
}
