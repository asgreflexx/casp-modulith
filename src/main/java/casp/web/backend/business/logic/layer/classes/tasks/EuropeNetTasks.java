package casp.web.backend.business.logic.layer.classes.tasks;

import casp.web.backend.business.logic.layer.classes.DogService;
import casp.web.backend.business.logic.layer.classes.external.apis.EuropeNetApiClient;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;


@Service
class EuropeNetTasks {

    private final DogService dogService;

    private final EuropeNetApiClient apiClient;

    private static final Logger LOG = LoggerFactory.getLogger(EuropeNetTasks.class);

    @Autowired
    EuropeNetTasks(DogService dogService, EuropeNetApiClient apiClient) {
        this.dogService = dogService;
        this.apiClient = apiClient;
    }

    @Scheduled(cron = "0 1 * * * *")
    public void scheduleChipnumbersCheckTask() {
        LOG.info("starting chipnuumbersCheck Task");
        ArrayList<Dog> dogArrayList = (ArrayList<Dog>) dogService.getDogs();

        for (Dog dog :
                dogArrayList) {
            if (dog.getEuropetnetState() == EuropeNetState.NOT_CHECKED && StringUtils.isEmpty(dog.getChipNumber())) {
                EuropeNetState europenetState = apiClient.callChipnumbersCheckApi(dog.getChipNumber());
                dog.setEuropetnetState(europenetState);
                dogService.saveDog(dog);
            }
        }
        LOG.info("stopped chipnumbersCheck Task");
    }
}
