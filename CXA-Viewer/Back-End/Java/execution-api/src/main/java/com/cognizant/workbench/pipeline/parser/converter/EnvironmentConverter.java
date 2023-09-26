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


import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsEnvironment;
import com.cognizant.workbench.pipeline.parser.utils.TemplateManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EnvironmentConverter {

    private final JenkinsEnvironment jenkinsEnvironment;

    public EnvironmentConverter(JenkinsEnvironment jenkinsEnvironment) {
        this.jenkinsEnvironment = jenkinsEnvironment;
    }

    public String getEnvironmentFtl() {
        log.info("Setting & preparing Pipeline environment  ftl Data");
        TemplateManager templateManager = new TemplateManager();
        Map<String, Object> templateData = new HashMap<>();

        templateData.put("env", jenkinsEnvironment);
        String templateStr = templateManager.processTemplate("environment", templateData);
        log.info("Completed Settings & preparing pipeline environment ftl Data");
        return templateStr;
    }

}
