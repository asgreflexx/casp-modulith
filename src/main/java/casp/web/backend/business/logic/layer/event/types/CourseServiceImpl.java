package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
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

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto> implements CourseService {
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

    private static void removeSpace(Course course, UUID spaceId) {
        course.removeSpace(findSpaceById(course, spaceId));
    }

    private static Space findSpaceById(Course course, UUID spaceId) {
        return course
                .getSpaces()
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
        setSpaces(dto, course);

        courseRepository.setMetadataAndSave(course);
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
                .getSpaces()
                .stream()
                .map(s -> s.getDogHasHandler().getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void saveSpace(UUID courseId, Space space) {
        var course = getOneByIdOrThrowException(courseId);
        removeSpace(course, space.getId());
        course.addSpace(space);
        courseRepository.save(course);
    }

    @Override
    public void removeSpace(UUID courseId, UUID spaceId) {
        var course = getOneByIdOrThrowException(courseId);
        removeSpace(course, spaceId);
        courseRepository.save(course);
    }

    private void setCoTrainers(CourseDto courseDto, Course course) {
        var actualCoTrainers = course.getCoTrainers();
        var newCoTrainers = courseDto.getNewCoTrainers()
                .stream()
                .flatMap(id -> findMemberReferenceById(id)
                        .map(CoTrainer::new)
                        .stream())
                .collect(Collectors.toSet());
        actualCoTrainers.addAll(newCoTrainers);
        course.setCoTrainers(actualCoTrainers);
    }

    private void setSpaces(CourseDto courseDto, Course course) {
        var actualSpaces = courseDto.getSpaces();
        var newSpaces = courseDto.getNewSpaces()
                .stream()
                .flatMap(id -> findDogHandlerReferenceById(id)
                        .map(Space::new)
                        .stream())
                .collect(Collectors.toSet());
        actualSpaces.addAll(newSpaces);
        course.setSpaces(actualSpaces);
    }
}
