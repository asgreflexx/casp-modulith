package casp.web.backend.calendar;

import jakarta.validation.constraints.NotNull;

public record CoursesFeesStatsDto(@NotNull CoursesFeesStatsByYearDto thisYear,
                                  @NotNull CoursesFeesStatsByYearDto lastYear,
                                  @NotNull CoursesFeesStatsByYearDto twoYearsAgo) {
}
