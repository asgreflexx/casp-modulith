package casp.web.backend.dog;

import casp.web.backend.dog.data.EuropeNetState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
class EuropeNetTasksImpl implements EuropeNetTasks {
    private static final String DOG_IS_REGISTERED = "Der Hund ist registriert";
    private static final String DOG_NOT_REGISTERED = "Es wurde kein Hund mit diesem Chipcode gefunden";
    private final DogService dogService;
    private final String euroPetNetApi;
    private final RestTemplate restTemplate;

    @Autowired
    EuropeNetTasksImpl(DogService dogService,
                       RestTemplateBuilder restTemplateBuilder,
                       @Value("${casp.europenet-api-endpoint}") String euroPetNetApi) {
        this.dogService = dogService;
        this.euroPetNetApi = euroPetNetApi;
        restTemplate = restTemplateBuilder.build();
    }

    private static EuropeNetState getNotCheckStatusBecauseOfUnexpectedResponse(String body) {
        log.info("Unexpected response from EuroPetNet API: {}", body);
        return EuropeNetState.NOT_CHECKED;
    }

    private static EuropeNetState evaluateResponseBody(String chipNumber, String body) {
        // simpler, than response.hasBody and afterward assert body != null
        if (!ObjectUtils.isEmpty(body)) {
            if (body.contains(DOG_IS_REGISTERED)) {
                log.info("Dog with chipNumber: {} is registered", chipNumber);
                return EuropeNetState.DOG_IS_REGISTERED;
            } else if (body.contains(DOG_NOT_REGISTERED)) {
                log.info("Dog with chipNumber: {} is not registered", chipNumber);
                return EuropeNetState.DOG_NOT_REGISTERED;
            } else {
                return getNotCheckStatusBecauseOfUnexpectedResponse(body);
            }
        } else {
            return getNotCheckStatusBecauseOfUnexpectedResponse(body);
        }
    }

    @Override
    public Page<DogDto> registerDogsManually(Pageable pageRequest) {
        var dogPage = dogService.getDogsThatWereNotChecked(pageRequest);
        registerDogs(dogPage);
        return dogPage;
    }

    @Scheduled(cron = "${casp.cron}")
    void scheduleChipNumbersCheckTask() {
        var dogPage = dogService.getDogsThatWereNotChecked(Pageable.unpaged());
        registerDogs(dogPage);
    }

    private void registerDogs(Page<DogDto> dogPage) {
        if (dogPage.isEmpty()) {
            log.info("No dogs to check");
            return;
        }

        log.info("Start chip number check task. Amount of chips to check: {}", dogPage.getNumberOfElements());
        dogPage.forEach(dog -> {
            dog.setEuropeNetState(callChipNumberCheckApi(dog.getChipNumber()));
            dogService.saveDog(dog);
        });
        log.info("Chip number check task has ended");
    }

    private EuropeNetState callChipNumberCheckApi(String chipNumber) {
        log.info("Calling EuroPetNet API for chipNumber: {}", chipNumber);
        try {
            var response = restTemplate.getForEntity(euroPetNetApi, String.class, Map.of("chipNumber", chipNumber));
            if (HttpStatus.OK != response.getStatusCode()) {
                log.warn("EuroPetNet API not reachable. Status code:{},\n{}", response.getStatusCode(), response.getBody());
                return EuropeNetState.API_NOT_REACHABLE;
            }
            return evaluateResponseBody(chipNumber, response.getBody());
        } catch (RestClientException e) {
            log.warn("EuroPetNet API not reachable", e);
            return EuropeNetState.API_NOT_REACHABLE;
        }
    }
}
