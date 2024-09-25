package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.data.access.layer.commons.Payment;
import casp.web.backend.data.access.layer.commons.PaymentConstraint;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.StringJoiner;

@PaymentConstraint
@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(Space.PARTICIPANT_TYPE)
public class Space extends BaseParticipant implements Payment {
    public static final String PARTICIPANT_TYPE = "SPACE";

    private String note;

    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice;

    private boolean isPaid;

    private LocalDate paidDate;

    @Valid
    @DBRef
    private DogHasHandler dogHasHandler;

    public Space() {
        super(PARTICIPANT_TYPE);
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

    public DogHasHandler getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandler dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Space.class.getSimpleName() + "[", "]")
                .add("note='" + note + "'")
                .add("paidPrice=" + paidPrice)
                .add("isPaid=" + isPaid)
                .add("paidDate=" + paidDate)
                .add("participantType='" + participantType + "'")
                .add("memberOrHandlerId=" + memberOrHandlerId)
                .add("response=" + response)
                .add("baseEvent=" + baseEvent)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
