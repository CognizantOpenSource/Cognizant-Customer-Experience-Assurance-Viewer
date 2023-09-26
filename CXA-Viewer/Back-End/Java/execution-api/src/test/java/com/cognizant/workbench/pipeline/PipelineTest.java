package com.cognizant.workbench.pipeline;

import com.cognizant.workbench.pipeline.model.PipelineConfigBean;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.pipelineconverter.PipelineString;
import com.cognizant.workbench.pipeline.service.PipelineService;
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

@SpringBootTest
public class PipelineTest {

    private static final String _PATH = "classpath:Jenkinsfile.json";
    private Project project;

    @BeforeEach
    public void createScriptObject() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = ResourceUtils.getFile(_PATH);
            project = objectMapper.readValue(Files.readAllBytes(Path.of(file.getCanonicalPath())), Project.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doTestPipeline() {
        PipelineService pipelineService = new PipelineService();
        PipelineConfigBean configBean = new PipelineConfigBean("jenkins", "2.0", "Win");
        PipelineString pipelineString = pipelineService.generatePipeline(project, configBean);
        System.out.println(pipelineString.getPipeline());
    }

    @AfterEach
    public void destroyScriptObject() {
        project = null;
    }
}
