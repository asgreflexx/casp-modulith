package casp.web.backend.data.access.layer.documents.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.commons.BaseEntityTest;
import casp.web.backend.data.access.layer.documents.enumerations.EuropeNetState;
import casp.web.backend.data.access.layer.documents.enumerations.Gender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

class DogTest extends BaseEntityTest {
    @Test
    void happyPath() {
        var dog = TestFixture.createValidDog();

        assertThat(TestFixture.getViolations(dog)).isEmpty();
        baseAssertions(dog);
        assertSame(Gender.FEMALE, dog.getGender());
        assertSame(EuropeNetState.NOT_CHECKED, dog.getEuropeNetState());
    }

}
