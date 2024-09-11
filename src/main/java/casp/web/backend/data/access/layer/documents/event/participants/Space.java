package casp.web.backend.data.access.layer.documents.event.participants;

import casp.web.backend.data.access.layer.documents.commons.Payment;
import casp.web.backend.data.access.layer.documents.commons.PaymentConstraint;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.TypeAlias;
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

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
