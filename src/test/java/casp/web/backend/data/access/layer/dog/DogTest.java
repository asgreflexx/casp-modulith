package casp.web.backend.data.access.layer.dog;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.commons.BaseDocumentTest;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import casp.web.backend.data.access.layer.enumerations.Gender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

class DogTest extends BaseDocumentTest {
    @Test
    void happyPath() {
        var dog = TestFixture.createDog();

        assertThat(TestFixture.getViolations(dog)).isEmpty();
        baseAssertions(dog);
        assertSame(Gender.FEMALE, dog.getGender());
        assertSame(EuropeNetState.NOT_CHECKED, dog.getEuropeNetState());
    }

}
