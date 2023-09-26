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

package com.cognizant.workbench.tools.services;

import com.cognizant.workbench.base.models.User;
import com.cognizant.workbench.base.repos.UserRepository;
import com.cognizant.workbench.tools.beans.*;
import com.cognizant.workbench.tools.repo.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 8/13/2019 3:05 PM
 */
@Slf4j
@Service
@AllArgsConstructor
public class StageService {

    private ToolTestRepository testRepository;
    private ToolSourceRepository sourceRepository;
    private ToolDeployRepository deployRepository;
    private ToolAgentRepository agentRepository;
    private ToolStageStepRepository stepRepository;
    private GroupOrderRepository groupOrderRepository;

    private UserRepository userRepository;

    /*Source Stage Calls*/
    public ToolSource addSourceStage(ToolSource toolSource) {
        log.info("Adding SourceStage");
        return sourceRepository.insert(toolSource);
    }

    public ToolSource updateSourceStage(ToolSource toolSource) {
        log.info("Updating SourceStage");
        return sourceRepository.save(toolSource);
    }

    public List<ToolSource> addAllSourceStages(List<ToolSource> toolSources) {
        log.info("Adding SourceStages");
        return sourceRepository.insert(toolSources);
    }

    public Optional<ToolSource> getSourceStage(String sourceStageId) {
        log.info("Getting SourceStage from DB");
        return sourceRepository.findById(sourceStageId);
    }

    public List<ToolSource> getAllSourceStages() {
        log.info("Getting all SourceStages from DB");
        return sourceRepository.findAll();
    }

    public void removeSourceStage(String sourceStageId) {
        log.info("Deleting SourceStage from DB");
        sourceRepository.deleteById(sourceStageId);
    }

    /*Test Stage Calls*/
    public ToolTest addTestStage(ToolTest toolTest) {
        log.info("Adding TestStage ");
        return testRepository.insert(toolTest);
    }

    public ToolTest updateTestStage(ToolTest toolTest) {
        log.info("Updating TestStage ");
        return testRepository.save(toolTest);
    }

    public List<ToolTest> addAllTestStages(List<ToolTest> toolTests) {
        log.info("Adding TestStage ");
        return testRepository.insert(toolTests);
    }
    public List<User> addUser(List<User> user) {
        log.info("Adding TestStage ");
        return userRepository.insert(user);
    }
    public List<User> getAllUser(){
        log.info("getting User");
        return userRepository.findAll();
    }
    public Optional<ToolTest> getTestStage(String testStageId) {
        log.info("Getting TestStage from DB");
        return testRepository.findById(testStageId);
    }

    public List<ToolTest> getAllTestStages() {
        log.info("Getting all TestStages from DB");
        return testRepository.findAll();
    }

    public List<ToolTest> getAllTestStagesOrderBy() {
        log.info("Getting all TestStages from DB");
        List<ToolTest> toolTests = testRepository.findAll();
        List<GroupOrder> groupOrders = groupOrderRepository.findAll();
        if (CollectionUtils.isEmpty(groupOrders)) return toolTests;
        Map<String, List<ToolTest>> map = new LinkedHashMap<>();
        groupOrders.stream().sorted(Comparator.comparingInt(GroupOrder::getOrderNumber))
                .forEach(groupOrder -> map.put(groupOrder.getName(), new ArrayList<>()));
        map.put("OTHERS", new ArrayList<>());
        toolTests.parallelStream().forEach(toolTest -> {
            if (map.containsKey(toolTest.getGroup()))
                map.get(toolTest.getGroup()).add(toolTest);
            else
                map.get("OTHERS").add(toolTest);
        });
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void removeTestStage(String testStageId) {
        log.info("Deleting TestStage from DB");
        testRepository.deleteById(testStageId);
    }

    /*Deployment Stage Calls*/
    public ToolDeploy addDeployStage(ToolDeploy toolDeploy) {
        log.info("Adding DeploymentStage ");
        return deployRepository.insert(toolDeploy);
    }

    public ToolDeploy updateDeployStage(ToolDeploy toolDeploy) {
        log.info("Updating DeploymentStage ");
        return deployRepository.save(toolDeploy);
    }

    public Optional<ToolDeploy> getDeployStage(String deployStageId) {
        log.info("Getting Deployment stage. Id : "+deployStageId);
        return deployRepository.findById(deployStageId);
    }

    public List<ToolDeploy> getAllDeployStages() {
        log.info("Getting all Deployment stages");
        return deployRepository.findAll();
    }

    public List<ToolDeploy> addAllDeployStages(List<ToolDeploy> toolDeploys) {
        log.info("Adding All DeploymentStages ");
        return deployRepository.insert(toolDeploys);
    }

    public void removeDeployStage(String deployStageId) {
        log.info("Deleting Deployment stage. Id : "+deployStageId);
        deployRepository.deleteById(deployStageId);
    }

    /*Agent Stage Calls*/
    public ToolAgent addAgentStage(ToolAgent toolAgent) {
        log.info("Adding AgentStage ");
        return agentRepository.insert(toolAgent);
    }

    public ToolAgent updateAgentStage(ToolAgent toolAgent) {
        log.info("Updating AgentStage ");
        return agentRepository.save(toolAgent);
    }

    public Optional<ToolAgent> getAgentStage(String agentStageId) {
        log.info("Getting Agent stage. Id: "+agentStageId);
        return agentRepository.findById(agentStageId);
    }

    public List<ToolAgent> getAllAgentStages() {
        log.info("Getting all Agent stages");
        return agentRepository.findAll();
    }

    public List<ToolAgent> addAllAgentStages(List<ToolAgent> toolAgents) {
        log.info("Adding All Agent ");
        return agentRepository.insert(toolAgents);
    }

    public void removeAgentStage(String agentStageId) {
        log.info("Deleting Agent stage. Id: "+agentStageId);
        agentRepository.deleteById(agentStageId);
    }

    /*Stage Steps Calls*/
    public ToolStageStep addStageStep(ToolStageStep toolStageStep) {
        log.info("Adding StageStep ");
        return stepRepository.insert(toolStageStep);
    }

    public ToolStageStep updateStageStep(ToolStageStep toolStageStep) {
        log.info("Updating StageStep ");
        return stepRepository.save(toolStageStep);
    }

    public Optional<ToolStageStep> getStageStep(String stageStepId) {
        log.info("Getting StageStep. Id: "+stageStepId);
        return stepRepository.findById(stageStepId);
    }

    public List<ToolStageStep> getAllStageSteps() {
        log.info("Getting all StageStep");
        return stepRepository.findAll();
    }

    public List<ToolStageStep> addAllStageSteps(List<ToolStageStep> toolStageSteps) {
        log.info("Adding All StageSteps ");
        return stepRepository.insert(toolStageSteps);
    }

    public void removeStageStep(String stageStepId) {
        log.info("Deleting StageStep. Id: "+stageStepId);
        stepRepository.deleteById(stageStepId);
    }
}
