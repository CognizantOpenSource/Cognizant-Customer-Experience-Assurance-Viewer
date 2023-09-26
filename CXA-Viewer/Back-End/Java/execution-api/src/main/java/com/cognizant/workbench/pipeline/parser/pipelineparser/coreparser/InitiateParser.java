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

package com.cognizant.workbench.pipeline.parser.pipelineparser.coreparser;

import com.cognizant.workbench.pipeline.parser.model.*;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsAgent;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsEnvironment;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsPipeline;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStage;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.pipeline.PipelineScript;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
public class InitiateParser {

    private final String pipelineName;
    private final String pipelineVersion;
    private final String pipelinePlatform;
    private final Date creationDate;
    private final PipelineScript pipelineModel;


    public InitiateParser(PipelineScript pipelineModel, String pipelineName,
                          String pipelineVersion, String pipelinePlatform, Date creationDate) {
        this.pipelineModel = pipelineModel;
        this.pipelineName = pipelineName;
        this.pipelineVersion = pipelineVersion;
        this.pipelinePlatform = pipelinePlatform;
        this.creationDate = creationDate;
    }


    public JenkinsPipeline getJenkinsPipeline() {

        log.info("--Pipeline Parsing started--");

        JenkinsPipeline jenkinsPipeline = new JenkinsPipeline();

        jenkinsPipeline.setName(pipelineName);
        jenkinsPipeline.setVersion(pipelineVersion);
        jenkinsPipeline.setPlatform(pipelinePlatform);
        jenkinsPipeline.setCreationDate(creationDate);

        log.info("Basic Pipeline data is been set");

        if (!CollectionUtils.isEmpty(pipelineModel.getAgentInfo())) {
            log.info("Pipeline - Agent Parsing started");
            JenkinsAgent agent = new AgentParser().getAgent(pipelineModel.getAgentInfo());
            jenkinsPipeline.setAgentExists(true);
            jenkinsPipeline.setJenkinsAgent(agent);
            log.info("Pipeline - Agent Parsing completed");
        }

        if (!CollectionUtils.isEmpty(pipelineModel.getEnvironmentInfo())) {
            log.info("Pipeline - Environment Parsing started");
            JenkinsEnvironment jenkinsEnvironment =
                    new EnvironmentParser().getEnvironment(pipelineModel.getEnvironmentInfo());
            jenkinsPipeline.setEnvironment(jenkinsEnvironment);
            jenkinsPipeline.setEnvironmentExists(true);
            log.info("Pipeline - Environment Parsing completed");
        }

        if (!CollectionUtils.isEmpty(pipelineModel.getOptionsInfo())) {
            log.info("Pipeline - Options Parsing started");
            jenkinsPipeline.setOptionsExists(true);
            Options options = new OptionsParser().getOptions(pipelineModel.getOptionsInfo());
            jenkinsPipeline.setOptions(options);
            log.info("Pipeline - Options Parsing Completed");
        }

        if (!CollectionUtils.isEmpty(pipelineModel.getParamsInfo())) {
            log.info("Pipeline - Parameters Parsing started");
            jenkinsPipeline.setParametersExists(true);
            Parameters parameters = new ParameterParser().getParameters(pipelineModel.getParamsInfo());
            jenkinsPipeline.setParameters(parameters);
            log.info("Pipeline - Parameters Parsing Completed");
        }

        if (!CollectionUtils.isEmpty(pipelineModel.getToolsInfo())) {
            log.info("Pipeline - Tools Parsing started");
            jenkinsPipeline.setToolsExists(true);
            Tools tools = new ToolsParser().getTools(pipelineModel.getToolsInfo());
            jenkinsPipeline.setTools(tools);
            log.info("Pipeline - Tools Parsing Completed");
        }

        if (!CollectionUtils.isEmpty(pipelineModel.getTriggerInfo())) {
            log.info("Pipeline - Triggers Parsing started");
            jenkinsPipeline.setTriggersExists(true);
            PipelineTriggers triggers = new TriggersParser().getTriggers(pipelineModel.getTriggerInfo());
            jenkinsPipeline.setTriggers(triggers);
            log.info("Pipeline - Triggers Parsing Completed");
        }

        log.info("Pipeline - Stages Parsing started");
        List<JenkinsStage> stages = new StageParser().getJenkinsStage(pipelineModel.getStagesInfo());
        jenkinsPipeline.setJenkinsStages(stages);
        log.info("Pipeline - Stages Parsing completed");

        if (!CollectionUtils.isEmpty(pipelineModel.getPostInfo())) {
            log.info("Pipeline - Post Parsing started");
            PipelinePost post = new PostParser().getPost(pipelineModel.getPostInfo());
            jenkinsPipeline.setPostExists(true);
            jenkinsPipeline.setPost(post);
            log.info("Pipeline - Post Parsing completed");
        }

        log.info("Pipeline All Data is been Set");

        return jenkinsPipeline;
    }

}
