package com.configservice.config.env;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class EnvPropertySource extends PropertySource<Resource> {

    private final Map<String, String> properties = new HashMap<>();

    public EnvPropertySource(String name, Resource resource) {
        super(name, resource);
        loadProperties(resource);
    }

    private void loadProperties(Resource resource) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (isValidLine(line)) {
                    int separatorIndex = line.indexOf('=');
                    if (separatorIndex != -1) {
                        String key = line.substring(0, separatorIndex).trim();
                        String value = parseValue(line.substring(separatorIndex + 1).trim());
                        properties.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
          
        }
    }

    private boolean isValidLine(String line) {
        return StringUtils.hasText(line) && !line.startsWith("#");
    }

    private String parseValue(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) || 
            (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }
}
