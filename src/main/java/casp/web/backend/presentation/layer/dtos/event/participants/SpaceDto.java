package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.commons.Payment;
import casp.web.backend.data.access.layer.commons.PaymentConstraint;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@PaymentConstraint
public class SpaceDto extends BaseParticipantDto implements Payment {
    private DogHasHandlerDto dogHasHandler;

    private String note;

    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;

    private boolean isPaid;

    private LocalDate paidDate;

    public SpaceDto() {
        super(Space.PARTICIPANT_TYPE);
    }

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    @Override
    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(final double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(final boolean paid) {
        isPaid = paid;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(final LocalDate paidDate) {
        this.paidDate = paidDate;
    }
}
