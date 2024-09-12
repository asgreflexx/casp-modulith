package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.member.Member;
import jakarta.annotation.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * IDogHasHandlerService
 *
 * @author sarah
 */

public interface DogHasHandlerService {

    DogHasHandler saveDogHasHandler(DogHasHandler dogHasHandler);

    DogHasHandler getDogHasHandlerById(UUID id);

    void deleteDogHasHandlersByMemberId(UUID memberId);

    void deleteDogHasHandlersByDogId(UUID dogId);

    Set<Dog> getDogsByMemberId(UUID memberId);

    Set<Member> getMembersByDogId(UUID dogId);

    Set<DogHasHandler> getDogHasHandlersByMemberId(UUID memberId);

    Set<DogHasHandler> getDogHasHandlersByDogId(UUID dogId);

    Set<DogHasHandler> searchByName(@Nullable String name);

    Set<DogHasHandler> getAllDogHasHandler();

    Set<DogHasHandler> getDogHasHandlersByIds(Set<UUID> handlerIds);

    Set<UUID> getDogHasHandlerIdsByMemberId(UUID memberId);

    Set<UUID> getDogHasHandlerIdsByDogId(UUID dogId);

    Set<String> getMembersEmailByIds(Set<UUID> handlerIds);

    void deactivateDogHasHandlersByMemberId(UUID memberId);

    void activateDogHasHandlersByMemberId(UUID memberId);
}
