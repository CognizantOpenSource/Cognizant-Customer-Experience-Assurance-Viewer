package com.cognizant.workbench.seleniumgrid;

import com.cognizant.workbench.seleniumgrid.beans.Image;
import com.cognizant.workbench.seleniumgrid.services.DockerComposeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest
public class SeleniumGridTest {

    private static final String _PATH = "classpath:images.json";
    private List<Image> images;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void createScriptObject() {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Image.class);

        try {
            File file = ResourceUtils.getFile(_PATH);
            images = objectMapper.readValue(Files.readAllBytes(Path.of(file.getCanonicalPath())), listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doTestPipeline() {
        DockerComposeService dockerComposeService = new DockerComposeService();
        String response = dockerComposeService.generateCompose(images);
        System.out.println(response);
    }

    @Test
    public void getImagesList() {
        try {
            // To Convert the Java Object to String
            System.out.println(objectMapper.writeValueAsString(images));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void destroyScriptObject() {
        images = null;
    }
}
