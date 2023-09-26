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

package com.cognizant.workbench.tools.util;

import com.cognizant.workbench.base.config.UserAuditing;
import com.cognizant.workbench.tools.beans.*;
import com.cognizant.workbench.tools.dto.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Created by 784420 on 9/9/2019 12:22 PM
 */
@Service
@Slf4j
@AllArgsConstructor
public class ToolsUtil {

    private final UserAuditing auditing;

    /*Source Stage Converters*/
    public ToolSource convertSourceDtoToEntity(ToolSourceDTO dto, ToolSource toolSource) {
        BeanUtils.copyProperties(dto, toolSource);
        return toolSource;
    }

    public ToolSource convertSourceDtoToNewEntity(ToolSourceDTO dto, ToolSource toolSource) {
        BeanUtils.copyProperties(dto, toolSource);
        toolSource.setCreatedUser(getCurrentUser());
        toolSource.setCreatedDate(new Date());
        return toolSource;
    }

    /*Test Stage Converters*/
    public ToolTest convertTestDtoToEntity(ToolTestDTO dto, ToolTest toolTest) {
        BeanUtils.copyProperties(dto, toolTest);
        if (dto.getAgent() != null)
            toolTest.setAgent(convertAgentDtoToEntity(dto.getAgent(), new ToolAgent()));
        else
            toolTest.setAgent(null);
        return toolTest;
    }

    public ToolTest convertTestDtoToNewEntity(ToolTestDTO dto, ToolTest toolTest) {
        BeanUtils.copyProperties(dto, toolTest);
        toolTest.setCreatedUser(getCurrentUser());
        toolTest.setCreatedDate(new Date());
        if (dto.getAgent() != null)
            toolTest.setAgent(convertAgentDtoToEntity(dto.getAgent(), new ToolAgent()));
        return toolTest;
    }

    /*Agent Stage Converters*/
    public ToolAgent convertAgentDtoToEntity(ToolAgentDTO dto, ToolAgent toolAgent) {
        BeanUtils.copyProperties(dto, toolAgent);
        return toolAgent;
    }

    public ToolAgent convertAgentDtoToNewEntity(ToolAgentDTO dto, ToolAgent toolAgent) {
        BeanUtils.copyProperties(dto, toolAgent);
        toolAgent.setCreatedUser(getCurrentUser());
        toolAgent.setCreatedDate(new Date());
        return toolAgent;
    }

    /*Deploy Stage Converters*/
    public ToolDeploy convertDeployDtoToEntity(ToolDeployDTO dto, ToolDeploy toolDeploy) {
        BeanUtils.copyProperties(dto, toolDeploy);
        return toolDeploy;
    }

    public ToolDeploy convertDeployDtoToNewEntity(ToolDeployDTO dto, ToolDeploy toolDeploy) {
        BeanUtils.copyProperties(dto, toolDeploy);
        toolDeploy.setCreatedUser(getCurrentUser());
        toolDeploy.setCreatedDate(new Date());
        return toolDeploy;
    }

    /*StageStep Converters*/
    public ToolStageStep convertStepDtoToEntity(ToolStageStepDTO dto, ToolStageStep toolStageStep) {
        BeanUtils.copyProperties(dto, toolStageStep);
        return toolStageStep;
    }

    public ToolStageStep convertStepDtoToNewEntity(ToolStageStepDTO dto, ToolStageStep toolStageStep) {
        BeanUtils.copyProperties(dto, toolStageStep);
        toolStageStep.setCreatedUser(getCurrentUser());
        toolStageStep.setCreatedDate(new Date());
        return toolStageStep;
    }

    private String getCurrentUser() {
        Optional<String> currentAuditor = auditing.getCurrentAuditor();
        return currentAuditor.orElse("");
    }

    /*Project Config Converters*/
    public ToolProjectConfig convertConfigDtoToEntity(ToolProjectConfigDTO source, ToolProjectConfig target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public ToolProjectConfig convertConfigDtoToNewEntity(ToolProjectConfigDTO dto, ToolProjectConfig target) {
        BeanUtils.copyProperties(dto, target);
        target.setCreatedUser(getCurrentUser());
        target.setCreatedDate(new Date());
        return target;
    }
}
