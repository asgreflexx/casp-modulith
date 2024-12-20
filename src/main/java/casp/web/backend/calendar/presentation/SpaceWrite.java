package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.participants.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;

import java.time.LocalDate;
import java.util.Objects;

public class SpaceWrite implements SpaceWriteRequiredFields {
    private DogHasHandlerReference dogHasHandler;
    private String note;
    private double paidPrice;
    private LocalDate paidDate;
    private EventResponse response;

    @Override
    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    @Override
    public void setDogHasHandler(DogHasHandlerReference dogHasHandler) {
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
    public EventResponse getResponse() {
        return response;
    }

    @Override
    public void setResponse(EventResponse response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceWrite that)) return false;
        return Objects.equals(getDogHasHandler().getId(), that.getDogHasHandler().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDogHasHandler().getId());
    }
}
