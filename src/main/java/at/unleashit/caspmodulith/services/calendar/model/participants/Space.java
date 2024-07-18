package at.unleashit.caspmodulith.services.calendar.model.participants;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@SpacePaymentConstraint
@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(Space.PARTICIPANT_TYPE)
public class Space extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "SPACE";

    private String note;

    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    private double paidPrice = 0D;

    private boolean isPaid = false;

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

    public double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(double paidPrice) {
        this.paidPrice = paidPrice;
    }

    public boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
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
