package casp.web.backend.dog;

import casp.web.backend.dog.data.Dog;

public enum TestFixture {
    ;

    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }
}
