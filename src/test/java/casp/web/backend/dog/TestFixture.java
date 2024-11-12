package casp.web.backend.dog;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.DogHasHandler;

public enum TestFixture {
    ;

    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

    public static DogHasHandler createDogHasHandler() {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setDog(ReferenceTestFixture.createDogReference());
        dogHasHandler.setMember(ReferenceTestFixture.createMemberReference());
        return dogHasHandler;
    }
}
