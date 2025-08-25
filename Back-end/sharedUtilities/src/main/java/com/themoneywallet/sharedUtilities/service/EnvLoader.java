package com.themoneywallet.sharedUtilities.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {

    public static void load(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }
        props.forEach((k, v) -> System.setProperty(k.toString(), v.toString()));
    }
}
