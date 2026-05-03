package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.CourseRepository;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto, Space, CourseRepository> implements CourseService {

    @Autowired
    CourseServiceImpl(CourseRepository courseRepository) {
        super(courseRepository);
    }

    @Override
    public void save(CourseDto dto) {
        var course = COURSE_MAPPER.toSource(dto);

        setCalendarEntriesAndMember(dto, course);
        setCoTrainers(dto, course);
        setParticipants(dto, course);

        repository.save(course);
    }

    @Override
    public CourseDto getOneById(UUID id) {
        return COURSE_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    @Override
    public Page<CourseDto> getAllByYear(int year, Pageable pageable) {
        var coursePage = repository.findAllByYear(year, pageable);
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
            var msg = "The course with id %s has been updated in the meantime. The actual version is %d"
                    .formatted(courseId, actualVersion);
            log.error(msg);
            throw new OptimisticLockingFailureException(msg);
        }
        course.setParticipants(COURSE_MAPPER.toSpaces(spaceDtos));
        return COURSE_MAPPER.toTarget(repository.save(course));
    }

    @Override
    public Page<CourseDto> getCoursesByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable) {
        var coursePage = repository.findAllBySpaceId(dogHasHandlerId, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    @Override
    public CoursesFeesStatsDto getCoursesFeesStats() {
        return repository.getCoursesFeesStats();
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
