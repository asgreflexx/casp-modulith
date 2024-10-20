package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.common.enums.BaseParticipantType;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.validation.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Space extends BaseParticipant implements Payment {
    private String note;

    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;

    private boolean isPaid;

    private LocalDate paidDate;

    @Valid
    @NotNull
    @DBRef
    private DogHasHandlerReference dogHasHandler;

    public Space() {
        super(BaseParticipantType.SPACE);
    }

    public Space(final DogHasHandlerReference dogHasHandler) {
        this();
        this.dogHasHandler = dogHasHandler;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerReference dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Space space)) return false;
        return Objects.equals(dogHasHandler, space.dogHasHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dogHasHandler);
    }
}
