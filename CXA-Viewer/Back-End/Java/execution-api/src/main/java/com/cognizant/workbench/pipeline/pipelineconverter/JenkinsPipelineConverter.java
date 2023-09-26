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

package com.cognizant.workbench.pipeline.pipelineconverter;

import com.cognizant.workbench.pipeline.model.*;
import com.cognizant.workbench.pipeline.model.directives.Environment;
import com.cognizant.workbench.pipeline.model.directives.Parameter;
import com.cognizant.workbench.pipeline.model.directives.Triggers;
import com.cognizant.workbench.pipeline.model.options.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class JenkinsPipelineConverter {

    private static final int TAB_1 = 1;
    private static final String HEADER = "#!/usr/bin/groovy \n";
    private static final String PIPELINE = "pipeline{\n";
    private static final String STAGES = "%s" + "stages{%n";
    private static final String STAGE = "%s" + "stage('%s'){%n";

    private static final String PARALLEL = "%s" + "parallel{%n";
    private StageConverter stageConverter = new StageConverter();

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }


    public String generatePipeline(Project project, PipelineConfigBean configBean) {
        log.info("generatePipeline() >>> started");
        StringBuilder response = new StringBuilder();
        Pipeline pipeline = project.getCi().getPipeline();

        Agent agent = pipeline.getAgent();
        Post post = pipeline.getPost();
        Triggers triggers = pipeline.getTriggers();
        List<Parameter> parameters = pipeline.getParameters();
        List<Environment> environments = pipeline.getEnvironments();
        Map<String, String> tools = pipeline.getTools();
        List<Option> options = pipeline.getOptions();

        response.append(HEADER);
        Optional.ofNullable(pipeline.getScriptDefinitions()).ifPresent(defs ->
            defs.forEach(def -> response.append(def.getDataAsGroovyScript().convertAsDef(-1)))
        );
        response.append(PIPELINE);

        //Agent Section
        response.append(new AgentConverter().generateAgent(agent, TAB_1));

        //Options Section
        if (!CollectionUtils.isEmpty(options))
            response.append(new OptionsConverter().convert(options, TAB_1));

        //Tools Section //This is ignored if agent none is specified.
        if (Objects.nonNull(agent) && !"none".equalsIgnoreCase(agent.getAgentType()) && Objects.nonNull(tools))
            response.append(new DirectiveConverter().convertTools(tools, TAB_1));

        //Triggers Section
        if (Objects.nonNull(triggers))
            response.append(new TriggerConverter().convert(triggers, TAB_1));

        //Environments Section
        if (Objects.nonNull(environments))
            response.append(new DirectiveConverter().convertEnv(environments, TAB_1));

        //Parameters Section
        if (Objects.nonNull(parameters))
            response.append(new ParameterConverter().convert(parameters, TAB_1));

        // Stages section
        List<Stage> stages = pipeline.getStages().stream().filter(stage -> !stage.getType().equalsIgnoreCase("config")).collect(Collectors.toList());
        response.append(String.format(STAGES, tabs(TAB_1)));
        for (Stage stage : stages)
            response.append(stage.isParallel() ? generateParallel(stage, TAB_1 + 1, configBean, stage.isParallel()) : stageConverter.generateStage(stage, TAB_1 + 1, configBean, stage.isParallel())).append("\n");
        response.append(tabs(TAB_1)).append("}\n");

        //Post Section
        if (Objects.nonNull(post)) {
            response.append(post.convert(configBean.getPlatform(), TAB_1));
        }

        response.append("}");

        log.info("generatePipeline() >>> Ended");
        return response.toString();
    }

    private String generateParallel(Stage stage, final int i, PipelineConfigBean configBean, boolean isParallel) {
        //i->2
        log.info("generateParallel() : Started");
        StringBuilder parallel = new StringBuilder();

        parallel.append(String.format(STAGE, tabs(i), stage.getId())).append(String.format(PARALLEL, tabs(i + 1)));

        for (Stage pStage : stage.getDataAsList())
            parallel.append(stageConverter.generateStage(pStage, i + 2, configBean, isParallel)).append("\n");

        parallel.append(tabs(i + 1)).append("}\n").append(String.format("%s}", tabs(i)));
        log.info("generateParallel() : Ended");
        return parallel.toString();
    }

}
