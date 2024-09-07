package casp.web.backend.presentation.layer.dog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

final class MvcMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        MAPPER.setDateFormat(sf);
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private MvcMapper() {
    }

    static <T> T toObject(MvcResult mvcResult, Class<T> clazz) throws JsonProcessingException, UnsupportedEncodingException {
        String value = mvcResult.getResponse().getContentAsString();
        return MAPPER.readValue(value, clazz);
    }

    static <T> T toObject(MvcResult mvcResult, TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        String value = mvcResult.getResponse().getContentAsString();
        return MAPPER.readValue(value, typeReference);
    }

    static String toString(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
