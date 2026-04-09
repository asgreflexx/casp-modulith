package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.CourseRepository;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static casp.web.backend.calendar.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto, Space> implements CourseService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;

    @Autowired
    CourseServiceImpl(CourseRepository courseRepository,
                      MemberReferenceRepository memberReferenceRepository,
                      DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        super(memberReferenceRepository, courseRepository, dogHasHandlerReferenceRepository);
        this.courseRepository = courseRepository;
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
    public CourseDto updateSpaces(UUID courseId, long courseVersion, Set<SpaceDto> spaceDtos) {
        var course = getOneByIdOrThrowException(courseId);
        var actualVersion = course.getVersion();
        if (actualVersion != courseVersion) {
            var msg = "The course with id %s has been updated in the meantime. The actual version is %d".formatted(courseId, actualVersion);
            LOG.error(msg);
            throw new OptimisticLockingFailureException(msg);
        }
        course.setParticipants(COURSE_MAPPER.toSpaces(spaceDtos));
        return COURSE_MAPPER.toTarget(courseRepository.save(course));
    }

    @Override
    public Page<CourseDto> getCoursesByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable) {
        var coursePage = courseRepository.findAllBySpaceId(dogHasHandlerId, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    @Override
    public CoursesFeesStatsDto getCoursesFeesStats() {
        return courseRepository.getCoursesFeesStats();
    }

    private void setCoTrainers(CourseDto courseDto, Course course) {
        course.addCoTrainers(mapToCoTrainers(courseDto.getCoTrainerIds()));
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
    Stream<Space> mapToParticipant(UUID id) {
        return findDogHandlerReferenceById(id)
                .map(Space::new)
                .stream();
    }
}
