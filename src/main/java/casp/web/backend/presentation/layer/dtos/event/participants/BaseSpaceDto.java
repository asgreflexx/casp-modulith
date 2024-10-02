package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.commons.Payment;
import casp.web.backend.data.access.layer.enumerations.EventResponse;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseSpaceDto implements Payment {
    protected UUID id;
    protected long version;
    protected LocalDateTime created;
    protected LocalDateTime modified;
    @NotNull
    protected UUID memberOrHandlerId;
    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;
    protected String note;
    @PositiveOrZero
    @Digits(integer = 9, fraction = 2)
    protected double paidPrice;
    protected boolean isPaid;
    protected LocalDate paidDate;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }

    public UUID getMemberOrHandlerId() {
        return memberOrHandlerId;
    }

    public void setMemberOrHandlerId(final UUID memberOrHandlerId) {
        this.memberOrHandlerId = memberOrHandlerId;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(final EventResponse response) {
        this.response = response;
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
