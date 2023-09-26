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

package com.cognizant.workbench.pipeline.service;

import com.cognizant.workbench.pipeline.model.PipelineConfigBean;
import com.cognizant.workbench.pipeline.model.Project;
import com.cognizant.workbench.pipeline.pipelineconverter.JenkinsPipelineConverter;
import com.cognizant.workbench.pipeline.pipelineconverter.PipelineString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PipelineService {

    public PipelineString generatePipeline(Project project, PipelineConfigBean configBean) {
        log.info("Generating Jenkins pipeline <Starts>");
        PipelineString pipelineString = new PipelineString();
        JenkinsPipelineConverter jenkinsPipelineConverter = new JenkinsPipelineConverter();
        String pipeline = jenkinsPipelineConverter.generatePipeline(project, configBean);
        pipelineString.setVersion(project.getVersion());
        pipelineString.setPipeline(pipeline);
        log.info("Generated pipeline script: \n" + pipeline);
        log.info("Generating Jenkins pipeline <Ends>");

        return pipelineString;
    }

    public PipelineString generatePipeline(Project project) {
        PipelineConfigBean configBean = new PipelineConfigBean(null, null, project.getPlatform());
        return generatePipeline(project, configBean);
    }

}
