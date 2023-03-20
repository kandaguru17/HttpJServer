package com.httpjserver.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.httpjserver.config.HttpJConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class HttpJUtil {

    private HttpJUtil() {

    }

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

    public static String writeValueAsStringFromFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(getFile(filePath)))) {
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
        } catch (IOException ex) {
            throw new HttpJConfigurationException(ex);
        }
        return contentBuilder.toString();
    }

    private static File getFile(String filePath) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(filePath)).getFile());
    }

}
