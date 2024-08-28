package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.member.Member;

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

    void deleteDogHasHandlerByMemberId(UUID memberId);

    void deleteDogHasHandlersByDogId(UUID dogId);

    Set<Dog> getDogsByMemberId(UUID memberId);

    Set<Member> getMembersByDogId(UUID dogId);

    Set<DogHasHandler> getHandlersByMemberId(UUID memberId);

    Set<DogHasHandler> getHandlersByDogId(UUID dogId);

    Set<DogHasHandler> searchDogHasHandlerByFirstNameOrLastNameOrDogName(String name);

    Set<DogHasHandler> getAllDogHasHandler();

    Set<DogHasHandler> getDogHasHandlersByIds(Set<UUID> handlerIds);

    Set<UUID> getDogHasHandlerIdsByMemberId(UUID memberId);

    Set<UUID> getDogHasHandlerIdsByDogId(UUID dogId);

    Set<String> getMembersEmailByIds(Set<UUID> handlerIds);
}
