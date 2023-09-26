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

import com.cognizant.workbench.pipeline.parser.model.PipelineSource;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsSource;
import com.cognizant.workbench.pipeline.parser.utils.TemplateManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SourceConverter {

    private final PipelineSource source = new PipelineSource();
    private final JenkinsSource jenkinsSource;

    public SourceConverter(JenkinsSource jenkinsSource) {
        this.jenkinsSource = jenkinsSource;
    }

    private void setSourceData() {

        log.info("Setting Stage Source Pojo");
        source.setId(jenkinsSource.getId());
        source.setType(jenkinsSource.getType());
        source.setName(jenkinsSource.getName());

        source.setDataType(jenkinsSource.getDataType());
        source.setRepo(jenkinsSource.getRepo());
        source.setBranch(jenkinsSource.getBranch());
        source.setSubDirectory(jenkinsSource.getSubDirectory());
        log.info("Completed Setting Stage Source Pojo");
    }

    public String getsource_ftl() {
        log.info("Preparing Stage Source ftl data");
        TemplateManager templateManager = new TemplateManager();
        Map<String, Object> templateData = new HashMap<>();
        setSourceData();
        templateData.put("source", source);
        String response = templateManager.processTemplate("source", templateData);
        log.info("Completed Preparing Stage Source ftl data");
        return response;
    }

}
