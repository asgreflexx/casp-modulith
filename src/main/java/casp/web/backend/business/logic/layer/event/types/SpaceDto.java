package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class SpaceDto {
    private UUID courseId;
    private String courseName;
    private DogHasHandlerReference dogHasHandler;
    private String note;
    private double paidPrice;
    private LocalDate paidDate;
    private EventResponse response;

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public UUID getId() {
        return dogHasHandler.getId();
    }

    public DogHasHandlerReference getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(DogHasHandlerReference dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
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

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(EventResponse response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceDto spaceDto)) return false;
        return Objects.equals(courseId, spaceDto.courseId) && Objects.equals(getId(), spaceDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, getId());
    }
}
