package com.mengcc.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * json对象操作工具类
 *
 * @author ZhouZQ
 * @create 2019/3/20
 */
public class JacksonUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 日期序列化为long
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 日期序列化支持LocalDateTime LocalDate
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 反序列化时, 忽略不认识的字段, 而不是抛出异常
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    /**
     * 仅读取整个json中的某个属性值,若该属性是一个json对象,则返回空字符串""
     * @param jsonStr
     * @param fieldName
     * @return
     */
    public static String readField(String jsonStr, String fieldName){
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonStr);
            JsonNode node = root.get(fieldName);
            return node == null ? null : node.asText();
        } catch (IOException e) {
            throw new RuntimeException("error reading json field", e);
        }
    }


    /**
     * 将json字符串转换为指定类型的java对象
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T json2pojo(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            throw new RuntimeException("error transfomer json to pojo", e);
        }
    }

    /**
     * 将json字符串转换为指定类型的java对象, 此方法用于目标类包含泛型的java对象.
     * <br/>
     * Example:<br/>
     * <code>    JacksonUtils.json2pojo("...", new TypeReference&lt;PageResult&lt;User&gt;&gt;(){});</code>
     *
     * @param jsonStr
     * @param typeReference
     * @return
     */
    public static <T> T json2pojo(String jsonStr, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("error transfomer json to pojo", e);
        }
    }


    /**
     * 将json字符串转换为HashMap(json里的子对象也将转换为Map)
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> json2map(String jsonStr) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<HashMap<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("error transfomer json to map", e);
        }
    }

    /**
     * 将java对象转换为json字符串
     *
     * @param pojo
     * @return
     */
    public static String pojo2json(Object pojo) {
        try {
            return OBJECT_MAPPER.writeValueAsString(pojo);
        } catch (IOException e) {
            throw new RuntimeException("error transfomer pojo to json", e);
        }
    }

    /**
     * 将java对象转换为map对象
     *
     * @param pojo
     * @return
     */
    public static Map<String, Object> pojo2map(Object pojo) {
        return json2map(pojo2json(pojo));
    }

    /**
     * 将json字符串转换为List<T>
     *
     * @param jsonStr
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T jsonToList(String jsonStr, Class<T> collectionClass, Class<?>... elementClasses) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            T ls = OBJECT_MAPPER.readValue(jsonStr, javaType);
            return ls;
        } catch (IOException e) {
            throw new RuntimeException("error transform to ObjList", e);
        }
    }

}
