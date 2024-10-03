package casp.web.backend.business.logic.layer.dog;


import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EuropeNetTasksTest {
    private static final String EURO_PET_NET_API = "euroPetNetApi";
    private static final Map<String, String> URI_VARIABLES = Map.of("chipNumber", "chipNumber");
    private static final String DOG_IS_REGISTERED = "Der Hund ist registriert";
    private static final String DOG_NOT_REGISTERED = "Es wurde kein Hund mit diesem Chipcode gefunden";
    @Mock
    private DogService dogService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private ResponseEntity<String> responseEntity;

    private Dog dog;

    private EuropeNetTasks europeNetTasks;

    @BeforeEach
    void setUp() {
        dog = spy(new Dog());
        dog.setChipNumber("chipNumber");
        var dogPage = new PageImpl<>(List.of(dog));
        when(dogService.getDogsThatWereNotChecked(null)).thenReturn(dogPage);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        europeNetTasks = new EuropeNetTasks(dogService, restTemplateBuilder, EURO_PET_NET_API);
    }

    @Nested
    class ScheduleChipNumbersCheckTask {
        @Test
        void responseStatusIsNotOk() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dog).setEuropeNetState(EuropeNetState.API_NOT_REACHABLE);
        }

        @Test
        void responseBodyIsEmpty() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn("");
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dog).setEuropeNetState(EuropeNetState.NOT_CHECKED);
        }

        @Test
        void responseContainsDogIsRegistered() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn(DOG_IS_REGISTERED);
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dog).setEuropeNetState(EuropeNetState.DOG_IS_REGISTERED);
        }

        @Test
        void responseContainsDogIsNotRegistered() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn(DOG_NOT_REGISTERED);
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dog).setEuropeNetState(EuropeNetState.DOG_NOT_REGISTERED);
        }

        @Test
        void responseBodyIsUnknown() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn("XXXXXXXX");
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dog).setEuropeNetState(EuropeNetState.NOT_CHECKED);
        }

        @Test
        void newStateWasSaved() {
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn(DOG_IS_REGISTERED);
            when(restTemplate.getForEntity(EURO_PET_NET_API, String.class, URI_VARIABLES)).thenReturn(responseEntity);

            europeNetTasks.scheduleChipNumbersCheckTask();

            verify(dogService).saveDog(dog);
        }
    }
}
