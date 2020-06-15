package ch.fhnw.ip6.common.util;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class JsonUtil {

    public <T> List<T> getJsonAsList(String fileName, Class<T> clazz) {

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource("classpath:" + fileName)).getFile());
            String jsonString = FileUtils.readFileToString(file, "UTF-8");
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
