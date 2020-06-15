package ch.fhnw.ip6.common.util;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

public class JsonUtil {

    public <T> List<T> getJsonAsList(String fileName, Class<T> clazz) {

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            StringWriter writer = new StringWriter();
            IOUtils.copy(Objects.requireNonNull(classLoader.getResourceAsStream(fileName)), writer, "UTF-8");
            String jsonString = writer.toString();
            ObjectMapper mapper = new ObjectMapper();
            Class<?> clz = Class.forName(clazz.getName());
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
            return mapper.readValue(jsonString, type);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
