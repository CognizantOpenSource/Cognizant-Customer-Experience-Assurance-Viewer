package com.cognizant.workbench.pipeline;

import com.cognizant.workbench.pipeline.parser.GenerateJson;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PipelineParserTest {

    @Test
    void recursionTest(){
        String data = "steps{\n" +
                "\t\t\t\t\t\tcheckout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [], userRemoteConfigs: [[credentialsId: 'MyId', url: 'https://code.cognizant.com/784420/leap-api.git']]])\n" +
                "\t\t\t\t\t\tsh 'mvn test -Dtype=owasp-passive'\n" +
                "\t\t\t\t\t\tsh 'mkdir -p test'\n" +
                "\t\t\t\t\t}";
        final Map<Character, Character> closeToOpen = new HashMap<>();
        closeToOpen.put('}', '{');
        boolean balancedNew = PipelineUtil.isBalanced(data, new LinkedList<>(), closeToOpen);
        System.out.println(balancedNew);
    }

    @Test
    void generateTest(){
        Path path = Paths.get("src", "test", "resources", "pipeline-script", "test-pipelines", "ram-test-pipeline.txt");
        String data = "";
        try {
            data = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GenerateJson generateJson = new GenerateJson();
        String jsonString = generateJson.generateJsonString(data);
        System.out.println(jsonString);
    }

}
