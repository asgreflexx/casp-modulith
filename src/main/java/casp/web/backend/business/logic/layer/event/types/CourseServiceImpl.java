package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.options.RecurrenceOptionUtility;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl implements CourseService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final MemberReferenceRepository memberReferenceRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    private final BaseEventMigrationService migrationService;

    @Autowired
    CourseServiceImpl(final CourseRepository courseRepository,
                      final MemberReferenceRepository memberReferenceRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      final BaseEventMigrationService migrationService) {
        this.courseRepository = courseRepository;
        this.memberReferenceRepository = memberReferenceRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        this.migrationService = migrationService;
    }

    private static void setDateEntries(final CourseDto courseDto, final Course course) {
        if (null == courseDto.getRecurrenceOption()) {
            course.setCalendarEntries(new ArrayList<>(List.of(courseDto.getNewCalendarEntry())));
        } else {
            course.setCalendarEntries(RecurrenceOptionUtility.createCalendarEntries(courseDto.getRecurrenceOption()));
        }
    }

    private static Set<Space> getSpaces(final Course course, final UUID id) {
        return course.getSpaces()
                .stream()
                .filter(s -> !id.equals(s.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public void save(final CourseDto courseDto) {
        var course = COURSE_MAPPER.toSource(courseDto);

        setDateEntries(courseDto, course);
        setMember(courseDto, course);
        setCoTrainers(courseDto, course);
        setSpaces(courseDto, course);

        courseRepository.setMetadataAndSave(course);
    }

    @Override
    public CourseDto getOneById(final UUID id) {
        return COURSE_MAPPER.toTarget(getCourse(id));
    }

    @Override
    public Page<CourseDto> getAllByYear(final int year, final Pageable pageable) {
        var coursePage = courseRepository.findAllByYear(year, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    @Override
    public void deleteById(final UUID id) {
        var course = getCourse(id);
        setNewEntityStatus(course, EntityStatus.DELETED);
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        courseRepository.findAllByMemberIdAndNotDeleted(memberId)
                .forEach(course -> setNewEntityStatus(course, EntityStatus.DELETED));
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        courseRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .forEach(course -> setNewEntityStatus(course, EntityStatus.INACTIVE));
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        courseRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)
                .forEach(course -> setNewEntityStatus(course, EntityStatus.ACTIVE));
    }

    @Override
    public void migrateDataToV2() {
        courseRepository.deleteAll();

        var courseSet = migrationService.mapToCourseV2();

        courseRepository.saveAll(courseSet);
    }

    @Override
    public Set<String> getEmailsByCourseId(final UUID id) {
        return getCourse(id)
                .getSpaces()
                .stream()
                .map(s -> s.getDogHasHandler().getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void saveSpace(final UUID courseId, final Space space) {
        var course = getCourse(courseId);
        var spaceSet = getSpaces(course, space.getId());
        spaceSet.add(space);
        course.setSpaces(spaceSet);
        courseRepository.save(course);
    }

    @Override
    public void removeSpace(final UUID courseId, final UUID spaceId) {
        var course = getCourse(courseId);
        course.setSpaces(getSpaces(course, spaceId));
        courseRepository.save(course);
    }

    private void setMember(final CourseDto courseDto, final Course course) {
        if (courseDto.getNewMemberId() != null) {
            findMemberReferenceByMemberIdAndStatus(courseDto.getNewMemberId())
                    .ifPresent(course::setMember);
        }
    }

    private Optional<MemberReference> findMemberReferenceByMemberIdAndStatus(final UUID courseDto) {
        return memberReferenceRepository.findOneByIdAndEntityStatus(courseDto, EntityStatus.ACTIVE);
    }

    private void setCoTrainers(final CourseDto courseDto, final Course course) {
        var actualCoTrainers = course.getCoTrainers();
        var newCoTrainers = courseDto.getNewCoTrainers()
                .stream()
                .flatMap(id -> findMemberReferenceByMemberIdAndStatus(id)
                        .map(CoTrainer::new)
                        .stream())
                .collect(Collectors.toSet());
        actualCoTrainers.addAll(newCoTrainers);
        course.setCoTrainers(actualCoTrainers);
    }

    private void setSpaces(final CourseDto courseDto, final Course course) {
        var actualSpaces = courseDto.getSpaces();
        var newSpaces = courseDto.getNewSpaces()
                .stream()
                .flatMap(id -> dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                        .map(Space::new)
                        .stream())
                .collect(Collectors.toSet());
        actualSpaces.addAll(newSpaces);
        course.setSpaces(actualSpaces);
    }

    private Course getCourse(final UUID id) {
        return courseRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> {
                    var msg = "Course with id %s does not exist or it is not active.".formatted(id);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    private void setNewEntityStatus(final Course course, final EntityStatus entityStatus) {
        course.setEntityStatus(entityStatus);
        courseRepository.save(course);
    }
}
