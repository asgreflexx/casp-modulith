package casp.web.backend.member.data;

import jakarta.validation.constraints.NotNull;

public record MembershipFeesStats(@NotNull MembershipFeesStatsByYear thisYear,
                                  @NotNull MembershipFeesStatsByYear lastYear,
                                  @NotNull MembershipFeesStatsByYear twoYearsAgo) {
}
