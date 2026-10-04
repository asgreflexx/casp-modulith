package casp.web.backend.dog;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.exception.DogHasHandlerConflictException;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.dog.data.DogHasHandler;
import casp.web.backend.dog.data.DogHasHandlerRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;

@RequiredArgsConstructor
@Slf4j
@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogReferenceRepository dogReferenceRepository;
    private final DogHasHandlerRepository dogHasHandlerRepository;

    private static NoSuchElementException throwNoSuchElementException(String clazzName, UUID id) {
        var msg = "%s with id %s not found or it isn't active".formatted(clazzName, id);
        log.error(msg);
        return new NoSuchElementException(msg);
    }

    @Override
    public DogHasHandlerDto saveDogHasHandler(DogHasHandlerDto dogHasHandlerDto) {
        var dog = getActiveDogById(dogHasHandlerDto.getDogId());
        var member = getActiveMemberById(dogHasHandlerDto.getMemberId());

        verifyForDogHasHandlerConflict(dogHasHandlerDto);

        var dogHasHandler = DOG_HAS_HANDLER_MAPPER.toSource(dogHasHandlerDto);
        dogHasHandler.setDog(dog);
        dogHasHandler.setMember(member);

        return DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandlerRepository.save(dogHasHandler));
    }

    @Override
    public DogHasHandlerDto getDogHasHandlerById(UUID id) {
        var dogHasHandler = dogHasHandlerRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> throwNoSuchElementException(DogHasHandler.class.getSimpleName(), id));
        return DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandler);
    }

    @Override
    public void deleteDogHasHandlersByMemberId(UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.DELETED));
    }

    @Override
    public void deleteDogHasHandlersByDogId(UUID dogId) {
        dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dogId)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.DELETED));
    }

    @Override
    public void deleteDogHasHandlerById(UUID id) {
        var dogHasHandler = dogHasHandlerRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> throwNoSuchElementException(DogHasHandler.class.getSimpleName(), id));

        saveItWithNewStatus(dogHasHandler, EntityStatus.DELETED);
    }

    @Override
    public Page<DogHasHandlerDto> searchByValue(@Nullable String value, Pageable pageable) {
        var dogHasHandlerPage = dogHasHandlerRepository.findAllByValue(value, pageable);
        return DOG_HAS_HANDLER_MAPPER.toTargetPage(dogHasHandlerPage);
    }

    @Override
    public Page<DogHasHandlerDto> getAllDogHasHandlers(Pageable pageable) {
        var dogHasHandlerPage = dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
        return DOG_HAS_HANDLER_MAPPER.toTargetPage(dogHasHandlerPage);
    }

    @Override
    public Set<DogHasHandlerDto> getDogHasHandlersByIds(Set<UUID> ids) {
        return DOG_HAS_HANDLER_MAPPER.toTargetSet(getActiveDogHasHandlerSet(ids));
    }

    @Override
    public Set<String> getEmailsByDogHasHandlersIds(Set<UUID> ids) {
        return getActiveDogHasHandlerSet(ids)
                .stream()
                .map(dhh -> dhh.getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void deactivateDogHasHandlersByMemberId(UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.INACTIVE));
    }

    @Override
    public void activateDogHasHandlersByMemberId(UUID memberId) {
        dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.INACTIVE)
                .forEach(dhh -> saveItWithNewStatus(dhh, EntityStatus.ACTIVE));
    }

    @Override
    public Set<DogHasHandlerDto> getDogHasHandlerByMemberId(UUID memberId) {
        var dogHasHandlerSet = dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
        return DOG_HAS_HANDLER_MAPPER.toTargetSet(dogHasHandlerSet);
    }

    @Override
    public Set<DogHasHandlerDto> getDogHasHandlerByDogId(UUID dogId) {
        var dogHasHandlerSet = dogHasHandlerRepository.findAllByDogIdAndEntityStatus(dogId, EntityStatus.ACTIVE);
        return DOG_HAS_HANDLER_MAPPER.toTargetSet(dogHasHandlerSet);
    }

    @Override
    public void correctEntityStatus() {
        var dogHasHandlers = new HashSet<>(dogHasHandlerRepository.findAll());
        dogHasHandlers.forEach(this::setTheCorrectEntityStatus);
        dogHasHandlerRepository.saveAll(dogHasHandlers);
    }

    private void verifyForDogHasHandlerConflict(DogHasHandlerDto dogHasHandlerDto) {
        dogHasHandlerRepository.findByDogIdAndMemberId(dogHasHandlerDto.getDogId(), dogHasHandlerDto.getMemberId())
                .ifPresent(dhh -> {
                    if (!dhh.getId().equals(dogHasHandlerDto.getId())) {
                        var msg = "There is already a DogHasHandler[id: %s] with this dog[id: %s] and this member[id: %s]"
                                .formatted(dhh.getId(), dhh.getDog().getId(), dhh.getMember().getId());
                        log.error(msg);
                        throw new DogHasHandlerConflictException(msg);
                    }
                });
    }

    private MemberReference getActiveMemberById(UUID memberId) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(memberId, EntityStatus.ACTIVE).
                orElseThrow(() -> throwNoSuchElementException("Member", memberId));
    }

    private DogReference getActiveDogById(UUID dogId) {
        return dogReferenceRepository.findOneByIdAndEntityStatus(dogId, EntityStatus.ACTIVE).
                orElseThrow(() -> throwNoSuchElementException("Dog", dogId));
    }

    private void saveItWithNewStatus(DogHasHandler dogHasHandler, EntityStatus entityStatus) {
        dogHasHandler.setEntityStatus(entityStatus);
        dogHasHandlerRepository.save(dogHasHandler);
    }

    private Set<DogHasHandler> getActiveDogHasHandlerSet(Set<UUID> ids) {
        return dogHasHandlerRepository.findAllByIdInAndEntityStatus(ids, EntityStatus.ACTIVE);
    }

    private void setTheCorrectEntityStatus(DogHasHandler dogHasHandler) {
        var dog = dogHasHandler.getDog();
        var member = dogHasHandler.getMember();
        if (dog.getEntityStatus() == EntityStatus.DELETED || member.getEntityStatus() == EntityStatus.DELETED) {
            dogHasHandler.setEntityStatus(EntityStatus.DELETED);
        } else if (dog.getEntityStatus() == EntityStatus.INACTIVE || member.getEntityStatus() == EntityStatus.INACTIVE) {
            dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
        }
    }
}
