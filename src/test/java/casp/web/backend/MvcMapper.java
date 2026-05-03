package casp.web.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public final class MvcMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        var sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        MAPPER.setDateFormat(sf);
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private MvcMapper() {
    }

    public static <T> T toObject(MvcResult mvcResult, Class<T> clazz) throws JsonProcessingException, UnsupportedEncodingException {
        var value = mvcResult.getResponse().getContentAsString();
        return MAPPER.readValue(value, clazz);
    }

    public static String toString(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
