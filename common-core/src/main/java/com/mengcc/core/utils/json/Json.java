package com.mengcc.core.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mengcc.core.exceptions.ConversionException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Json {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static final ObjectMapper PRETTY_MAPPER = new ObjectMapper();

    static {
        // Non-standard JSON but we allow C style comments in our JSON
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        MAPPER.registerModule(new JavaTimeModule());

        PRETTY_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        PRETTY_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);

        PRETTY_MAPPER.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        // custom types
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        // he have 2 extensions: RFC-7493
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());

        MAPPER.registerModule(module);
        PRETTY_MAPPER.registerModule(module);
    }

    /**
     * Encode a POJO to JSON using the underlying Jackson mapper.
     *
     * @param obj a POJO
     * @return a String containing the JSON representation of the given POJO.
     * @throws ConversionException if a property cannot be encoded.
     */
    public static String encode(Object obj) throws ConversionException {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ConversionException("转换为json字符串错误", e);
        }
    }

    /**
     * Encode a POJO to JSON with pretty indentation, using the underlying Jackson mapper.
     *
     * @param obj a POJO
     * @return a String containing the JSON representation of the given POJO.
     * @throws ConversionException if a property cannot be encoded.
     */
    public static String encodePrettily(Object obj) throws ConversionException {
        try {
            return PRETTY_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ConversionException("转换为json字符串错误", e);
        }
    }

    /**
     * Decode a given JSON string to a POJO of the given class type.
     * @param str the JSON string.
     * @param clazz the class to map to.
     * @param <T> the generic type.
     * @return an instance of T
     * @throws ConversionException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(String str, Class<T> clazz) throws ConversionException {
        try {
            return MAPPER.readValue(str, clazz);
        } catch (Exception e) {
            throw new ConversionException("json字符串转换为对象错误", e);
        }
    }

    /**
     * Decode a given JSON string to a POJO of the given type.
     * @param str the JSON string.
     * @param type the type to map to.
     * @param <T> the generic type.
     * @return an instance of T
     * @throws ConversionException when there is a parsing or invalid mapping.
     */
    public static <T> T decodeValue(String str, TypeReference<T> type) throws ConversionException {
        try {
            return MAPPER.readValue(str, type);
        } catch (Exception e) {
            throw new ConversionException("json字符串转换为对象错误", e);
        }
    }

    @SuppressWarnings("unchecked")
    static Object checkAndCopy(Object val, boolean copy) {
        if (val == null) {
            // OK
        } else if (val instanceof Number && !(val instanceof BigDecimal)) {
            // OK
        } else if (val instanceof Boolean) {
            // OK
        } else if (val instanceof String) {
            // OK
        } else if (val instanceof Character) {
            // OK
        } else if (val instanceof CharSequence) {
            val = val.toString();
        } else if (val instanceof JsonObject) {
            if (copy) {
                val = ((JsonObject) val).copy();
            }
        } else if (val instanceof JsonArray) {
            if (copy) {
                val = ((JsonArray) val).copy();
            }
        } else if (val instanceof Map) {
            if (copy) {
                val = (new JsonObject((Map)val)).copy();
            } else {
                val = new JsonObject((Map)val);
            }
        } else if (val instanceof List) {
            if (copy) {
                val = (new JsonArray((List)val)).copy();
            } else {
                val = new JsonArray((List)val);
            }
        } else if (val instanceof byte[]) {
            val = Base64.getEncoder().encodeToString((byte[])val);
        } else if (val instanceof Instant) {
            val = ISO_INSTANT.format((Instant) val);
        } else {
            throw new IllegalStateException("无效的JsonObject状态" + val.getClass());
        }
        return val;
    }

    static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static class JsonObjectSerializer extends JsonSerializer<JsonObject> {
        @Override
        public void serialize(JsonObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.getMap());
        }
    }

    private static class JsonArraySerializer extends JsonSerializer<JsonArray> {
        @Override
        public void serialize(JsonArray value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.getList());
        }
    }

    private static class InstantSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(ISO_INSTANT.format(value));
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {
        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(Base64.getEncoder().encodeToString(value));
        }
    }
}
