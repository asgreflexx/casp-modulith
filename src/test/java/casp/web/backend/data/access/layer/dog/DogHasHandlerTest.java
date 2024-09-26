package casp.web.backend.data.access.layer.dog;


import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DogHasHandlerTest extends BaseDocumentTest {

    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        dogHasHandler = TestFixture.createDogHasHandler();
    }

    @Test
    void happyPath() {
        assertThat(TestFixture.getViolations(dogHasHandler)).isEmpty();
        baseAssertions(dogHasHandler);
        assertNotNull(dogHasHandler.getGrades());
    }

    @Test
    void gradeIsInvalid() {
        dogHasHandler.setGrades(Collections.singleton(new Grade()));

        assertThat(TestFixture.getViolations(dogHasHandler)).isNotEmpty();
    }
}
