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

package com.cognizant.workbench.pipeline.parser.converter;

import com.cognizant.workbench.pipeline.parser.model.JenkinsPipelineParser;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsPipeline;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PipelineConverter {

    private final JenkinsPipeline jenkinsPipeline;

    public PipelineConverter(JenkinsPipeline jenkinsPipeline) {
        this.jenkinsPipeline = jenkinsPipeline;
    }

    private final JenkinsPipelineParser pipeline = new JenkinsPipelineParser();

    public JenkinsPipelineParser getPipeline() {
        generatePipeline();
        return pipeline;
    }

    public void generatePipeline() {

        log.info("Setting Pipeline basic Data");

        setPipelineData(jenkinsPipeline.getName(), jenkinsPipeline.getPlatform(), jenkinsPipeline.getVersion(),
                jenkinsPipeline.getCreationDate());

        if (jenkinsPipeline.isAgentExists()) {
            log.info("Setting Pipeline Agent ftl Data");
            pipeline.setAgent_ftl(getAgentFtl());
            log.info("Setting Pipeline Agent ftl Data completed");
        }

        if (jenkinsPipeline.isEnvironmentExists()) {
            log.info("Setting Pipeline Environment ftl Data");
            pipeline.setEnvironment_ftl(getEnvironmentFtl());
            log.info("Setting Pipeline Environment ftl Data completed");
        }

        if (jenkinsPipeline.isOptionsExists()) {
            log.info("Setting Pipeline Options ftl Data");
            pipeline.setOptions_ftl(getOptionsFtl());
            log.info("Setting Pipeline Options ftl Data Completed");
        }

        if (jenkinsPipeline.isParametersExists()) {
            log.info("Setting Pipeline Parameters ftl Data");
            pipeline.setParameters_ftl(getParametersFtl());
            log.info("Setting Pipeline Parameters ftl Data Completed");
        }

        if (jenkinsPipeline.isToolsExists()) {
            log.info("Tools Pipeline Parameters ftl Data");
            pipeline.setTools_ftl(getToolsFtl());
            log.info("Tools Pipeline Parameters ftl Data Completed");
        }

        if (jenkinsPipeline.isToolsExists()) {
            log.info("Triggers Pipeline Parameters ftl Data");
            pipeline.setTriggers_ftl(getTriggersFtl());
            log.info("Triggers Pipeline Parameters ftl Data Completed");
        }

        log.info("Setting Pipeline Stages ftl Data");
        pipeline.setStages_ftl(getStagesFtl());
        log.info("Setting Pipeline Stages ftl Data completed");

        if (jenkinsPipeline.isPostExists()) {
            log.info("Setting Pipeline Post ftl Data");
            pipeline.setPost_ftl(getPostFtl());
            log.info("Setting Pipeline Post ftl Data completed");
        }
    }

    private void setPipelineData(String name, String platform, String version, Date creationDate) {

        pipeline.setName(name);
        pipeline.setPlatform(platform);
        pipeline.setVersion(version);
        pipeline.setCreationDate(creationDate);
    }

    private String getAgentFtl() {
        pipeline.setIsAgent(jenkinsPipeline.isAgentExists());
        AgentConverter agentConverter = new AgentConverter(jenkinsPipeline.getJenkinsAgent());
        return agentConverter.getAgentFtl();
    }

    private String getEnvironmentFtl() {
        pipeline.setIsEnvironment(jenkinsPipeline.isEnvironmentExists());
        EnvironmentConverter environmentConverter = new EnvironmentConverter(jenkinsPipeline.getEnvironment());
        return environmentConverter.getEnvironmentFtl();
    }

    private String getStagesFtl() {

        StageConverter stageConverter = new StageConverter(jenkinsPipeline.getJenkinsStages());
        return stageConverter.getStageFtl();
    }

    private String getPostFtl() {
        pipeline.setIsPost(jenkinsPipeline.isPostExists());
        PostConverter postConverter = new PostConverter(jenkinsPipeline.getPost());
        return postConverter.getPostFtl();
    }

    private String getOptionsFtl() {
        pipeline.setIsOptions(jenkinsPipeline.isOptionsExists());
        OptionsConverter optionsConverter = new OptionsConverter(jenkinsPipeline.getOptions());
        return optionsConverter.getOptionsFtl();
    }

    private String getParametersFtl() {
        pipeline.setIsParameters(jenkinsPipeline.isParametersExists());
        ParameterConverter parameterConverter = new ParameterConverter(jenkinsPipeline.getParameters());
        return parameterConverter.getParametersFtl();
    }

    private String getToolsFtl() {
        pipeline.setIsTools(jenkinsPipeline.isToolsExists());
        ToolsConverter toolsConverter = new ToolsConverter(jenkinsPipeline.getTools());
        return toolsConverter.getToolsFtl();
    }

    private String getTriggersFtl() {
        pipeline.setIsTriggers(jenkinsPipeline.isTriggersExists());
        TriggersConverter triggersConverter = new TriggersConverter(jenkinsPipeline.getTriggers());
        return triggersConverter.getTriggersFtl();
    }

    public String getUnhandledScript() {
        String data = " ";
        List<String> unhandledData = new ArrayList<>();
        try {
            List<List<String>> lists = new ArrayList<>();
            lists.add(jenkinsPipeline.getEnvironment().getEnvironmentUnhandled());
            lists.add(jenkinsPipeline.getOptions().getOptionUnhandled());
            lists.add(jenkinsPipeline.getParameters().getParametersUnhandled());

            unhandledData.addAll(getUnhandledValues(lists));

            for (JenkinsStage jenkinsStage : jenkinsPipeline.getJenkinsStages()) {
                if (jenkinsStage.getStageUnhandled().size() > 1) {

                    //Logic to handle When, Input, Steps etc., - Change required
                    unhandledData.addAll(jenkinsStage.getStageUnhandled());
                    if (jenkinsStage.getStageData().getJenkinsEnvironment().getEnvironmentUnhandled() != null &&
                            jenkinsStage.getStageData().getJenkinsEnvironment().getEnvironmentUnhandled().size() > 1) {
                        unhandledData.addAll(jenkinsStage.getStageData().getJenkinsEnvironment().getEnvironmentUnhandled());
                    }

                    if (jenkinsStage.getStageData().getAgent().getAgentUnhandled() != null &&
                            jenkinsStage.getStageData().getAgent().getAgentUnhandled().size() > 1) {
                        unhandledData.addAll(jenkinsStage.getStageData().getAgent().getAgentUnhandled());
                    }
                }
            }

            if (jenkinsPipeline.getPost().getPostUnhandled() != null &&
                    jenkinsPipeline.getPost().getPostUnhandled().size() > 1) {
                unhandledData.addAll(jenkinsPipeline.getPost().getPostUnhandled());
            }

            if (unhandledData.size() > 1) {
                data = unhandledData.stream().map(Object::toString)
                        .collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return data;
    }

    private List<String> getUnhandledValues(List<List<String>> lists) {
        List<String> list = new ArrayList<>();
        lists.forEach(strings -> {
            if (strings != null && strings.size() > 1) {
                list.addAll(jenkinsPipeline.getEnvironment().getEnvironmentUnhandled());
            }
        });
        return list;
    }

}
