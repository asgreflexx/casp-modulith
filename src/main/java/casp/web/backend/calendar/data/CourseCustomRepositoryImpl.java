package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

        var expression = COURSE.entityStatus.eq(EntityStatus.ACTIVE)
                .and(COURSE.minTime.goe(from)
                        .and(COURSE.maxTime.loe(to)));
        return query()
                .where(expression)
                .fetchPage(pageable);
    }

    @Override
    public Page<Course> findAllBySpace(Space space, Pageable pageable) {
        return query()
                .where(COURSE.spaces.contains(space), COURSE.entityStatus.eq(EntityStatus.ACTIVE))
                .fetchPage(pageable);
    }
}
