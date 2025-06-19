package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.CourseRepository;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static casp.web.backend.calendar.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto, Space> implements CourseService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    CourseServiceImpl(CourseRepository courseRepository,
                      MemberReferenceRepository memberReferenceRepository,
                      DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, courseRepository, dogHasHandlerReferenceRepository, migrationService);
        this.courseRepository = courseRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    private static void removeSpace(Course course, UUID spaceId) {
        course.removeSpace(findSpaceById(course, spaceId));
    }

    private static Space findSpaceById(Course course, UUID spaceId) {
        return course
                .getParticipants()
                .stream()
                .filter(s -> s.getId().equals(spaceId))
                .findAny()
                .orElseThrow(() -> {
                    var msg = "Space with id %s not found in course with id %s.".formatted(spaceId, course.getId());
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
    }

    @Override
    public void save(CourseDto dto) {
        var course = COURSE_MAPPER.toSource(dto);

        setCalendarEntriesAndMember(dto, course);
        setCoTrainers(dto, course);
        setParticipants(dto, course);

        courseRepository.save(course);
    }

    @Override
    public CourseDto getOneById(UUID id) {
        return COURSE_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    @Override
    public Page<CourseDto> getAllByYear(int year, Pageable pageable) {
        var coursePage = courseRepository.findAllByYear(year, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    @Override
    public Set<String> getEmailsByCourseId(UUID id) {
        return getOneByIdOrThrowException(id)
                .getParticipants()
                .stream()
                .map(s -> s.getDogHasHandler().getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void updateSpace(UUID courseId, SpaceDto spaceDto) {
        var course = getOneByIdOrThrowException(courseId);
        removeSpace(course, spaceDto.getId());
        course.addSpace(COURSE_MAPPER.toSpace(spaceDto));
        courseRepository.save(course);
    }

    @Override
    public void removeSpace(UUID courseId, UUID spaceId) {
        var course = getOneByIdOrThrowException(courseId);
        removeSpace(course, spaceId);
        courseRepository.save(course);
    }

    @Override
    public Page<CourseDto> getCourseByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable) {
        var space = dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(dogHasHandlerId, EntityStatus.ACTIVE)
                .map(Space::new)
                .orElseThrow(() -> {
                    var msg = "Dog has handler with id %s does not exist or it is not active.".formatted(dogHasHandlerId);
                    LOG.error(msg);
                    return new NoSuchElementException(msg);
                });
        var coursePage = courseRepository.findAllBySpace(space, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    private void setCoTrainers(CourseDto courseDto, Course course) {
        var newCoTrainers = mapToCoTrainers(courseDto.getNewCoTrainers());
        course.addCoTrainers(newCoTrainers);
    }

    private Set<CoTrainer> mapToCoTrainers(Set<UUID> memberIds) {
        return memberIds
                .stream()
                .flatMap(id -> findMemberReferenceById(id)
                        .map(CoTrainer::new)
                        .stream())
                .collect(Collectors.toSet());
    }

    @Override
    Stream<Space> mapToParticipant(final UUID id) {
        return findDogHandlerReferenceById(id)
                .map(Space::new)
                .stream();
    }
}
