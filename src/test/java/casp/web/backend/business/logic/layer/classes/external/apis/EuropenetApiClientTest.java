package casp.web.backend.business.logic.layer.classes.external.apis;

import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Disabled until the EuropeNetApiClient #2 has been updated!")
@SpringBootTest
class EuropenetApiClientTest {

    @Autowired
    private EuropeNetApiClient apiClient;

    @Test
    void testCallChipnumbersCheckApi_DogNotRegistered() {
        EuropeNetState europenetState = apiClient.callChipnumbersCheckApi("12111121212");
        assertEquals(europenetState, EuropeNetState.DOG_NOT_REGISTERED);
    }

    @Test
    void testCallChipnumbersCheckApi_DogRegistered() {
        EuropeNetState europenetState = apiClient.callChipnumbersCheckApi("900182001441396");
        assertEquals(europenetState, EuropeNetState.DOG_IS_REGISTERED);
    }
}
