package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.business.logic.layer.interfaces.IDogHasHandlerService;
import casp.web.backend.business.logic.layer.interfaces.IMemberService;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.IDogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import casp.web.backend.presentation.layer.DogHasHandlerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * DogHasHandlerService
 *
 * @author sarah
 */

@Service
public class DogHasHandlerService implements IDogHasHandlerService {

    private final IDogHasHandlerRepository iDogHasHandlerRepository;
    private final IMemberRepository iMemberRepository;
    private final IDogRepository iDogRepository;
    private final IMemberService iMemberService;

    @Autowired
    DogHasHandlerService(IDogHasHandlerRepository iDogHasHandlerRepository, IMemberRepository iMemberRepository, IDogRepository iDogRepository, IMemberService iMemberService) {
        this.iDogHasHandlerRepository = iDogHasHandlerRepository;
        this.iMemberRepository = iMemberRepository;
        this.iDogRepository = iDogRepository;
        this.iMemberService = iMemberService;
    }

    @Override
    public DogHasHandler saveDogHasHandler(DogHasHandler dogHasHandler) {
        if (dogHasHandler.getId() == null) {
            dogHasHandler.setId(UUID.randomUUID());
        }

        final Optional<DogHasHandler> optional = iDogHasHandlerRepository.findOneByMemberIdAndDogIdAndEntityStatusNotLike(
                dogHasHandler.getMemberId(),
                dogHasHandler.getDogId(),
                EntityStatus.DELETED);

        if (optional.isPresent() && !optional.get().getId().equals(dogHasHandler.getId())) {
            throw new IllegalArgumentException(String.format("This handler already exists with id %s and status %s",
                    dogHasHandler.getId().toString(),
                    dogHasHandler.getEntityStatus()));
        }

        return iDogHasHandlerRepository.save(dogHasHandler);
    }

    @Override
    public DogHasHandler getDogHasHandlerById(UUID id) {

        return iDogHasHandlerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("DogHasHandler not found."));
    }

    @Override
    public void deleteDogHasHandlerByMemberId(UUID memberId) {

        for (DogHasHandler dhDogsByMember : iDogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId)) {
            dhDogsByMember.setEntityStatus(EntityStatus.DELETED);
            iDogHasHandlerRepository.save(dhDogsByMember);
        }

    }

    @Override
    public void deleteDogHasHandlerByDogId(UUID dogId) {

        for (DogHasHandler dhMembersByDog : iDogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId)) {

            dhMembersByDog.setEntityStatus(EntityStatus.DELETED);
            iDogHasHandlerRepository.save(dhMembersByDog);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Dog> getDogsByMemberId(UUID memberId) {

        List<DogHasHandler> allByEntityStatusAndMemberId = iDogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId);
        List<UUID> dogIds = new ArrayList<>();
        for (DogHasHandler dogHasHandler : allByEntityStatusAndMemberId) {
            dogIds.add(dogHasHandler.getDogId());
        }
        Iterable<Dog> allById = iDogRepository.findAllById(dogIds);
        return StreamSupport.stream(allById.spliterator(), false).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Member> getMembersByDogId(UUID dogId) {

        List<DogHasHandler> allByEntityStatusAndDogId = iDogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId);
        List<UUID> memberIds = new ArrayList<>();
        for (DogHasHandler dogHasHandler : allByEntityStatusAndDogId) {
            memberIds.add(dogHasHandler.getMemberId());
        }
        Iterable<Member> allById = iMemberRepository.findAllById(memberIds);
        return StreamSupport.stream(allById.spliterator(), false).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<DogHasHandler> getHandlerByMemberId(UUID memberId) {
        return iDogHasHandlerRepository.findAllByMemberId(iMemberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found")).getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<DogHasHandler> getHandlerByDogId(UUID dogId) {
        return iDogHasHandlerRepository.findAllByDogId(iDogRepository.findById(dogId).orElseThrow(() -> new IllegalArgumentException("Dog not found")).getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<DogHasHandler> searchDogHasHandlerByFirstNameOrLastNameOrDogName(String name) {
        List<Dog> allDogsActiveAndName = iDogRepository.findAllByEntityStatusAndName(EntityStatus.ACTIVE, name);

        List<UUID> dogIds = new ArrayList<>();
        for (Dog dog : allDogsActiveAndName) {
            dogIds.add(dog.getId());
        }

        List<Member> allByFirstNameOrLastName = iMemberRepository.findAllByFirstNameOrLastName(name, name);
        List<UUID> memberIds = new ArrayList<>();
        for (Member member : allByFirstNameOrLastName) {
            memberIds.add(member.getId());
        }
        return iDogHasHandlerRepository.findAllByEntityStatusAndDogIdInOrMemberIdIn(EntityStatus.ACTIVE, dogIds, memberIds);
    }

    @Override
    public List<DogHasHandlerDto> getAllDogHasHandler() {

        List<DogHasHandler> dogHasHandlerList = iDogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
        return mapHandlersToDtos(dogHasHandlerList);
    }

    private List<DogHasHandlerDto> mapHandlersToDtos(List<DogHasHandler> dogHasHandlerList) {

        List<DogHasHandlerDto> dtoList = new ArrayList<>();
        for (DogHasHandler dogHasHandler : dogHasHandlerList) {
            Optional<Member> member = iMemberRepository.findById(dogHasHandler.getMemberId());
            var dto = new DogHasHandlerDto();
            dto.setId(dogHasHandler.getId());
            member.ifPresent(dto::setMember);
            Optional<Dog> dog = iDogRepository.findById(dogHasHandler.getDogId());
            dog.ifPresent(dto::setDog);
            dto.setGrades(dogHasHandler.getGrades());
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<DogHasHandlerDto> getDogHasHandlersByIds(List<UUID> handlerIds) {

        List<DogHasHandler> dogHasHandlers = iDogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, handlerIds);
        return mapHandlersToDtos(dogHasHandlers);

    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByMemberId(UUID id) {
        return iDogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, id)
                .stream()
                .map(DogHasHandler::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByDogId(UUID id) {
        return iDogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, id)
                .stream()
                .map(DogHasHandler::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getMembersEmailByIds(Set<UUID> ids) {
        final Set<UUID> memberIds = iDogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, new ArrayList<>(ids))
                .stream()
                .map(DogHasHandler::getMemberId)
                .collect(Collectors.toSet());

        return iMemberService.getMembersEmailByIds(memberIds);
    }
}
