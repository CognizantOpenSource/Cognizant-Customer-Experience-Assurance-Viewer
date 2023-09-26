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
import com.cognizant.workbench.pipeline.model.SystemSetting;
import com.cognizant.workbench.pipeline.model.autosuggest.AutoSuggest;
import com.cognizant.workbench.pipeline.service.AutoSuggestService;
import com.cognizant.workbench.pipeline.service.SystemSettingService;
import com.cognizant.workbench.tools.beans.*;
import com.cognizant.workbench.tools.services.ProjectConfigService;
import com.cognizant.workbench.tools.services.StageService;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by 784420 on 8/16/2019 12:53 PM
 */
@Slf4j
@Service
@AllArgsConstructor
public class ToolsData {

    private StageService stageService;
    private AutoSuggestService autoSuggestService;
    private SystemSettingService systemSettingService;
    private ProjectConfigService projectConfigService;
    private InitialDBConfig dbConfig;

    public void checkTools() throws IOException {
        assertToolSource();
        assertToolTest();
        assertToolDeploy();
        assertToolAgent();
        assertToolStageStep();
        assertAutoSuggest();
        assertSystemSetting();
        assertProjectConfig();
    }

    private void assertProjectConfig() throws IOException {
        log.info("Getting all ProjectConfig from DB.");
        List<ToolProjectConfig> toolProjectConfigList = projectConfigService.getAll();
        if (Collections.isEmpty(toolProjectConfigList)){
            log.info("No ProjectConfig are available in DB......! ");
            List<ToolProjectConfig> toolProjectConfigs = dbConfig.insertProjectConfigsList();
            log.info("ProjectConfig are inserted successfully. ProjectConfig count post insertion is: "+toolProjectConfigs.size());
        } else {
            log.info("ProjectConfig are available in DB with count: "+toolProjectConfigList.size());
        }
    }

    private void assertSystemSetting() throws IOException {
        log.info("Getting all SystemSettings from DB.");
        List<SystemSetting> systemSettingList = systemSettingService.getAll();
        if (Collections.isEmpty(systemSettingList)){
            log.info("No SystemSettings are available in DB......! ");
            List<SystemSetting> systemSetting = dbConfig.insertSystemSettingsList();
            log.info("SystemSettings are inserted successfully. autoSuggests count post insertion is: "+systemSetting.size());
        } else {
            log.info("SystemSettings are available in DB with count: "+systemSettingList.size());
        }
    }

    private void assertAutoSuggest() throws IOException {
        log.info("Getting all autoSuggests from DB.");
        List<AutoSuggest> allAutoSuggests = autoSuggestService.getAll();
        if (Collections.isEmpty(allAutoSuggests)){
            log.info("No autoSuggests are available in DB......! ");
            List<AutoSuggest> autoSuggests = dbConfig.insertAutoSuggestList();
            log.info("autoSuggests are inserted successfully. autoSuggests count post insertion is: "+autoSuggests.size());
        } else {
            log.info("autoSuggests are available in DB with count: "+allAutoSuggests.size());
        }
    }

    private void assertToolStageStep() throws IOException {
        log.info("Getting all StageSteps from DB.");
        List<ToolStageStep> allStageSteps = stageService.getAllStageSteps();
        if (Collections.isEmpty(allStageSteps)){
            log.info("No StageSteps are available in DB......! ");
            List<ToolStageStep> toolStageSteps = dbConfig.insertStageSteps();
            log.info("StageSteps are inserted successfully. StageSteps count post insertion is: "+toolStageSteps.size());
        } else {
            log.info("StageSteps are available in DB with count: "+allStageSteps.size());
        }
    }

    private void assertToolAgent() throws IOException {
        log.info("Getting all AgentStages from DB.");
        List<ToolAgent> allAgentStages = stageService.getAllAgentStages();
        if (Collections.isEmpty(allAgentStages)){
            log.info("No AgentStages are available in DB......! ");
            List<ToolAgent> toolAgents = dbConfig.insertAgentStages();
            log.info("AgentStages are inserted successfully. AgentStages count post insertion is: "+toolAgents.size());
        } else {
            log.info("AgentStages are available in DB with count: "+allAgentStages.size());
        }
    }

    private void assertToolDeploy() throws IOException {
        log.info("Getting all DeploymentStages from DB.");
        List<ToolDeploy> allDeployStages = stageService.getAllDeployStages();
        if (Collections.isEmpty(allDeployStages)){
            log.info("No DeploymentStages are available in DB......! ");
            List<ToolDeploy> toolDeploys = dbConfig.insertDeployStages();
            log.info("DeploymentStages are inserted successfully. DeploymentStages count post insertion is: "+toolDeploys.size());
        } else {
            log.info("DeploymentStages are available in DB with count: "+allDeployStages.size());
        }
    }

    private void assertToolSource() throws IOException {
        log.info("Getting all SourceStages from DB.");
        List<ToolSource> allSourceStages = stageService.getAllSourceStages();
        if (Collections.isEmpty(allSourceStages)){
            log.info("No SourceStages are available in DB......! ");
            List<ToolSource> toolSourceList = dbConfig.insertSourceStages();
            log.info("SourceStages are inserted successfully. SourceStages count post insertion is: "+toolSourceList.size());
        } else {
            log.info("SourceStages are available in DB with count: "+allSourceStages.size());
        }

    }



    private void assertToolTest() throws IOException {
        log.info("Getting all TestStages from DB.");
        List<ToolTest> allTestStages = stageService.getAllTestStages();
        if (Collections.isEmpty(allTestStages)){
            log.info("No TestStages are available in DB......! ");
            List<ToolTest> toolTestList = dbConfig.insertTestStages();
            log.info("TestStages are inserted successfully. TestStages count post insertion is: "+toolTestList.size());
        } else {
            log.info("TestStages are available in DB with count: "+allTestStages.size());
        }
    }
}
