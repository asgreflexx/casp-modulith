package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Service
class EuropeNetTasksImpl implements EuropeNetTasks {
    private static final Logger LOG = LoggerFactory.getLogger(EuropeNetTasksImpl.class);
    private static final String DOG_IS_REGISTERED = "Der Hund ist registriert";
    private static final String DOG_NOT_REGISTERED = "Es wurde kein Hund mit diesem Chipcode gefunden";
    private final DogService dogService;
    private final String euroPetNetApi;
    private final RestTemplate restTemplate;

    @Autowired
    EuropeNetTasksImpl(final DogService dogService,
                       final RestTemplateBuilder restTemplateBuilder,
                       final @Value("${casp.europenet-api-endpoint}") String euroPetNetApi) {
        this.dogService = dogService;
        this.euroPetNetApi = euroPetNetApi;
        restTemplate = restTemplateBuilder.build();
    }

    private static EuropeNetState getNotCheckStatusBecauseOfUnexpectedResponse(final String body) {
        LOG.info("Unexpected response from EuroPetNet API: {}", body);
        return EuropeNetState.NOT_CHECKED;
    }

    @Override
    public Page<Dog> registerDogsManually(final Pageable pageRequest) {
        var dogPage = dogService.getDogsThatWereNotChecked(pageRequest);
        registerDogs(dogPage);
        return dogPage;
    }

    @Scheduled(cron = "${casp.cron}")
    void scheduleChipNumbersCheckTask() {
        var dogPage = dogService.getDogsThatWereNotChecked(null);
        registerDogs(dogPage);
    }

    private void registerDogs(final Page<Dog> dogPage) {
        if (dogPage.isEmpty()) {
            LOG.info("No dogs to check");
            return;
        }

        LOG.info("Start chip number check task. Amount of chips to check: {}", dogPage.getNumberOfElements());
        dogPage.forEach(dog -> {
            dog.setEuropeNetState(callChipNumberCheckApi(dog.getChipNumber()));
            dogService.saveDog(dog);
        });
        LOG.info("Chip number check task has ended");
    }

    private EuropeNetState callChipNumberCheckApi(String chipNumber) {
        LOG.info("Calling EuroPetNet API for chipNumber: {}", chipNumber);
        try {
            var response = restTemplate.getForEntity(euroPetNetApi, String.class, Map.of("chipNumber", chipNumber));
            if (HttpStatus.OK != response.getStatusCode()) {
                LOG.warn("EuroPetNet API not reachable. Status code:{},\n{}", response.getStatusCode(), response.getBody());
                return EuropeNetState.API_NOT_REACHABLE;
            }
            var body = response.getBody();
            // simpler, than response.hasBody and afterward assert body != null
            if (!ObjectUtils.isEmpty(body)) {
                if (body.contains(DOG_IS_REGISTERED)) {
                    LOG.info("Dog with chipNumber: {} is registered", chipNumber);
                    return EuropeNetState.DOG_IS_REGISTERED;
                } else if (body.contains(DOG_NOT_REGISTERED)) {
                    LOG.info("Dog with chipNumber: {} is not registered", chipNumber);
                    return EuropeNetState.DOG_NOT_REGISTERED;
                } else {
                    return getNotCheckStatusBecauseOfUnexpectedResponse(body);
                }
            } else {
                return getNotCheckStatusBecauseOfUnexpectedResponse(body);
            }
        } catch (RestClientException e) {
            LOG.warn("EuroPetNet API not reachable", e);
            return EuropeNetState.API_NOT_REACHABLE;
        }
    }
}
