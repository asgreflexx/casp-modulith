package casp.web.backend.calendar.data.participants;

import casp.web.backend.calendar.presentation.SpaceWriteRequiredFields;
import casp.web.backend.common.reference.DogHasHandlerReference;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.util.UUID;

public class Space extends BaseParticipant implements SpaceWriteRequiredFields {
    private String note;

    private double paidPrice;
    private LocalDate paidDate;

    @DBRef
    private DogHasHandlerReference dogHasHandler;

    public Space() {
        super(BaseParticipantType.SPACE);
    }

    public Space(DogHasHandlerReference dogHasHandler) {
        this();
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public double getPaidPrice() {
        return paidPrice;
    }

    @Override
    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    @Override
    public LocalDate getPaidDate() {
        return paidDate;
    }

    @Override
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    @Override
    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    @Override
    public void setDogHasHandler(DogHasHandlerReference dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public UUID getId() {
        return dogHasHandler.getId();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
