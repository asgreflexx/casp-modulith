package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.participants.SpaceRepository;
import casp.web.backend.data.access.layer.event.types.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
class SpaceServiceImpl implements SpaceService {
    private final DogHasHandlerRepository dogHasHandlerRepository;
    private final SpaceRepository spaceRepository;

    @Autowired
    SpaceServiceImpl(final DogHasHandlerRepository dogHasHandlerRepository, final SpaceRepository spaceRepository) {
        this.dogHasHandlerRepository = dogHasHandlerRepository;
        this.spaceRepository = spaceRepository;
    }

    @Override
    public Space saveParticipant(final Space space) {
        findDogHasHandler(space.getMemberOrHandlerId())
                .ifPresent(space::setDogHasHandler);

        return spaceRepository.save(space);
    }

    @Override
    public void replaceParticipants(final Course course, final Set<UUID> spacesId) {
        var spaces = createSpaces(course, spacesId);
        spaceRepository.deleteAllByBaseEventId(course.getId());
        spaceRepository.saveAll(spaces);
        course.setParticipantsSize(spaces.size());
    }

    @Override
    public Set<Space> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(final UUID baseEventId) {
        return getParticipantsByBaseEventId(baseEventId)
                .stream()
                .flatMap(s -> findDogHasHandler(s.getMemberOrHandlerId())
                        .map(dh -> {
                            s.setDogHasHandler(dh);
                            return s;
                        })
                        .stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Space> getParticipantsByBaseEventId(final UUID courseId) {
        return spaceRepository.findAllByBaseEventIdAndEntityStatus(courseId, EntityStatus.ACTIVE);
    }

    @Override
    public void deleteParticipantsByBaseEventId(final UUID courseId) {
        spaceRepository.findAllByBaseEventIdAndEntityStatusNot(courseId, EntityStatus.DELETED)
                .forEach(participant -> saveItWithStatus(participant, EntityStatus.DELETED));
    }

    @Override
    public void deactivateParticipantsByBaseEventId(final UUID courseId) {
        getParticipantsByBaseEventId(courseId)
                .forEach(participant -> saveItWithStatus(participant, EntityStatus.INACTIVE));
    }

    @Override
    public void activateParticipantsByBaseEventId(final UUID courseId) {
        spaceRepository.findAllByBaseEventIdAndEntityStatus(courseId, EntityStatus.INACTIVE)
                .forEach(participant -> saveItWithStatus(participant, EntityStatus.ACTIVE));
    }

    @Override
    public void deleteParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        spaceRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(memberOrHandlerId, EntityStatus.DELETED, Space.PARTICIPANT_TYPE)
                .forEach(participant -> saveItWithStatus((Space) participant, EntityStatus.DELETED));
    }

    @Override
    public void deactivateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        spaceRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.ACTIVE, Space.PARTICIPANT_TYPE)
                .forEach(participant -> saveItWithStatus((Space) participant, EntityStatus.INACTIVE));
    }

    @Override
    public void activateParticipantsByMemberOrHandlerId(final UUID memberOrHandlerId) {
        spaceRepository.findAllByMemberOrHandlerIdAndEntityStatus(memberOrHandlerId, EntityStatus.INACTIVE, Space.PARTICIPANT_TYPE)
                .forEach(participant -> saveItWithStatus((Space) participant, EntityStatus.ACTIVE));
    }

    @Override
    public Set<Space> getSpacesByMemberId(final UUID memberId) {
        return spaceRepository.findAllByMemberId(memberId);
    }

    @Override
    public Set<Space> getSpacesByDogId(final UUID dogId) {
        return spaceRepository.findAllByDogId(dogId);
    }

    private Set<Space> createSpaces(final Course course, final Set<UUID> spacesId) {
        return spacesId.stream()
                .flatMap(id ->
                        findDogHasHandler(id).map(dh -> new Space(course, dh)).stream())
                .collect(Collectors.toSet());
    }

    private Optional<DogHasHandler> findDogHasHandler(final UUID dogHasHandlerId) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE);
    }

    private void saveItWithStatus(final Space space, final EntityStatus entityStatus) {
        space.setEntityStatus(entityStatus);
        spaceRepository.save(space);
    }
}
