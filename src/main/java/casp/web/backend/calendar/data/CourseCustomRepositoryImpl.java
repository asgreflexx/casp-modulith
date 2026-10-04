package casp.web.backend.calendar.data;

import casp.web.backend.calendar.CoursesFeesStatsByYearDto;
import casp.web.backend.calendar.CoursesFeesStatsDto;
import casp.web.backend.calendar.data.participants.QSpace;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.dog.data.QDogHasHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class CourseCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Course> implements CourseCustomRepository {
    private static final QCourse COURSE = QCourse.course;
    private static final QSpace SPACE = QSpace.space;
    private static final String TOTAL_PAID = "totalPaid";
    private static final String AGGREGATION_ID = "_id";
    private final ZoneId zoneId;

    @Autowired
    CourseCustomRepositoryImpl(MongoOperations mongoOperations, ZoneId zoneId) {
        super(mongoOperations);
        this.zoneId = zoneId;
    }

    @Override
    public Page<Course> findAllByYear(int year, Pageable pageable) {
        var from = LocalDateTime.of(LocalDate.of(year, 1, 1), LocalTime.MIN).atZone(zoneId).toOffsetDateTime();
        var to = LocalDateTime.of(LocalDate.of(year, 12, 31), LocalTime.MAX).atZone(zoneId).toOffsetDateTime();

        var timeRangeCriteria = COURSE.entityStatus.eq(EntityStatus.ACTIVE)
                .and(COURSE.minODT.goe(from)
                        .and(COURSE.maxODT.loe(to)));
        return query()
                .where(timeRangeCriteria)
                .fetchPage(pageable);
    }

    @Override
    public Page<Course> findAllBySpaceId(UUID spaceId, Pageable pageable) {
        if (findActiveDogHandlerReferenceById(spaceId).isEmpty()) {
            return Page.empty();
        }
        return query()
                .where(COURSE.participants.any().dogHasHandler.id.eq(spaceId), COURSE.entityStatus.eq(EntityStatus.ACTIVE))
                .fetchPage(pageable);
    }

    // "%s.%s" is a false positive
    @SuppressWarnings("java:S1192")
    @Override
    public CoursesFeesStatsDto getCoursesFeesStats() {
        var thisYear = LocalDate.now(zoneId).getYear();
        var lastYear = thisYear - 1;
        var twoYearsAgo = thisYear - 2;
        var participantsFieldName = COURSE.participants.getMetadata().getName();
        var paidDateFieldName = SPACE.paidDate.getMetadata().getName();
        var paidPriceFieldName = SPACE.paidPrice.getMetadata().getName();
        var dogHasHandlerIdFieldName = "%s.%s.%s".formatted(participantsFieldName,
                SPACE.dogHasHandler.getMetadata().getName(), "$id");
        var dhhInfo = "dhhInfo";
        var dhhEntityStatusFieldName = "%s.%s".formatted(dhhInfo,
                SPACE.dogHasHandler.entityStatus.getMetadata().getName());

        var aggregation = Aggregation.newAggregation(
                Aggregation.unwind(participantsFieldName),
                Aggregation.lookup(QDogHasHandler.dogHasHandler.getMetadata().getName(), dogHasHandlerIdFieldName,
                        AGGREGATION_ID,
                        dhhInfo),
                Aggregation.unwind(dhhInfo),
                Aggregation.match(Criteria.where(COURSE.entityStatus.getMetadata().getName()).is(EntityStatus.ACTIVE.name())
                        .and(dhhEntityStatusFieldName).is(EntityStatus.ACTIVE.name())),
                Aggregation.project()
                        .and("%s.%s".formatted(participantsFieldName, paidDateFieldName)).extractYear().as(paidDateFieldName)
                        .and("%s.%s".formatted(participantsFieldName, paidPriceFieldName)).as(paidPriceFieldName),
                Aggregation.match(Criteria.where(paidDateFieldName).in(twoYearsAgo, lastYear, thisYear)),
                Aggregation.group(paidDateFieldName).sum(paidPriceFieldName).as(TOTAL_PAID)
        );
        var coursesFeesStatsMap = mongoOperations.aggregate(aggregation, Course.class, Map.class)
                .getMappedResults()
                .stream()
                .collect(Collectors.toMap(v -> (Integer) v.get(AGGREGATION_ID),
                        v -> (Double) v.get(TOTAL_PAID)));

        var thisYearStats = generateCoursesFeesStatsByYear(thisYear, coursesFeesStatsMap);
        var lastYearStats = generateCoursesFeesStatsByYear(lastYear, coursesFeesStatsMap);
        var twoYearsAgoStats = generateCoursesFeesStatsByYear(twoYearsAgo, coursesFeesStatsMap);
        return new CoursesFeesStatsDto(thisYearStats, lastYearStats, twoYearsAgoStats);
    }

    private CoursesFeesStatsByYearDto generateCoursesFeesStatsByYear(int year, Map<Integer, Double> coursesFeesStatsMap) {
        return new CoursesFeesStatsByYearDto(year, coursesFeesStatsMap.getOrDefault(year, 0.0));
    }
}
