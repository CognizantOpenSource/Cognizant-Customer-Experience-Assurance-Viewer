/*
 *
 *   Copyright (C) 2023 - Cognizant Technology Solutions
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.cognizant.workbench.pipeline.parser;

import com.cognizant.workbench.pipeline.parser.converter.PipelineConverter;
import com.cognizant.workbench.pipeline.parser.model.JenkinsPipelineParser;
import com.cognizant.workbench.pipeline.parser.pipelineparser.categorization.CategorizeStage;
import com.cognizant.workbench.pipeline.parser.pipelineparser.coreparser.InitiateParser;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsPipeline;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.pipeline.PipelineScript;
import com.cognizant.workbench.pipeline.parser.utils.TemplateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GenerateJson {

    public String generateJsonString(String pipelineData) {
        log.info("/n/n/n-----------Pipeline Parsing started---------");
        String pipelineName = "Import-pipeline";
        String pipelineVersion = "2.0";
        String pipelinePlatform = "windows";

        CategorizeStage categorizeStage = new CategorizeStage();
        categorizeStage.initiateCategorization(pipelineData);
        PipelineScript pipelineModel = categorizeStage.getPipelineModel();

        InitiateParser initiateParser = new InitiateParser(pipelineModel, pipelineName, pipelineVersion, pipelinePlatform, new Date());
        JenkinsPipeline jenkinsPipeline = initiateParser.getJenkinsPipeline();

        TemplateManager templateManager = new TemplateManager();
        PipelineConverter pipelineConverter = new PipelineConverter(jenkinsPipeline);
        JenkinsPipelineParser pipeline = pipelineConverter.getPipeline();
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("pipeline", pipeline);
        String response = templateManager.processTemplate("pipeline", templateData);

        log.info("-----------Pipeline parsing to JSON completed---------");
        log.info("------Pipeline JSON-----");
        log.info(response);
        if (!pipelineConverter.getUnhandledScript().equals(" ")) {
            log.info("------Unhandled Pipeline Script-----");
            log.info(pipelineConverter.getUnhandledScript());
        }
        return response;
    }
}
