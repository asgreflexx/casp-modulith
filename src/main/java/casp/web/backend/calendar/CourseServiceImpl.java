package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Course;
import casp.web.backend.calendar.data.CourseRepository;
import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                      DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, courseRepository, dogHasHandlerReferenceRepository, migrationService);
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
    public void updateSpaces(UUID courseId, Set<SpaceDto> spaceDtos) {
        var course = getOneByIdOrThrowException(courseId);
        course.setParticipants(COURSE_MAPPER.toSpaces(spaceDtos));
        courseRepository.save(course);
    }

    @Override
    public Page<CourseDto> getCoursesByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable) {
        var dogHasHandlerReference = findDogHandlerReferenceByIdOrThrowException(dogHasHandlerId);
        var coursePage = courseRepository.findAllBySpace(new Space(dogHasHandlerReference), pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
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
    Stream<Space> mapToParticipant(final UUID id) {
        return findDogHandlerReferenceById(id)
                .map(Space::new)
                .stream();
    }
}
