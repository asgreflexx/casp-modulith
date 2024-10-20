package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.deprecated.event.calendar.CalendarRepository;
import casp.web.backend.deprecated.event.participants.BaseParticipantRepository;
import casp.web.backend.deprecated.event.participants.CoTrainer;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.BaseEvent;
import casp.web.backend.deprecated.event.types.BaseEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.deprecated.event.calendar.CalendarV2Mapper.CALENDAR_V2_MAPPER;
import static casp.web.backend.deprecated.event.options.BaseEventOptionV2Mapper.BASE_EVENT_OPTION_V2_MAPPER;
import static casp.web.backend.deprecated.event.participants.BaseParticipantV2Mapper.BASE_PARTICIPANT_V2_MAPPER;
import static casp.web.backend.deprecated.event.types.BaseEventV2Mapper.BASE_EVENT_V2_MAPPER;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@Service
class BaseEventMigrationService {
    private static final Sort SORT = Sort.by("eventFrom").ascending().and(Sort.by("eventTo").ascending());

    private final BaseEventRepository baseEventRepository;
    private final BaseParticipantRepository baseParticipantRepository;
    private final CalendarRepository calendarRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    BaseEventMigrationService(final BaseEventRepository baseEventRepository,
                              final BaseParticipantRepository baseParticipantRepository,
                              final CalendarRepository calendarRepository,
                              final MemberReferenceRepository memberReferenceRepository,
                              final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.baseEventRepository = baseEventRepository;
        this.baseParticipantRepository = baseParticipantRepository;
        this.calendarRepository = calendarRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    static Optional<RecurrenceOption> mapToBaseEventOptionV2(final BaseEvent baseEvent) {
        if (null != baseEvent.getDailyOption()) {
            return Optional.of(BASE_EVENT_OPTION_V2_MAPPER.toDailyEventOption(baseEvent.getDailyOption()));
        } else if (null != baseEvent.getWeeklyOption()) {
            return Optional.of(BASE_EVENT_OPTION_V2_MAPPER.toWeeklyEventOption(baseEvent.getWeeklyOption()));
        } else {
            return Optional.empty();
        }
    }

    Set<Course> mapToCourseV2() {
        return baseEventRepository.findAllByEventType(casp.web.backend.deprecated.event.types.Course.EVENT_TYPE)
                .stream()
                .flatMap(cv1 -> memberReferenceRepository.findById(cv1.getMemberId())
                        .map(m -> mapCourseV1ToCourseV2(cv1, m)).stream()
                ).collect(Collectors.toSet());
    }

    Set<Event> mapToEventV2() {
        return baseEventRepository.findAllByEventType(casp.web.backend.deprecated.event.types.Event.EVENT_TYPE)
                .stream()
                .flatMap(ev1 -> memberReferenceRepository.findById(ev1.getMemberId())
                        .map(m -> mapEventV1ToEventV2(ev1, m)).stream()
                ).collect(Collectors.toSet());
    }

    Set<Exam> mapToExamV2() {
        return baseEventRepository.findAllByEventType(casp.web.backend.deprecated.event.types.Exam.EVENT_TYPE)
                .stream()
                .flatMap(ev1 -> memberReferenceRepository.findById(ev1.getMemberId())
                        .map(m -> mapExamV1ToExamV2(ev1, m)).stream()
                ).collect(Collectors.toSet());
    }

    private Course mapCourseV1ToCourseV2(final BaseEvent cv1, final MemberReference m) {
        var courseV2 = BASE_EVENT_V2_MAPPER.toCourse((casp.web.backend.deprecated.event.types.Course) cv1);
        courseV2.setMember(m);
        var calendarEntries = mapCalendarEntries(cv1.getId());
        courseV2.setCalendarEntries(calendarEntries.calendarEntries);
        courseV2.setLocation(calendarEntries.location);
        courseV2.setSpaces(mapToSpaceV2(cv1.getId()));
        courseV2.setCoTrainers(mapToCoTrainerV2(cv1.getId()));
        mapToBaseEventOptionV2(cv1).ifPresent(courseV2::setRecurrenceOption);
        return courseV2;
    }

    private Event mapEventV1ToEventV2(final BaseEvent ev1, final MemberReference m) {
        var eventV2 = BASE_EVENT_V2_MAPPER.toEvent((casp.web.backend.deprecated.event.types.Event) ev1);
        eventV2.setMember(m);
        var calendarEntries = mapCalendarEntries(ev1.getId());
        eventV2.setCalendarEntries(calendarEntries.calendarEntries);
        eventV2.setLocation(calendarEntries.location);
        eventV2.setParticipants(mapToEventParticipantV2(ev1.getId()));
        mapToBaseEventOptionV2(ev1).ifPresent(eventV2::setRecurrenceOption);
        return eventV2;
    }

    private Exam mapExamV1ToExamV2(final BaseEvent ev1, final MemberReference m) {
        var examV2 = BASE_EVENT_V2_MAPPER.toExam((casp.web.backend.deprecated.event.types.Exam) ev1);
        examV2.setMember(m);
        var calendarEntries = mapCalendarEntries(ev1.getId());
        examV2.setCalendarEntries(calendarEntries.calendarEntries);
        examV2.setLocation(calendarEntries.location);
        examV2.setParticipants(mapToExamParticipantV2(ev1.getId()));
        mapToBaseEventOptionV2(ev1).ifPresent(examV2::setRecurrenceOption);
        return examV2;
    }

    private Set<casp.web.backend.data.access.layer.event.participants.Space> mapToSpaceV2(final UUID id) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, Space.PARTICIPANT_TYPE)
                .stream()
                .flatMap(sv1 ->
                        dogHasHandlerReferenceRepository.findById(sv1.getMemberOrHandlerId())
                                .map(dhh -> {
                                    var spaceV2 = BASE_PARTICIPANT_V2_MAPPER.toSpace((Space) sv1);
                                    spaceV2.setDogHasHandler(dhh);
                                    return spaceV2;
                                }).stream()
                )
                .collect(Collectors.toSet());
    }

    private Set<casp.web.backend.data.access.layer.event.participants.CoTrainer> mapToCoTrainerV2(final UUID id) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, CoTrainer.PARTICIPANT_TYPE)
                .stream()
                .flatMap(ctv1 ->
                        memberReferenceRepository.findById(ctv1.getMemberOrHandlerId())
                                .map(mV2 -> {
                                    var coTrainerV2 = BASE_PARTICIPANT_V2_MAPPER.toCoTrainer((CoTrainer) ctv1);
                                    coTrainerV2.setMember(mV2);
                                    return coTrainerV2;
                                }).stream()
                )
                .collect(Collectors.toSet());
    }

    private Set<ExamParticipant> mapToExamParticipantV2(final UUID id) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, casp.web.backend.deprecated.event.participants.ExamParticipant.PARTICIPANT_TYPE)
                .stream()
                .flatMap(pv1 ->
                        dogHasHandlerReferenceRepository.findById(pv1.getMemberOrHandlerId())
                                .map(dhh -> {
                                    var spaceV2 = BASE_PARTICIPANT_V2_MAPPER.toExamParticipant((casp.web.backend.deprecated.event.participants.ExamParticipant) pv1);
                                    spaceV2.setDogHasHandler(dhh);
                                    return spaceV2;
                                }).stream()
                )
                .collect(Collectors.toSet());
    }

    private Set<EventParticipant> mapToEventParticipantV2(final UUID id) {
        return baseParticipantRepository.findAllByBaseEventIdAndParticipantType(id, casp.web.backend.deprecated.event.participants.EventParticipant.PARTICIPANT_TYPE)
                .stream()
                .flatMap(epv1 ->
                        memberReferenceRepository.findById(epv1.getMemberOrHandlerId())
                                .map(mV2 -> {
                                    var coTrainerV2 = BASE_PARTICIPANT_V2_MAPPER.toEventParticipant((casp.web.backend.deprecated.event.participants.EventParticipant) epv1);
                                    coTrainerV2.setMember(mV2);
                                    return coTrainerV2;
                                }).stream()
                )
                .collect(Collectors.toSet());
    }

    private CalendarEntries mapCalendarEntries(final UUID id) {
        var calendarList = calendarRepository.findAllByBaseEventId(id, SORT);
        var calendarEntryList = CALENDAR_V2_MAPPER.toCalendarEntryList(calendarList);
        return new CalendarEntries(calendarList.getFirst().getLocation(), calendarEntryList);
    }

    private record CalendarEntries(String location, List<CalendarEntry> calendarEntries) {
    }
}
