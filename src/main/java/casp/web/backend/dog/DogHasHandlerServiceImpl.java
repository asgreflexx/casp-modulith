package casp.web.backend.dog;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.dog.data.DogHasHandler;
import casp.web.backend.dog.data.DogHasHandlerRepository;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.dog.DogHasHandlerV2Mapper.DOG_HAS_HANDLER_V2_MAPPER;
import static casp.web.backend.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;

@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private static final Logger LOG = LoggerFactory.getLogger(DogHasHandlerServiceImpl.class);

    private final DogHasHandlerOldRepository dogHasHandlerOldRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogReferenceRepository dogReferenceRepository;
    private final DogHasHandlerRepository dogHasHandlerRepository;

    @Autowired
    DogHasHandlerServiceImpl(DogHasHandlerOldRepository dogHasHandlerOldRepository,
                             MemberReferenceRepository memberReferenceRepository,
                             DogReferenceRepository dogReferenceRepository,
                             DogHasHandlerRepository dogHasHandlerRepository) {
        this.dogHasHandlerOldRepository = dogHasHandlerOldRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.dogReferenceRepository = dogReferenceRepository;
        this.dogHasHandlerRepository = dogHasHandlerRepository;
    }

    private static NoSuchElementException throwNoSuchElementException(String clazzName, UUID id) {
        var msg = "%s with id %s not found or it isn't active".formatted(clazzName, id);
        LOG.error(msg);
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
    public Page<DogHasHandlerDto> searchByName(@Nullable String name, Pageable pageable) {
        var dogHasHandlerPage = dogHasHandlerRepository.findAllByName(name, pageable);
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
    public void migrateDataToV2() {
        var dogHasHandlerSet = dogHasHandlerOldRepository.findAll()
                .stream()
                .flatMap(dh -> dogReferenceRepository.findById(dh.getDogId())
                        .flatMap(dog -> findMemberAndMapToDogHasHandlerV2(dh, dog)).stream())
                .collect(Collectors.toSet());

        dogHasHandlerRepository.saveAll(dogHasHandlerSet);
    }

    private void verifyForDogHasHandlerConflict(final DogHasHandlerDto dogHasHandlerDto) {
        dogHasHandlerRepository.findByDogIdAndMemberId(dogHasHandlerDto.getDogId(), dogHasHandlerDto.getMemberId())
                .ifPresent(dhh -> {
                    if (!dhh.getId().equals(dogHasHandlerDto.getId())) {
                        var msg = "There is already a DogHasHandler[id: %s] with this dog[id: %s] and this member[id: %s]"
                                .formatted(dhh.getId(), dhh.getDog().getId(), dhh.getMember().getId());
                        LOG.error(msg);
                        throw new IllegalStateException(msg);
                    }
                });
    }

    private MemberReference getActiveMemberById(final UUID memberId) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(memberId, EntityStatus.ACTIVE).
                orElseThrow(() -> throwNoSuchElementException("Member", memberId));
    }

    private DogReference getActiveDogById(final UUID dogId) {
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

    private Optional<DogHasHandler> findMemberAndMapToDogHasHandlerV2(casp.web.backend.deprecated.dog.DogHasHandler dh, DogReference dog) {
        return memberReferenceRepository.findById(dh.getMemberId()).map(member -> {
            var dogHasHandler = DOG_HAS_HANDLER_V2_MAPPER.toDogHasHandler(dh);
            dogHasHandler.setDog(dog);
            dogHasHandler.setMember(member);
            return dogHasHandler;
        });
    }
}
