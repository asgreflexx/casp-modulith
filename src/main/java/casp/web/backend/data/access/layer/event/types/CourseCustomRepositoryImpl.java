package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.data.access.layer.event.participants.Space;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class CourseCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Course> implements CourseCustomRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CourseCustomRepositoryImpl.class);

    private static final QCourse COURSE = QCourse.course;

    @Autowired
    CourseCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    private static BooleanExpression mapToSpaceConstraint(Set<DogHasHandlerReference> dogHasHandlers) {
        BooleanExpression combined = null;
        for (var dhh : dogHasHandlers) {
            combined = combined == null ? createSpaceExpression(dhh) : combined.or(createSpaceExpression(dhh));
        }
        return combined;
    }

    private static BooleanExpression createSpaceExpression(DogHasHandlerReference dhh) {
        return COURSE.spaces.contains(new Space(dhh));
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
    public Set<Course> findAllByDogHasHandlers(Set<DogHasHandlerReference> dogHasHandlers) {
        if (ObjectUtils.isEmpty(dogHasHandlers)) {
            var msg = "The set of DogHasHandlerReference should not be empty.";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return query()
                .where(mapToSpaceConstraint(dogHasHandlers), COURSE.entityStatus.eq(EntityStatus.ACTIVE))
                .stream()
                .collect(Collectors.toSet());
    }
}
