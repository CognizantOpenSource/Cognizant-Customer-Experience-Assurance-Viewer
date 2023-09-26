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

package com.cognizant.workbench.pipeline.parser.pipelineparser.model;


import com.cognizant.workbench.pipeline.parser.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenkinsPipeline {

    private String version;
    private String name;
    private String platform;
    private Date creationDate;

    private boolean agentExists = false;
    private JenkinsAgent jenkinsAgent;

    private boolean isOptionsExists = false;
    private Options options;

    private boolean isParametersExists = false;
    private Parameters parameters;

    private boolean isEnvironmentExists = false;
    private JenkinsEnvironment environment;

    private boolean isToolsExists = false;
    private Tools tools;

    private boolean isTriggersExists = false;
    private PipelineTriggers triggers;

    private List<JenkinsStage> jenkinsStages = new ArrayList<>();

    private boolean isPostExists = false;
    private PipelinePost post;


}
