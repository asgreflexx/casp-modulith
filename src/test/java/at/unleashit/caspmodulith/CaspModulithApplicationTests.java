package at.unleashit.caspmodulith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class CaspModulithApplicationTests {

    ApplicationModules modules = ApplicationModules
            .of(CaspModulithApplication.class);

    @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeModuleCanvases()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

    @Test
    void verifyArchitecture() {
        ApplicationModules.of(CaspModulithApplication.class).verify();
    }

    @Test
    void contextLoads() {
    }



}
