package casp.web.backend.calendar.data;

import jakarta.validation.constraints.NotNull;

public record CoursesFeesStats(@NotNull CoursesFeesStatsByYear thisYear,
                               @NotNull CoursesFeesStatsByYear lastYear,
                               @NotNull CoursesFeesStatsByYear twoYearsAgo) {
}
