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

package com.cognizant.workbench.pipeline.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenkinsPipelineParser {

    private String version;
    private String name;
    private String platform;
    private Date creationDate;

    private Boolean isEnvironment = false;
    private String environment_ftl;

    private Boolean isAgent = false;
    private String agent_ftl;

    private Boolean isOptions = false;
    private String options_ftl;

    private Boolean isParameters = false;
    private String parameters_ftl;

    private String stages_ftl;

    private Boolean isPost = false;
    private String post_ftl;

    private Boolean isTools = false;
    private String tools_ftl;

    private Boolean isTriggers = false;
    private String triggers_ftl;
}
