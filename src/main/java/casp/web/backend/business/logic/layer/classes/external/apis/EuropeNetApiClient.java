package casp.web.backend.business.logic.layer.classes.external.apis;

import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EuropeNetApiClient {

    private final String EUROPNET_API;

    private static final Logger LOG = LoggerFactory.getLogger(EuropeNetApiClient.class);

    @Autowired
    EuropeNetApiClient(@Value("${casp.europenet-api-endpoint}") String EUROPNET_API) {
        this.EUROPNET_API = EUROPNET_API;
    }

    public EuropeNetState callChipnumbersCheckApi(String chipnumber) {
        RestTemplate restTemplate = new RestTemplate();

        if (chipnumber != null && !chipnumber.isEmpty()) {
            String resourceUrl = EUROPNET_API + "?mn=" + chipnumber;

            LOG.info("Calling Europetnet API for Chipnumber: " + chipnumber);
            ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl, String.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                String body = response.getBody();
                if (body.contains("Der Hund ist registriert")) {
                    LOG.info("Dog with chipnumber: " + chipnumber + " is registered");
                    return EuropeNetState.DOG_IS_REGISTERED;
                } else if (body.contains("Es wurde kein Hund mit diesem Chipcode gefunden.")) {
                    LOG.info("Dog with chipnumber: " + chipnumber + " is not registered");
                    return EuropeNetState.DOG_NOT_REGISTERED;
                }
            } else {
                LOG.info("Europenet API not reachable");
                return EuropeNetState.API_NOT_REACHABLE;
            }
        }
        LOG.info("Chipnumber is null or empty");
        return EuropeNetState.NOT_CHECKED;
    }

}
