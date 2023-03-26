package com.httpjserver.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.httpjserver.config.HttpJConfigurationException;

import java.io.File;
import java.util.Objects;

/**
 * HttpJUtil class containing the helper methods
 */
public class HttpJUtil {

    private HttpJUtil() {

    }

    /**
     * parse the yaml file and deserialize it to a POJO.
     *
     * @param filePath location of the yaml file
     * @param type     the Deserialized type
     * @param <T>      the generic class/type
     * @return instance of the Deserialized type
     */
    public static <T> T readValueFromFile(String filePath, Class<T> type) {
        try {
            final File file = getFile(filePath);
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(file, type);
        } catch (Exception ex) {
            throw new HttpJConfigurationException(ex);
        }
    }

    private static File getFile(String filePath) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(filePath)).getFile());
    }

}
