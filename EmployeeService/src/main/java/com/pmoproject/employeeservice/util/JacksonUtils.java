package com.pmoproject.employeeservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a utility class containing methods that use Jackson to convert POJO's to JSON and vice-versa
 */
public class JacksonUtils {
    /**
     * Deserializes a JSON object into a POJO
     *
     * @param json JSON object to convert
     * @param deserializingClass the type of class to deserialize the object into
     * @return an instance of the class specified with data from the deserialized JSON object
     * @throws IllegalArgumentException if the JSON object cannot be deserialized into the type of class specified for whatever reason
     */
    public static <T> T convertJsonToObject(Object json, Class<T> deserializingClass) throws IllegalArgumentException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(json, deserializingClass);
    }

    /**
     * Serializes a POJO into a JSON object represented as a String
     * @param object the POJO to serialize into a JSON object
     * @return a serialized JSON object represented as a String
     * @throws JsonProcessingException if the POJO cannot be serialized into a JSON string for whatever reason
     */
    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
