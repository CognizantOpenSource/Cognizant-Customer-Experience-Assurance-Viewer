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

package com.cognizant.workbench.tools.beans;

import lombok.Data;

import java.util.List;

/**
 * Created by 784420 on 8/21/2019 10:36 AM
 */
@Data
public class ToolsData {
    private List<ToolSource> sources;
    private List<ToolTest> tests;
    private List<ToolDeploy> deployments;
    private List<ToolAgent> agents;
    private List<ToolStageStep> tools;
    private List<ToolProjectConfig> config;

    public ToolsData(List<ToolSource> sources, List<ToolTest> tests, List<ToolDeploy> deployments, List<ToolAgent> agents, List<ToolStageStep> tools, List<ToolProjectConfig> config) {
        this.sources = sources;
        this.tests = tests;
        this.deployments = deployments;
        this.agents = agents;
        this.tools = tools;
        this.config = config;
    }
}
