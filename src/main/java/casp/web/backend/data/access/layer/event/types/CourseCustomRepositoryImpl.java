package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class CourseCustomRepositoryImpl implements CourseCustomRepository {
    private static final QCourse COURSE = QCourse.course;
    private final MongoOperations mongoOperations;

    @Autowired
    CourseCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Page<Course> findAllByYear(final int year, final Pageable pageable) {
        var from = LocalDateTime.of(LocalDate.of(year, 1, 1), LocalTime.MIN);
        var to = LocalDateTime.of(LocalDate.of(year, 12, 31), LocalTime.MAX);

        var expression = COURSE.entityStatus.eq(EntityStatus.ACTIVE)
                .and(COURSE.minTime.goe(from)
                        .and(COURSE.maxTime.loe(to)));
        return query()
                .where(expression)
                .fetchPage(pageable);
    }

    @Override
    public Set<Course> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        var expression = COURSE.entityStatus.ne(EntityStatus.DELETED)
                .and(COURSE.member.id.eq(memberId));
        return getCourseSet(expression);
    }

    @Override
    public Set<Course> findAllByMemberIdAndStatus(final UUID memberId, final EntityStatus status) {
        var expression = COURSE.entityStatus.eq(status)
                .and(COURSE.member.id.eq(memberId));
        return getCourseSet(expression);
    }

    private Set<Course> getCourseSet(final BooleanExpression expression) {
        return query()
                .where(expression)
                .stream()
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<Course> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, Course.class);
    }
}
