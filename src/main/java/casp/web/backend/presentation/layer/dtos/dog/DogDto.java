package casp.web.backend.presentation.layer.dtos.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;

import java.util.HashSet;
import java.util.Set;

public class DogDto extends Dog {
    private Set<DogHasHandlerDto> dogHasHandlerSet = new HashSet<>();

    public Set<DogHasHandlerDto> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    public void setDogHasHandlerSet(final Set<DogHasHandlerDto> dogHasHandlerSet) {
        dogHasHandlerSet.forEach(dh -> {
            dh.setDog(null);
            dh.setDogId(null);
        });
        this.dogHasHandlerSet = dogHasHandlerSet;
    }
}
