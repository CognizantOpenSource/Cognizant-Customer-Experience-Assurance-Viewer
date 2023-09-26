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

package com.cognizant.workbench.init.component;

import com.cognizant.workbench.base.models.User;
import com.cognizant.workbench.base.models.WhiteList;
import com.cognizant.workbench.base.repos.WhiteListRepository;
import com.cognizant.workbench.init.constants.InitDBCons;
import com.cognizant.workbench.init.util.InitDBUtil;
import com.cognizant.workbench.pipeline.model.SystemSetting;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggest;
import com.cognizant.workbench.pipeline.service.AutoSuggestService;
import com.cognizant.workbench.pipeline.service.SystemSettingService;
import com.cognizant.workbench.seleniumgrid.beans.Image;
import com.cognizant.workbench.seleniumgrid.services.DockerComposeService;
import com.cognizant.workbench.tools.beans.*;
import com.cognizant.workbench.tools.services.ProjectConfigService;
import com.cognizant.workbench.tools.services.StageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 784420 on 8/1/2019 9:43 AM
 */
@Slf4j
@AllArgsConstructor
@Component
public class InitialDBConfig {

    private StageService stageService;
    private AutoSuggestService autoSuggestService;
    private SystemSettingService systemSettingService;
    private ProjectConfigService projectConfigService;
    private DockerComposeService dockerComposeService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private InitDBUtil util;
    private WhiteListRepository whiteListRepository;

    public void init() {
        // Need to Implement logic for future validations
    }

//    public void insertUser() {
//        //Need to implement the logic
//    }

    public void insertRoles() {
        //Need to implement the logic
    }

    public void insertProjects() {
        //Need to implement the logic
    }

    List<ToolSource> insertSourceStages() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.SOURCE_STAGE_JSON_FILE);
        if (buffer.length > 0) {
            List<ToolSource> toolSourceList = objectMapper.readValue(buffer, new TypeReference<List<ToolSource>>() {
            });
            toolSourceList.forEach(toolSource -> {
                toolSource.setCreatedUser(InitDBCons.SYSTEM_NAME);
                toolSource.setCreatedDate(new Date());
                toolSource.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                toolSource.setLastModifiedDate(Instant.now());
            });
            return stageService.addAllSourceStages(toolSourceList);
        } else {
            return new ArrayList<>();
        }
    }

    List<ToolTest> insertTestStages() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.TEST_STAGE_JSON_FILE);
        if (buffer.length > 0) {
            List<ToolTest> toolTestList = objectMapper.readValue(buffer, new TypeReference<List<ToolTest>>() {
            });
            toolTestList.forEach(toolTest -> {
                toolTest.setCreatedUser(InitDBCons.SYSTEM_NAME);
                toolTest.setCreatedDate(new Date());
                toolTest.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                toolTest.setLastModifiedDate(Instant.now());
            });
            return stageService.addAllTestStages(toolTestList);
        } else {
            return new ArrayList<>();
        }
    }



    List<ToolDeploy> insertDeployStages() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.DEPLOY_STAGE_JSON_FILE);
        if (buffer.length > 0) {
            List<ToolDeploy> toolDeployList = objectMapper.readValue(buffer, new TypeReference<List<ToolDeploy>>() {
            });
            toolDeployList.forEach(toolDeploy -> {
                toolDeploy.setCreatedUser(InitDBCons.SYSTEM_NAME);
                toolDeploy.setCreatedDate(new Date());
                toolDeploy.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                toolDeploy.setLastModifiedDate(Instant.now());
            });
            return stageService.addAllDeployStages(toolDeployList);
        } else {
            return new ArrayList<>();
        }
    }

    List<ToolAgent> insertAgentStages() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.AGENT_STAGE_JSON_FILE);
        if (buffer.length > 0) {
            List<ToolAgent> toolAgentList = objectMapper.readValue(buffer, new TypeReference<List<ToolAgent>>() {
            });
            toolAgentList.forEach(toolAgent -> {
                toolAgent.setCreatedUser(InitDBCons.SYSTEM_NAME);
                toolAgent.setCreatedDate(new Date());
                toolAgent.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                toolAgent.setLastModifiedDate(Instant.now());
            });
            return stageService.addAllAgentStages(toolAgentList);
        } else {
            return new ArrayList<>();
        }
    }

    List<ToolStageStep> insertStageSteps() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.STAGE_STEP_JSON_FILE);
        if (buffer.length > 0) {
            List<ToolStageStep> toolStageStepList = objectMapper.readValue(buffer, new TypeReference<List<ToolStageStep>>() {
            });
            toolStageStepList.forEach(toolStageStep -> {
                toolStageStep.setCreatedUser(InitDBCons.SYSTEM_NAME);
                toolStageStep.setCreatedDate(new Date());
                toolStageStep.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                toolStageStep.setLastModifiedDate(Instant.now());
            });
            return stageService.addAllStageSteps(toolStageStepList);
        } else {
            return new ArrayList<>();
        }
    }

    public List<AutoSuggest> insertAutoSuggestList() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.AUTO_SUGGEST_JSON_FILE);
        if (buffer.length > 0) {
            List<AutoSuggest> autoSuggestList = objectMapper.readValue(buffer, new TypeReference<List<AutoSuggest>>() {
            });
            autoSuggestList.forEach(autoSuggest -> {
                autoSuggest.setCreatedUser(InitDBCons.SYSTEM_NAME);
                autoSuggest.setCreatedDate(new Date());
                autoSuggest.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                autoSuggest.setLastModifiedDate(Instant.now());
            });
            return autoSuggestService.addAllAutoSuggestList(autoSuggestList);
        } else {
            return new ArrayList<>();
        }
    }

    public List<SystemSetting> insertSystemSettingsList() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.SYSTEM_SETTING_JSON);
        if (buffer.length > 0) {
            List<SystemSetting> systemSettingList = objectMapper.readValue(buffer, new TypeReference<List<SystemSetting>>() {
            });
            systemSettingList.forEach(systemSetting -> {
                systemSetting.setCreatedUser(InitDBCons.SYSTEM_NAME);
                systemSetting.setCreatedDate(new Date());
                systemSetting.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                systemSetting.setLastModifiedDate(Instant.now());
            });
            return systemSettingService.addAll(systemSettingList);
        } else {
            return new ArrayList<>();
        }
    }

    public List<ToolProjectConfig> insertProjectConfigsList() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.PROJECT_CONFIG_JSON);
        if (buffer.length > 0) {
            List<ToolProjectConfig> projectConfigList = objectMapper.readValue(buffer, new TypeReference<List<ToolProjectConfig>>() {
            });
            projectConfigList.forEach(projectConfig -> {
                projectConfig.setCreatedUser(InitDBCons.SYSTEM_NAME);
                projectConfig.setCreatedDate(new Date());
                projectConfig.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                projectConfig.setLastModifiedDate(Instant.now());
            });
            return projectConfigService.addAll(projectConfigList);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Image> insertSeleniumGridImages() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.IMAGES_JSON_FILE);
        if (buffer.length > 0) {
            List<Image> images = objectMapper.readValue(buffer, new TypeReference<List<Image>>() {
            });
            images.forEach(image -> {
                image.setCreatedUser(InitDBCons.SYSTEM_NAME);
                image.setCreatedDate(new Date());
                image.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                image.setLastModifiedDate(Instant.now());
            });
            return dockerComposeService.addAll(images);
        } else {
            return new ArrayList<>();
        }
    }

    public List<WhiteList> insertWhitelistUrls() throws IOException {
        byte[] buffer = util.getBytes(InitDBCons.WHITELIST_URL_JSON_FILE);
        if (buffer.length > 0) {
            List<WhiteList> list = objectMapper.readValue(buffer, new TypeReference<List<WhiteList>>() {
            });
            list.forEach(whiteList -> {
                whiteList.setCreatedUser(InitDBCons.SYSTEM_NAME);
                whiteList.setCreatedDate(new Date());
                whiteList.setLastModifiedUser(InitDBCons.SYSTEM_NAME);
                whiteList.setLastModifiedDate(Instant.now());
            });
            return whiteListRepository.saveAll(list);
        } else {
            return new ArrayList<>();
        }
    }
}
