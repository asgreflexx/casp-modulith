package casp.web.backend.configuration;

import org.bson.Document;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Configuration
class MongoConfig {
    private static final String FIELD_DATE_TIME = "dateTime";
    private static final String FIELD_OFFSET = "offset";

    @WritingConverter
    static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Document> {
        @Override
        public Document convert(OffsetDateTime source) {
            var document = new Document();
            document.put(FIELD_DATE_TIME, source.toInstant());
            document.put(FIELD_OFFSET, source.getOffset().toString());
            return document;
        }
    }

    @ReadingConverter
    static class OffsetDateTimeReadConverter implements Converter<Document, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Document source) {
            var date = source.getDate(FIELD_DATE_TIME);
            var offset = source.getString(FIELD_OFFSET);
            return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.of(offset));
        }
    }

    @Bean
    MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new OffsetDateTimeWriteConverter(),
                new OffsetDateTimeReadConverter()
        ));
    }

    @Bean
    public ZoneId zoneId(JacksonProperties jacksonProperties) {
        var timeZone = jacksonProperties.getTimeZone();
        if (timeZone == null) {
            throw new IllegalStateException("jacksonProperties.getTimeZone() must not be null");
        }
        return timeZone.toZoneId();
    }
}
