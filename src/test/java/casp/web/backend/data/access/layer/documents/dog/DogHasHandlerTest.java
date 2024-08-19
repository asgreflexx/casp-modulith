package casp.web.backend.data.access.layer.documents.dog;


import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DogHasHandlerTest extends BaseEntityTest {

    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(UUID.randomUUID());
        dogHasHandler.setMemberId(UUID.randomUUID());
        dogHasHandler.setMember(createValidMember());
        dogHasHandler.setDog(createValidDog());

    }

    @Test
    void happyPath() {
        assertThat(getViolations(dogHasHandler)).isEmpty();
        baseAssertions(dogHasHandler);
        assertNotNull(dogHasHandler.getGrades());
    }

    @Test
    void gradeIsInvalid() {
        dogHasHandler.setGrades(Collections.singleton(new Grade()));

        assertThat(getViolations(dogHasHandler)).isNotEmpty();
    }
}
