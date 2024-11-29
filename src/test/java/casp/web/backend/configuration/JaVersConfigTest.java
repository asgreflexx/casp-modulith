package casp.web.backend.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JaVersConfigTest {

    @Test
    void shouldReturnSystemAsAuthorProvider() {
        var jaVersConfig = new JaVersConfig();

        var authorProvider = jaVersConfig.authorProvider();

        assertEquals("SYSTEM", authorProvider.provide());
    }
}
