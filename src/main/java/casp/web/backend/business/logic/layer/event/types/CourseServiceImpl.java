package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto> implements CourseService {
    private final CourseRepository courseRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    CourseServiceImpl(final CourseRepository courseRepository,
                      final MemberReferenceRepository memberReferenceRepository,
                      final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository,
                      final BaseEventMigrationService migrationService) {
        super(memberReferenceRepository, courseRepository, migrationService);
        this.courseRepository = courseRepository;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    private static Set<Space> getSpaces(final Course course, final UUID id) {
        return course.getSpaces()
                .stream()
                .filter(s -> !id.equals(s.getId()))
                .collect(Collectors.toSet());
    }

    @Override
    public void save(final CourseDto dto) {
        var course = COURSE_MAPPER.toSource(dto);

        setCalendarEntries(dto, course);
        setMember(dto, course);
        setCoTrainers(dto, course);
        setSpaces(dto, course);

        courseRepository.setMetadataAndSave(course);
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
        var spaceSet = getSpaces(course, space.getId());
        spaceSet.add(space);
        course.setSpaces(spaceSet);
        courseRepository.save(course);
    }

    @Override
    public void removeSpace(final UUID courseId, final UUID spaceId) {
        var course = getOneByIdOrThrowException(courseId);
        course.setSpaces(getSpaces(course, spaceId));
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
                .flatMap(id -> dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                        .map(Space::new)
                        .stream())
                .collect(Collectors.toSet());
        actualSpaces.addAll(newSpaces);
        course.setSpaces(actualSpaces);
    }
}
