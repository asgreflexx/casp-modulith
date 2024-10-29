package casp.web.backend.data.access.layer.event.types;

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
    CourseCustomRepositoryImpl(final MongoOperations mongoOperations) {
        super(mongoOperations);
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
}
