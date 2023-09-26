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

import com.cognizant.workbench.pipeline.parser.model.PipelineStage;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.*;
import com.cognizant.workbench.pipeline.parser.utils.TemplateManager;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StageConverter {

    private final List<JenkinsStage> jenkinsStages;

    public StageConverter(List<JenkinsStage> jenkinsStages) {
        this.jenkinsStages = jenkinsStages;
    }

    public String getStageFtl() {
        log.info("Preparing Pipeline Stage ftl data");
        TemplateManager templateManager = new TemplateManager();
        Map<String, Object> templateData = new HashMap<>();
        List<PipelineStage> stages = getStages();
        templateData.put("stages", stages);
        templateData.put("config", getPipelineConfigFtl());
        String templateStr = templateManager.processTemplate("stage", templateData);
        log.info("Completed Preparing Pipeline Stage ftl data");
        return templateStr;
    }

    public List<PipelineStage> getStages() {

        List<PipelineStage> stages = new ArrayList<>();

        for (JenkinsStage jenkinsStage : jenkinsStages) {
            stages.add(addStageData(jenkinsStage));
        }
        return stages;
    }

    public PipelineStage addStageData(JenkinsStage jenkinsStage) {

        log.info("Setting Pipeline Stage Pojo "+jenkinsStage.getId());
        PipelineStage stage = new PipelineStage();

        stage.setId(jenkinsStage.getId());
        stage.setType(jenkinsStage.getType());
        stage.setDescription(jenkinsStage.getDescription());
        stage.setToolId(jenkinsStage.getToolId());
        stage.setParallel(jenkinsStage.isParallel());
        stage.setChildStage(jenkinsStage.isParallel());

        if (jenkinsStage.isParallel()) {
            List<PipelineStage> childStages = handleParallelStages(jenkinsStage.getJenkinsStages());
            stage.setChildStageDate(getChildStagesFtl(childStages));
        }

        stage.setStep(jenkinsStage.getStageData().isStep());
        stage.setIsEnvironment(jenkinsStage.getStageData().getIsEnvironment());
        stage.setIsInput(jenkinsStage.getStageData().isInput());
        stage.setIsOptions(jenkinsStage.getStageData().isOptions());
        stage.setIsWhen(jenkinsStage.getStageData().isWhen());
        stage.setAgent(jenkinsStage.getStageData().getIsAgent());
        stage.setSource(jenkinsStage.isSource());

        String stageData = getStageDataJson(jenkinsStage.getStageData());
        stage.setStageData(stageData);

        if (jenkinsStage.getStageData().isStep()) {
            stage.setStep_ftl(getSteps_ftl(jenkinsStage.getStageData().getSteps()));
        }

        if (jenkinsStage.getStageData().getIsEnvironment()) {
            stage.setEnvironment_ftl(
                    getEnvironment_ftl(jenkinsStage.getStageData().getJenkinsEnvironment()));
        }

        if (jenkinsStage.getStageData().isInput()) {
            stage.setInput_ftl(jenkinsStage.getStageData().getInputData());
        }

        if (jenkinsStage.getStageData().isOptions()) {
            stage.setOptions_ftl(jenkinsStage.getStageData().getOptionsData());
        }

        if (jenkinsStage.getStageData().isWhen()) {
            stage.setWhen_ftl(jenkinsStage.getStageData().getWhenData());
        }

        if (jenkinsStage.getStageData().getIsAgent()) {
            stage.setAgent_ftl(getAgent_ftl(jenkinsStage.getStageData().getAgent()));
        }

        if (jenkinsStage.isSource()) {
            stage.setSource_ftl(getSource_ftl(jenkinsStage.getJenkinsSource()));
        }

        log.info("Completed Setting Pipeline Stage Pojo "+jenkinsStage.getId());

        return stage;
    }

    private List<PipelineStage> handleParallelStages(List<JenkinsStage> jenkinsStages) {

        List<PipelineStage> childStages = new ArrayList<>();

        for (JenkinsStage jenkinsStage : jenkinsStages) {
            PipelineStage stage = new PipelineStage();
            log.info("Setting Pipeline Child Stage Pojo "+jenkinsStage.getId());
            stage.setId(jenkinsStage.getId());
            stage.setType(jenkinsStage.getType());
            stage.setDescription(jenkinsStage.getDescription());
            stage.setToolId(jenkinsStage.getToolId());
            stage.setParallel(jenkinsStage.isParallel());
            stage.setChildStage(jenkinsStage.isParallel());

            stage.setStep(jenkinsStage.getStageData().isStep());
            stage.setIsEnvironment(jenkinsStage.getStageData().getIsEnvironment());
            stage.setIsInput(jenkinsStage.getStageData().isInput());
            stage.setIsOptions(jenkinsStage.getStageData().isOptions());
            stage.setIsWhen(jenkinsStage.getStageData().isWhen());
            stage.setAgent(jenkinsStage.getStageData().getIsAgent());
            stage.setSource(jenkinsStage.isSource());

            String stageData = getStageDataJson(jenkinsStage.getStageData());
            stage.setStageData(stageData);

            if (jenkinsStage.getStageData().isStep()) {
                stage.setStep_ftl(getSteps_ftl(jenkinsStage.getStageData().getSteps()));
            }

            if (jenkinsStage.getStageData().getIsEnvironment()) {
                stage.setEnvironment_ftl(
                        getEnvironment_ftl(jenkinsStage.getStageData().getJenkinsEnvironment()));
            }

            if (jenkinsStage.getStageData().isInput()) {
                stage.setInput_ftl(jenkinsStage.getStageData().getInputData());
            }

            if (jenkinsStage.getStageData().isOptions()) {
                stage.setOptions_ftl(jenkinsStage.getStageData().getOptionsData());
            }

            if (jenkinsStage.getStageData().isWhen()) {
                stage.setWhen_ftl(jenkinsStage.getStageData().getWhenData());
            }

            if (jenkinsStage.getStageData().getIsAgent()) {
                stage.setAgent_ftl(getAgent_ftl(jenkinsStage.getStageData().getAgent()));
            }

            if (jenkinsStage.isSource()) {
                stage.setSource_ftl(getSource_ftl(jenkinsStage.getJenkinsSource()));
            }
            log.info("Completed Setting Pipeline Child Stage Pojo "+jenkinsStage.getId());
            childStages.add(stage);
        }

        return childStages;
    }

    private String getChildStagesFtl(List<PipelineStage> childStages) {

        TemplateManager templateManager = new TemplateManager();
        Map<String, Object> templateData = new HashMap<>();

        templateData.put("childstages", childStages);
        return templateManager.processTemplate("multistage", templateData);
    }


    private String getSteps_ftl(List<JenkinsStep> steps) {

        StepConverter stepConverter = new StepConverter(steps);
        return stepConverter.getStepFtl();
    }

    private String getEnvironment_ftl(JenkinsEnvironment jenkinsEnvironment) {
        EnvironmentConverter environmentConverter = new EnvironmentConverter(jenkinsEnvironment);
        return environmentConverter.getEnvironmentFtl();
    }

    private String getAgent_ftl(JenkinsAgent jenkinsAgent) {

        AgentConverter agentConverter = new AgentConverter(jenkinsAgent);
        return agentConverter.getAgentFtl();
    }

    private String getSource_ftl(JenkinsSource jenkinsSource) {

        SourceConverter sourceConverter = new SourceConverter(jenkinsSource);
        return sourceConverter.getsource_ftl();
    }


    private String getPipelineConfigFtl() {

        ConfigurationConverter configurationConverter = new ConfigurationConverter();
        return configurationConverter.getConfigFtl();
    }

    private String getStageDataJson(JenkinsStageData stageData) {

        JSONObject stageData_json = new JSONObject();
        String str_stageData;
        switch (stageData.getStageDataType()) {

            case GIT:
                stageData_json.put("type", stageData.getType());
                stageData_json.put("repo", stageData.getRepo());
                stageData_json.put("branch", stageData.getBranch());
                str_stageData = stageData_json.toString();
                str_stageData = str_stageData.substring(1, str_stageData.length() - 1);
                break;

            case TEST:
                stageData_json.put("type", stageData.getType());
                stageData_json.put("client", stageData.getClient());
                str_stageData = stageData_json.toString();
                str_stageData = str_stageData.substring(1, str_stageData.length() - 1);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + stageData);
        }

        return str_stageData;
    }

}
