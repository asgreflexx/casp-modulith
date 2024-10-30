package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.options.RecurrenceOptionUtility;
import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventCustomRepository;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Exam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.CalendarEntryMapper.CALENDAR_MAPPER;

abstract class BaseEventServiceImpl<D extends BaseEvent, T extends BaseEventDto> implements BaseEventService<T> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEventServiceImpl.class);

    protected final BaseRepository<D> baseRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final BaseEventCustomRepository<D> baseEventCustomRepository;
    private final Class<D> documentClass;
    private final BaseEventMigrationService migrationService;

    @SuppressWarnings("unchecked")
    BaseEventServiceImpl(final MemberReferenceRepository memberReferenceRepository,
                         final BaseRepository<D> baseRepository,
                         final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                         final BaseEventMigrationService migrationService) {
        this.memberReferenceRepository = memberReferenceRepository;
        this.baseRepository = baseRepository;
        this.baseEventCustomRepository = (BaseEventCustomRepository<D>) baseRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.migrationService = migrationService;
        var types = (ParameterizedType) getClass().getGenericSuperclass();
        this.documentClass = (Class<D>) types.getActualTypeArguments()[0];
    }

    private static <D extends BaseEvent> CalendarEntryDto mapToCalendarEntryDto(final D d, final CalendarEntry ce) {
        var calendarEntryDto = CALENDAR_MAPPER.fromBaseEvent(d);
        calendarEntryDto.setEntryFrom(ce.getEntryFrom());
        calendarEntryDto.setEntryTo(ce.getEntryTo());
        calendarEntryDto.setCalendarEntryId(ce.getId());
        return calendarEntryDto;
    }

    private static boolean isWithinRange(final CalendarEntry calendarEntry, final LocalDateTime from, final LocalDateTime to) {
        return !calendarEntry.getEntryFrom().isBefore(from) && !calendarEntry.getEntryTo().isAfter(to);
    }

    @Override
    public void deleteById(final UUID id) {
        var document = getOneByIdOrThrowException(id);
        saveItNewEntityStatus(document, EntityStatus.DELETED);
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.DELETED));
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.INACTIVE));
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        baseEventCustomRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)
                .forEach(d -> saveItNewEntityStatus(d, EntityStatus.ACTIVE));
    }

    @Override
    public Set<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(final LocalDateTime from, final LocalDateTime to) {
        return baseEventCustomRepository.findAllBetweenFromAndTo(from, to)
                .stream()
                .flatMap(d -> d.getCalendarEntries()
                        .stream()
                        .filter(ce -> isWithinRange(ce, from, to))
                        .map(ce -> mapToCalendarEntryDto(d, ce)))
                .collect(Collectors.toSet());
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

    protected void setCalendarEntriesAndMember(final T dto, final D document) {
        setCalendarEntries(dto, document);
        setMember(dto, document);
    }

    protected Optional<MemberReference> findMemberReferenceById(final UUID memberId) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(memberId, EntityStatus.ACTIVE);
    }

    protected D getOneByIdOrThrowException(final UUID id) {
        return baseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> {
                    var msg = "%s with id %s does not exist or it is not active.".formatted(documentClass.getSimpleName(), id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    protected void saveItNewEntityStatus(final D document, final EntityStatus entityStatus) {
        document.setEntityStatus(entityStatus);
        baseRepository.save(document);
    }

    protected Optional<DogHasHandlerReference> findDogHandlerReferenceById(final UUID dogHasHandlerId) {
        return dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE);
    }

    private void setCalendarEntries(final T dto, final D document) {
        if (null == dto.getRecurrenceOption()) {
            var newCalendarEntry = dto.getNewCalendarEntry();
            var calendarEntry = new CalendarEntry(newCalendarEntry.getEntryFrom(), newCalendarEntry.getEntryTo());
            document.setCalendarEntries(new ArrayList<>(List.of(calendarEntry)));
        } else {
            document.setCalendarEntries(RecurrenceOptionUtility.createCalendarEntries(dto.getRecurrenceOption()));
        }
    }

    private void setMember(final T dto, final D document) {
        if (dto.getNewMemberId() != null) {
            findMemberReferenceById(dto.getNewMemberId())
                    .ifPresent(document::setMember);
        }
    }
}
