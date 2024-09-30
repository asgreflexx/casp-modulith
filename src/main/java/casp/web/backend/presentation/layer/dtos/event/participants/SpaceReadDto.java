package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;

public class SpaceReadDto extends BaseSpaceDto {
    private DogHasHandlerDto dogHasHandler;

    private SimpleCourseDto course;

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }

    public SimpleCourseDto getCourse() {
        return course;
    }

    public void setCourse(final SimpleCourseDto course) {
        this.course = course;
    }
}
