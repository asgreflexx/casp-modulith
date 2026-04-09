package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventCustomRepository;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.calendar.options.RecurrenceOptionUtility;
import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class BaseEventServiceImpl<D extends BaseEvent<P>, T extends BaseEventDto<P>, P extends BaseParticipant> implements BaseEventService<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);

    protected final BaseRepository<D> baseRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final BaseEventCustomRepository<D> baseEventCustomRepository;
    private final Class<D> documentClass;

    @SuppressWarnings("unchecked")
    BaseEventServiceImpl(MemberReferenceRepository memberReferenceRepository,
                         BaseRepository<D> baseRepository,
                         DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.memberReferenceRepository = memberReferenceRepository;
        this.baseRepository = baseRepository;
        baseEventCustomRepository = (BaseEventCustomRepository<D>) baseRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        var types = (ParameterizedType) getClass().getGenericSuperclass();
        documentClass = (Class<D>) types.getActualTypeArguments()[0];
    }

    @Override
    public void deleteById(UUID id) {
        var document = getOneByIdOrThrowException(id);
        saveItNewEntityStatus(document, EntityStatus.DELETED);
    }

    @Override
    public void deleteBaseEventsByMemberId(UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.DELETED));
    }

    @Override
    public void deactivateBaseEventsByMemberId(UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.INACTIVE));
    }

    @Override
    public void activateBaseEventsByMemberId(UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.ACTIVE));
    }

    @Override
    public Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, UUID memberId) {
        return baseEventCustomRepository.findAllBetweenFromAndToOrMemberId(from, to, memberId)
                .flatMap(d -> d.getCalendarEntries()
                        .stream()
                        .map(ce -> new CalendarEntryDto(ce, d)));
    }

    protected void setCalendarEntriesAndMember(T dto, D document) {
        setCalendarEntries(dto, document);
        setMember(dto, document);
    }

    protected Optional<MemberReference> findMemberReferenceById(UUID memberId) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }

    protected D getOneByIdOrThrowException(UUID id) {
        return baseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> {
                    var msg = "%s with id %s does not exist or it is not active.".formatted(documentClass.getSimpleName(), id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    protected void saveItNewEntityStatus(D document, EntityStatus entityStatus) {
        document.setEntityStatus(entityStatus);
        baseRepository.save(document);
    }

    protected Optional<DogHasHandlerReference> findDogHandlerReferenceById(UUID dogHasHandlerId) {
        return dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE);
    }

    protected Set<P> getExistingParticipantsMatchingDtoParticipantIds(T dto) {
        return baseRepository.findOneByIdAndEntityStatus(dto.getId(), EntityStatus.ACTIVE)
                .stream()
                .flatMap(p -> p.getParticipants().stream())
                .filter(p -> dto.getParticipantIds().contains(p.getId()))
                .collect(Collectors.toSet());
    }

    protected Set<P> getNewParticipants(T dto, Set<P> existingParticipants) {
        var existingParticipantIds = existingParticipants.stream().map(P::getId).collect(Collectors.toSet());
        return dto.getParticipantIds().stream()
                .filter(participantId -> !existingParticipantIds.contains(participantId))
                .flatMap(this::mapToParticipant)
                .collect(Collectors.toSet());
    }

    protected void setParticipants(T dto, D d) {
        // 1. Get existing active participants from the DB event that are also desired in the DTO's list
        var existingParticipants = getExistingParticipantsMatchingDtoParticipantIds(dto);

        // 2. Determine which participant IDs from the DTO are genuinely new (not already linked to the existing event)
        var newParticipants = getNewParticipants(dto, existingParticipants);

        // 3. Combine the existing participants (those already in the DB and still desired) with the new ones
        d.addParticipants(existingParticipants);
        d.addParticipants(newParticipants);
    }

    private void setCalendarEntries(T dto, D document) {
        if (null == dto.getRecurrenceOption()) {
            var newCalendarEntry = dto.getNewCalendarEntry();
            var calendarEntry = new CalendarEntry(newCalendarEntry.getEntryFrom(), newCalendarEntry.getEntryTo());
            document.addCalendarEntry(calendarEntry);
        } else {
            document.setCalendarEntries(RecurrenceOptionUtility.createCalendarEntries(dto.getRecurrenceOption()));
        }
    }

    private void setMember(T dto, D document) {
        findMemberReferenceById(dto.getMemberId())
                .ifPresentOrElse(document::setMember,
                        () -> {
                            var msg = "Member with id %s does not exist or it is not active.".formatted(dto.getMemberId());
                            LOG.error(msg);
                            throw new NoSuchElementException(msg);
                        });
    }

    abstract Stream<P> mapToParticipant(UUID id);
}
