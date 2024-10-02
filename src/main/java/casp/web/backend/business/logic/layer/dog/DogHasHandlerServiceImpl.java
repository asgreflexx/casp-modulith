package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.business.logic.layer.event.participants.BaseParticipantObserver;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private static final Logger LOG = LoggerFactory.getLogger(DogHasHandlerServiceImpl.class);

    private final DogHasHandlerRepository dogHasHandlerRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final BaseParticipantObserver baseParticipantObserver;

    @Autowired
    DogHasHandlerServiceImpl(final DogHasHandlerRepository dogHasHandlerRepository,
                             final MemberRepository memberRepository,
                             final DogRepository dogRepository,
                             final BaseParticipantObserver baseParticipantObserver) {
        this.dogHasHandlerRepository = dogHasHandlerRepository;
        this.memberRepository = memberRepository;
        this.dogRepository = dogRepository;
        this.baseParticipantObserver = baseParticipantObserver;
    }

    @Override
    public DogHasHandler saveDogHasHandler(final DogHasHandler dogHasHandler) {
        setDogAndMember(dogHasHandler);
        return dogHasHandlerRepository.save(dogHasHandler);
    }

    @Override
    public DogHasHandler getDogHasHandlerById(final UUID id) {
        var dogHasHandler = dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "DogHasHandler with id %s not found or it isn't active".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
        return setDogAndMember(dogHasHandler);
    }

    @Override
    public void deleteDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatusIsNot(memberId, EntityStatus.DELETED)
                .forEach(this::deleteDogHasHandler);
    }

    @Override
    public void deleteDogHasHandlersByDogId(final UUID dogId) {
        dogHasHandlerRepository.findAllByDogIdAndEntityStatusNot(dogId, EntityStatus.DELETED)
                .forEach(this::deleteDogHasHandler);
    }

    @Override
    public Set<Dog> getDogsByMemberId(final UUID memberId) {
        return getDogHasHandlersByMemberId(memberId).stream()
                .map(dh -> setDogAndMember(dh).getDog())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Member> getMembersByDogId(final UUID dogId) {
        return getDogHasHandlersByDogId(dogId).stream()
                .map(dh -> setDogAndMember(dh).getMember())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<DogHasHandler> getDogHasHandlersByMemberId(final UUID memberId) {
        return setMissingMembersAndDogs(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE));
    }

    @Override
    public Set<DogHasHandler> getDogHasHandlersByDogId(final UUID dogId) {
        return setMissingMembersAndDogs(dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE));
    }

    @Override
    public Set<DogHasHandler> searchByName(final String name) {
        return setMissingMembersAndDogs(dogHasHandlerRepository.findAllByMemberNameOrDogName(name));
    }

    @Override
    public Set<DogHasHandler> getAllDogHasHandler() {
        return dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
    }

    @Override
    public Set<DogHasHandler> getDogHasHandlersByIds(final Set<UUID> handlerIds) {
        return dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, handlerIds);
    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByMemberId(final UUID memberId) {
        return getDogHasHandlersByMemberId(memberId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByDogId(final UUID dogId) {
        return getDogHasHandlersByDogId(dogId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getMembersEmailByIds(final Set<UUID> handlerIds) {
        return getDogHasHandlersByIds(handlerIds).stream()
                .flatMap(dh -> Optional.ofNullable(setDogAndMember(dh).getMember())
                        .map(Member::getEmail)
                        .stream())
                .collect(Collectors.toSet());
    }

    @Override
    public void deactivateDogHasHandlersByMemberId(final UUID memberId) {
        getDogHasHandlersByMemberId(memberId).forEach(dh -> {
            baseParticipantObserver.deactivateParticipantsByMemberOrHandlerId(dh.getId());
            saveItWithNewStatus(dh, EntityStatus.INACTIVE);
        });
    }

    @Override
    public void activateDogHasHandlersByMemberId(final UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE).forEach(dh -> {
            baseParticipantObserver.activateParticipantsByMemberOrHandlerId(dh.getId());
            saveItWithNewStatus(dh, EntityStatus.ACTIVE);
        });
    }

    private Set<DogHasHandler> setMissingMembersAndDogs(final Set<DogHasHandler> dogHasHandlers) {
        dogHasHandlers.forEach(this::setDogAndMember);
        return dogHasHandlers;
    }

    private void saveItWithNewStatus(final DogHasHandler dh, final EntityStatus entityStatus) {
        dh.setEntityStatus(entityStatus);
        dogHasHandlerRepository.save(dh);
    }

    private void deleteDogHasHandler(final DogHasHandler dh) {
        baseParticipantObserver.deleteParticipantsByMemberOrHandlerId(dh.getId());
        saveItWithNewStatus(dh, EntityStatus.DELETED);
    }

    private DogHasHandler setDogAndMember(final DogHasHandler dogHasHandler) {
        if (null == dogHasHandler.getMember()) {
            memberRepository.findByIdAndEntityStatus(dogHasHandler.getMemberId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setMember);
        }
        if (null == dogHasHandler.getDog()) {
            dogRepository.findDogByIdAndEntityStatus(dogHasHandler.getDogId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setDog);
        }
        return dogHasHandler;
    }
}
