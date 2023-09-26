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

package com.cognizant.workbench.tools.controllers;

import com.cognizant.workbench.error.ResourceNotFoundException;
import com.cognizant.workbench.error.StageExistsException;
import com.cognizant.workbench.tools.beans.*;
import com.cognizant.workbench.tools.dto.*;
import com.cognizant.workbench.tools.services.ProjectConfigService;
import com.cognizant.workbench.tools.services.StageService;
import com.cognizant.workbench.tools.util.ToolsUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 8/13/2019 3:03 PM
 */
@RestController
@RequestMapping("/editor/stage")
@AllArgsConstructor
public class StageController {

    private static final String STEP = "StageStep";
    private static final String SOURCE = "SourceStage";
    private static final String TEST = "TestStage";
    private static final String AGENT = "AgentStage";
    private static final String DEPLOY = "DeploymentStage";

    private StageService stageService;
    private ProjectConfigService projectConfigService;
    private ToolsUtil util;

    /*Source Stage calls*/

    /**
     * Adding SourceStage into DB
     *
     * @param toolSourceDTO SourceStage which have to be store in DB
     * @return SourceStage, which was stored in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/source")
    @PreAuthorize("hasPermission('SourceStage','tool.create')")
    public ToolSource addSourceStage(@Valid @RequestBody ToolSourceDTO toolSourceDTO) {
        try {
            return stageService.addSourceStage(util.convertSourceDtoToNewEntity(toolSourceDTO, new ToolSource()));
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException(SOURCE, "Id", toolSourceDTO.getId());
        }
    }

    /**
     * Updating existing SourceStage with provided details
     *
     * @param toolSourceDTO SourceStage which have to be update in DB
     * @return SourceStage, which was updated in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/source")
    @PreAuthorize("hasPermission('SourceStage','tool.update')")
    public ToolSource updateSourceStage(@Valid @RequestBody ToolSourceDTO toolSourceDTO) {
        ToolSource toolSource = assertAndGetStageOrStep(toolSourceDTO.getId(), SOURCE);
        return stageService.updateSourceStage(util.convertSourceDtoToEntity(toolSourceDTO, toolSource));
    }

    /**
     * getting Source Stage based on the id provided
     *
     * @param sourceId Id which should get from DB
     * @return SourceStage details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/source/{sourceId}")
    @PreAuthorize("hasPermission('SourceStage','tool.read')")
    public ToolSource getSourceStage(@PathVariable String sourceId) {
        return assertAndGetStageOrStep(sourceId, SOURCE);
    }

    /**
     * Getting all source stages from DB
     *
     * @return List of SourceStage details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/source")
    @PreAuthorize("hasPermission('SourceStage','tool.read')")
    public List<ToolSource> getAllSourceStages() {
        return stageService.getAllSourceStages();
    }

    /**
     * Deleting Source stage from DB
     *
     * @param sourceId which should be delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/source/{sourceId}")
    @PreAuthorize("hasPermission('SourceStage','tool.delete')")
    public void removeSourceStage(@PathVariable String sourceId) {
        assertAndGetStageOrStep(sourceId, SOURCE);
        stageService.removeSourceStage(sourceId);
    }


    /*Test Stage calls*/

    /**
     * Adding TestStage in the DB
     *
     * @param toolTestDTO TestStage detail which have to be store in DB
     * @return TestStage, which was stored in DB.
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/test")
    @PreAuthorize("hasPermission('TestStage','tool.create')")
    public ToolTest addTestStage(@Valid @RequestBody ToolTestDTO toolTestDTO) {
        try {
            return stageService.addTestStage(util.convertTestDtoToNewEntity(toolTestDTO, new ToolTest()));
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException(TEST, "Id", toolTestDTO.getId());
        }
    }

    /**
     * Updating existing TestStage with provided details
     *
     * @param toolTestDTO TestStage which have to be update in DB
     * @return TestStage, which was updated in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/test")
    @PreAuthorize("hasPermission('TestStage','tool.update')")
    public ToolTest updateTestStage(@Valid @RequestBody ToolTestDTO toolTestDTO) {
        ToolTest toolTest = assertAndGetStageOrStep(toolTestDTO.getId(), TEST);
        return stageService.updateTestStage(util.convertTestDtoToEntity(toolTestDTO, toolTest));
    }

    /**
     * Getting TestStage details from DB
     *
     * @param testId id of TestStage which should get from DB
     * @return TestStage details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/test/{testId}")
    @PreAuthorize("hasPermission('TestStage','tool.read')")
    public ToolTest getTestStage(@PathVariable String testId) {
        return assertAndGetStageOrStep(testId, TEST);
    }

    /**
     * Getting list of TestStages from DB
     *
     * @return List of TestStage details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/test")
    @PreAuthorize("hasPermission('TestStage','tool.read')")
    public List<ToolTest> getAllTestStages() {
//        return stageService.getAllTestStages();
        return stageService.getAllTestStagesOrderBy();
    }

    /**
     * Deleting TestStage from DB
     *
     * @param testId which should be delete from DB
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/test/{testId}")
    @PreAuthorize("hasPermission('TestStage','tool.delete')")
    public void removeTestStage(@PathVariable String testId) {
        assertAndGetStageOrStep(testId, TEST);
        stageService.removeTestStage(testId);
    }

    /*Deployment Stage calls*/

    /**
     * Adding DeploymentStage in the DB
     *
     * @param toolDeployDTO DeploymentStage details
     * @return DeploymentStage details post storing in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/deploy")
    @PreAuthorize("hasPermission('DeploymentStage','tool.create')")
    public ToolDeploy addDeployStage(@Valid @RequestBody ToolDeployDTO toolDeployDTO) {
        try {
            return stageService.addDeployStage(util.convertDeployDtoToNewEntity(toolDeployDTO, new ToolDeploy()));
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException("DeployStage", "name", toolDeployDTO.getName());
        }
    }

    /**
     * Updating existing DeploymentStage with provided details
     *
     * @param toolDeployDTO DeploymentStage which have to be update in DB
     * @return DeploymentStage, which was updated in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/deploy")
    @PreAuthorize("hasPermission('DeploymentStage','tool.update')")
    public ToolDeploy updateDeployStage(@Valid @RequestBody ToolDeployDTO toolDeployDTO) {
        ToolDeploy toolDeploy = assertAndGetStageOrStep(toolDeployDTO.getName(), DEPLOY);
        return stageService.updateDeployStage(util.convertDeployDtoToEntity(toolDeployDTO, toolDeploy));
    }

    /**
     * Getting Deployment details from DB.
     *
     * @param deployId id of DeployStage which should get from DB
     * @return Deployment details.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/deploy/{deployId}")
    @PreAuthorize("hasPermission('DeploymentStage','tool.read')")
    public ToolDeploy getDeployStage(@PathVariable String deployId) {
        return assertAndGetStageOrStep(deployId, DEPLOY);
    }

    /**
     * Getting list of all Deployment Stage from DB
     *
     * @return List of all Deployment Stage which are available in DB
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/deploy")
    @PreAuthorize("hasPermission('DeploymentStage','tool.read')")
    public List<ToolDeploy> getAllDeployStages() {
        return stageService.getAllDeployStages();
    }

    /**
     * Deleting Deployment stage based on the provided id
     *
     * @param deployId id of deployment stage
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/deploy/{deployId}")
    @PreAuthorize("hasPermission('DeploymentStage','tool.delete')")
    public void removeDeployStage(@PathVariable String deployId) {
        assertAndGetStageOrStep(deployId, DEPLOY);
        stageService.removeDeployStage(deployId);
    }

    /*Agent Stage calls*/

    /**
     * Adding Agent Stage details in the DB
     *
     * @param toolAgentDTO details which need to store in DB
     * @return AgentDetails, post storing in DB.
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/agent")
    @PreAuthorize("hasPermission('Agent','tool.create')")
    public ToolAgent addAgentStage(@Valid @RequestBody ToolAgentDTO toolAgentDTO) {
        try {
            return stageService.addAgentStage(util.convertAgentDtoToNewEntity(toolAgentDTO, new ToolAgent()));
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException(AGENT, "Id", toolAgentDTO.getId());
        }
    }

    /**
     * Updating existing AgentDetails with provided details
     *
     * @param toolAgentDTO AgentDetails which have to be update in DB
     * @return AgentDetails, which was updated in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/agent")
    @PreAuthorize("hasPermission('Agent','tool.update')")
    public ToolAgent updateAgentStage(@Valid @RequestBody ToolAgentDTO toolAgentDTO) {
        ToolAgent toolAgent = assertAndGetStageOrStep(toolAgentDTO.getId(), AGENT);
        return stageService.updateAgentStage(util.convertAgentDtoToEntity(toolAgentDTO, toolAgent));
    }

    /**
     * Getting Agent Stage Details
     *
     * @param agentId id of Agent which should get from the DB
     * @return Agent Details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/agent/{agentId}")
    @PreAuthorize("hasPermission('Agent','tool.read')")
    public ToolAgent getAgentStage(@PathVariable String agentId) {
        return assertAndGetStageOrStep(agentId, AGENT);
    }

    /**
     * @return List of all Agent Stages which are available in DB
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/agent")
    @PreAuthorize("hasPermission('Agent','tool.read')")
    public List<ToolAgent> getAllAgentStages() {
        return stageService.getAllAgentStages();
    }

    /**
     * Deleting Agent Stage from DB
     *
     * @param agentId id of Agent stage which should delete from DB
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/agent/{agentId}")
    @PreAuthorize("hasPermission('Agent','tool.read')")
    public void removeAgentStage(@PathVariable String agentId) {
        assertAndGetStageOrStep(agentId, AGENT);
        stageService.removeAgentStage(agentId);
    }

    /*StageStep calls*/

    /**
     * Adding StageStep details in DB
     *
     * @param toolStageStepDTO details which need to store in DB
     * @return StageStep details post storing in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/step")
    @PreAuthorize("hasPermission('StageSteps','tool.create')")
    public ToolStageStep addStageStep(@Valid @RequestBody ToolStageStepDTO toolStageStepDTO) {
        try {
            return stageService.addStageStep(util.convertStepDtoToNewEntity(toolStageStepDTO, new ToolStageStep()));
        } catch (org.springframework.dao.DuplicateKeyException | com.mongodb.DuplicateKeyException e) {
            throw new StageExistsException(STEP, "Name", toolStageStepDTO.getName());
        }
    }

    /**
     * Updating existing StageStep with provided details
     *
     * @param toolStageStepDTO StageStep which have to be update in DB
     * @return StageStep, which was updated in DB
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/step")
    @PreAuthorize("hasPermission('StageSteps','tool.update')")
    public ToolStageStep updateStageStep(@Valid @RequestBody ToolStageStepDTO toolStageStepDTO) {
        ToolStageStep toolStageStep = assertAndGetStageOrStep(toolStageStepDTO.getName(), STEP);
        return stageService.updateStageStep(util.convertStepDtoToEntity(toolStageStepDTO, toolStageStep));
    }

    /**
     * Getting Stage Step details
     *
     * @param stepId Id of StageStep, Which should be get from the DB
     * @return StageStep details
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/step/{stepId}")
    @PreAuthorize("hasPermission('StageSteps','tool.read')")
    public ToolStageStep getStageStep(@PathVariable String stepId) {
        return assertAndGetStageOrStep(stepId, STEP);
    }

    /**
     * @return List of all StageSteps details which are available in DB
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/step")
    @PreAuthorize("hasPermission('StageSteps','tool.read')")
    public List<ToolStageStep> getAllStageSteps() {
        return stageService.getAllStageSteps();
    }

    /**
     * Deleting StageStep from DB.
     *
     * @param stepId id of StageStep which should delete from DB
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/step/{stepId}")
    @PreAuthorize("hasPermission('StageSteps','tool.delete')")
    public void removeStageStep(@PathVariable String stepId) {
        assertAndGetStageOrStep(stepId, STEP);
        stageService.removeStageStep(stepId);
    }


    /*###All stages details returning in the single call response###*/

    /**
     * @return All stages details in single response
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("hasPermission('SourceStage','tool.read')")
    public ToolsData getAllStages() {
        return new ToolsData(getAllSourceStages(), getAllTestStages(), getAllDeployStages(), getAllAgentStages(), getAllStageSteps(), getAllProjectConfig());
    }

    /**
     * Assert and Get the Stage or Step details based on the Id. If data present it will return the object otherwise it will through ResourceNotFoundException.
     *
     * @param id   Stage or Step Id
     * @param type Stage type (Source/Test/Deploy/Agent/Step)
     * @param <T>  return class type
     * @return returns the Stage or Step details.
     */
    private <T> T assertAndGetStageOrStep(String id, String type) {
        switch (type) {
            case SOURCE:
                Optional<ToolSource> sourceStage = stageService.getSourceStage(id);
                if (sourceStage.isPresent())
                    return (T) sourceStage.get();
                break;
            case TEST:
                Optional<ToolTest> toolTest = stageService.getTestStage(id);
                if (toolTest.isPresent())
                    return (T) toolTest.get();
                break;
            case AGENT:
                Optional<ToolAgent> agentStage = stageService.getAgentStage(id);
                if (agentStage.isPresent())
                    return (T) agentStage.get();
                break;
            case DEPLOY:
                Optional<ToolDeploy> deployStage = stageService.getDeployStage(id);
                if (deployStage.isPresent())
                    return (T) deployStage.get();
                break;
            case STEP:
                Optional<ToolStageStep> stageStep = stageService.getStageStep(id);
                if (stageStep.isPresent())
                    return (T) stageStep.get();
                break;
            default:
                break;
        }
        throw new ResourceNotFoundException(type, type + "Id", id);
    }

    private List<ToolProjectConfig> getAllProjectConfig() {
        return projectConfigService.getAll();
    }
}
