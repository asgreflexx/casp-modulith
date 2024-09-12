package casp.web.backend.data.access.layer.enumerations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum Role {
    USER,
    ADMIN,
    TRAINER,
    SECRETARY,
    REPRESENTATIVE,
    CASHIER,
    CASH,
    KEY_OWNER;

    public static List<Role> getAllRolesSorted() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing(Enum::name))
                .toList();
    }
}
