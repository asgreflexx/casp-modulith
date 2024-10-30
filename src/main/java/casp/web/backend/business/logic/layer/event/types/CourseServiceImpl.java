package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto> implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    CourseServiceImpl(final CourseRepository courseRepository,
                      final MemberReferenceRepository memberReferenceRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      final BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, courseRepository, dogHasHandlerReferenceRepository, migrationService);
        this.courseRepository = courseRepository;
    }

    private static void removeSpace(final Set<Space> spaces, final UUID spaceId) {
        spaces.removeIf(s -> spaceId.equals(s.getId()));
    }

    @Override
    public void save(final CourseDto dto) {
        var course = COURSE_MAPPER.toSource(dto);

        setCalendarEntriesAndMember(dto, course);
        setCoTrainers(dto, course);
        setSpaces(dto, course);

        courseRepository.setMetadataAndSave(course);
    }

    @Override
    public CourseDto getOneByIdAndCalendarEntryId(final UUID id, final UUID calendarEntryId) {
        var courseDto = getOneById(id);
        var calendarEntry = courseDto.getCalendarEntries()
                .stream()
                .filter(ce -> calendarEntryId.equals(ce.getId()))
                .findAny()
                .orElseThrow(() -> {
                    var msg = "The Calendar entry with Id %s not found in course with Id %s".formatted(calendarEntryId, id);
                    return new NoSuchElementException(msg);
                });
        courseDto.setCalendarEntries(List.of(calendarEntry));
        return courseDto;
    }

    @Override
    public CourseDto getOneById(final UUID id) {
        return COURSE_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    @Override
    public Page<CourseDto> getAllByYear(final int year, final Pageable pageable) {
        var coursePage = courseRepository.findAllByYear(year, pageable);
        return COURSE_MAPPER.toTargetPage(coursePage);
    }

    @Override
    public Set<String> getEmailsByCourseId(final UUID id) {
        return getOneByIdOrThrowException(id)
                .getSpaces()
                .stream()
                .map(s -> s.getDogHasHandler().getMember().getEmail())
                .collect(Collectors.toSet());
    }

    @Override
    public void saveSpace(final UUID courseId, final Space space) {
        var course = getOneByIdOrThrowException(courseId);
        var spaces = course.getSpaces();
        removeSpace(spaces, space.getId());
        spaces.add(space);
        course.setSpaces(spaces);
        courseRepository.save(course);
    }

    @Override
    public void removeSpace(final UUID courseId, final UUID spaceId) {
        var course = getOneByIdOrThrowException(courseId);
        removeSpace(course.getSpaces(), spaceId);
        courseRepository.save(course);
    }

    private void setCoTrainers(final CourseDto courseDto, final Course course) {
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

    private void setSpaces(final CourseDto courseDto, final Course course) {
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
