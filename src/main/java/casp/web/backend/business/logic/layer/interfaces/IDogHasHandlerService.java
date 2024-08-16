package casp.web.backend.business.logic.layer.interfaces;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.presentation.layer.DogHasHandlerDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * IDogHasHandlerService
 *
 * @author sarah
 */

public interface IDogHasHandlerService {

    DogHasHandler saveDogHasHandler(DogHasHandler dogHasHandler);

    DogHasHandler getDogHasHandlerById(UUID id);

    void deleteDogHasHandlerByMemberId(UUID memberId);

    void deleteDogHasHandlerByDogId(UUID dogId);

    List<Dog> getDogsByMemberId(UUID memberId);

    List<Member> getMembersByDogId(UUID dogId);

    List<DogHasHandler> getHandlerByMemberId(UUID memberId);

    List<DogHasHandler> getHandlerByDogId(UUID dogId);

    List<DogHasHandler> searchDogHasHandlerByFirstNameOrLastNameOrDogName(String name);

    List<DogHasHandlerDto> getAllDogHasHandler();

    List<DogHasHandlerDto> getDogHasHandlersByIds(List<UUID> handlerIds);

    Set<UUID> getDogHasHandlerIdsByMemberId(UUID id);

    Set<UUID> getDogHasHandlerIdsByDogId(UUID id);

    Set<String> getMembersEmailByIds(Set<UUID> ids);
}
