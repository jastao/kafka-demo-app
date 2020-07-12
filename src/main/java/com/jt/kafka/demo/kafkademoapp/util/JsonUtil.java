package com.jt.kafka.demo.kafkademoapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Utility class to convert a POJO to/from JSON format string.
 *
 * Created by Jason Tao on 7/8/2020
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper;

    private static ObjectMapper createObjectMapper() {

        if(Objects.isNull(mapper)) {
            mapper = new ObjectMapper();
        }
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }

    public static String convertToJson(Object object) {

        try {
            return createObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error for converting the object to JSON string: " + e.getMessage());
        }
        return null;
    }

    public static <T> Object convertToObject(String json, Class<T> classType) {

        try {
            return createObjectMapper().readValue(json, classType);
        } catch (JsonProcessingException e) {
            logger.error("Error for converting the JSON string to object: " + e.getMessage());
        }
        return null;
    }
}
