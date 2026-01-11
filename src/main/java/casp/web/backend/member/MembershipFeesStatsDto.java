package casp.web.backend.member;

import jakarta.validation.constraints.NotNull;

public record MembershipFeesStatsDto(@NotNull MembershipFeesStatsByYearDto thisYear,
                                     @NotNull MembershipFeesStatsByYearDto lastYear,
                                     @NotNull MembershipFeesStatsByYearDto twoYearsAgo) {
}
