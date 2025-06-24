package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEvent;
import casp.web.backend.calendar.data.BaseEventCustomRepository;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.calendar.options.RecurrenceOptionUtility;
import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
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
    private final BaseEventMigrationService migrationService;

    @SuppressWarnings("unchecked")
    BaseEventServiceImpl(MemberReferenceRepository memberReferenceRepository,
                         BaseRepository<D> baseRepository,
                         DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                         BaseEventMigrationService migrationService) {
        this.memberReferenceRepository = memberReferenceRepository;
        this.baseRepository = baseRepository;
        baseEventCustomRepository = (BaseEventCustomRepository<D>) baseRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.migrationService = migrationService;
        var types = (ParameterizedType) getClass().getGenericSuperclass();
        documentClass = (Class<D>) types.getActualTypeArguments()[0];
    }

    private static boolean isWithinRange(CalendarEntry calendarEntry, LocalDateTime from, LocalDateTime to) {
        return !calendarEntry.getEntryFrom().isBefore(from) && !calendarEntry.getEntryTo().isAfter(to);
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
    public Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(LocalDateTime from, LocalDateTime to) {
        return baseEventCustomRepository.findAllBetweenFromAndTo(from, to)
                .flatMap(d -> d.getCalendarEntries()
                        .stream()
                        .filter(ce -> isWithinRange(ce, from, to))
                        .map(ce -> new CalendarEntryDto(ce, d)));
    }

    @Override
    public T getOneByIdAndCalendarEntryId(UUID id, UUID calendarEntryId) {
        var dto = getOneById(id);
        var calendarEntry = dto.getCalendarEntries()
                .stream()
                .filter(ce -> calendarEntryId.equals(ce.getId()))
                .findAny()
                .orElseThrow(() -> {
                    var msg = "The Calendar entry with Id %s not found in %s with Id %s"
                            .formatted(calendarEntryId, documentClass.getSimpleName(), id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
        dto.setCalendarEntries(List.of(calendarEntry));
        return dto;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void migrateDataToV2() {
        baseRepository.deleteAll();
        Set<D> documents;
        if (documentClass.equals(Course.class)) {
            documents = (Set<D>) migrationService.mapToCourseV2();
        } else if (documentClass.equals(Exam.class)) {
            documents = (Set<D>) migrationService.mapToExamV2();
        } else {
            documents = (Set<D>) migrationService.mapToEventV2();
        }
        baseRepository.saveAll(documents);
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

    protected DogHasHandlerReference findDogHandlerReferenceByIdOrThrowException(UUID dogHasHandlerId) {
        return findDogHandlerReferenceById(dogHasHandlerId)
                .orElseThrow(() -> {
                    var msg = "Dog has handler with id %s does not exist or it is not active.".formatted(dogHasHandlerId);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    protected Set<P> getExistingParticipantsMatchingDtoParticipantIds(final T dto) {
        return baseRepository.findOneByIdAndEntityStatus(dto.getId(), EntityStatus.ACTIVE)
                .stream()
                .flatMap(p -> p.getParticipants().stream())
                .filter(p -> dto.getParticipantIds().contains(p.getId()))
                .collect(Collectors.toSet());
    }

    protected Set<P> getNewParticipants(final T dto, final Set<P> existingParticipants) {
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
