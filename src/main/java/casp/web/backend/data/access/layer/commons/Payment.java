package casp.web.backend.data.access.layer.commons;

import java.time.LocalDate;

public interface Payment {
    double getPaidPrice();

    boolean isPaid();

    LocalDate getPaidDate();
}
