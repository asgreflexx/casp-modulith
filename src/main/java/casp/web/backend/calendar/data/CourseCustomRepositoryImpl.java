package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.CoTrainer;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.MemberReference;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
class CourseCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Course> implements CourseCustomRepository {
    private static final QCourse COURSE = QCourse.course;

    @Autowired
    CourseCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public Page<Course> findAllByYear(int year, Pageable pageable) {
        var from = LocalDateTime.of(LocalDate.of(year, 1, 1), LocalTime.MIN);
        var to = LocalDateTime.of(LocalDate.of(year, 12, 31), LocalTime.MAX);

        return query()
                .where(createTimeRangeCriteria(from, to))
                .fetchPage(pageable);
    }

    @Override
    public Page<Course> findAllBySpace(Space space, Pageable pageable) {
        return query()
                .where(COURSE.participants.contains(space), COURSE.entityStatus.eq(EntityStatus.ACTIVE))
                .fetchPage(pageable);
    }

    @Override
    public Stream<Course> findAllBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId) {
        var criteria = createTimeRangeCriteria(from, to);
        if (memberId != null) {
            criteria = criteria.and(COURSE.member.id.eq(memberId)
                    .or(COURSE.coTrainers.contains(mapToCoTrainer(memberId)))
                    .or(COURSE.participants.any().dogHasHandler.in(findDogHasHandlersAndMapToSpaces(memberId))));
        }
        return query()
                .where(criteria)
                .stream();
    }

    private static CoTrainer mapToCoTrainer(UUID memberId) {
        var memberReference = new MemberReference();
        memberReference.setId(memberId);
        return new CoTrainer(memberReference);
    }

    private List<DogHasHandlerReference> findDogHasHandlersAndMapToSpaces(UUID memberId) {
        return findDogHasHandlersByMemberId(memberId).toList();
    }

}
