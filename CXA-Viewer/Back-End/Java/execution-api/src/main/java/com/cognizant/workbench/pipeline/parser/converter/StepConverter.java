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

import com.cognizant.workbench.pipeline.parser.model.Step;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStep;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import com.cognizant.workbench.pipeline.parser.utils.TemplateManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StepConverter {

    private final List<JenkinsStep> jenkinsSteps;

    public StepConverter(List<JenkinsStep> jenkinsSteps) {
        this.jenkinsSteps = jenkinsSteps;
    }

    public List<Step> getSteps() {
        List<Step> steps = new ArrayList<>();
        for (JenkinsStep jenkinsStep : jenkinsSteps) {
            steps.add(setStepData(jenkinsStep));
        }
        return steps;
    }

    public String getStepFtl() {
        TemplateManager templateManager = new TemplateManager();
        Map<String, Object> templateData = new HashMap<>();

        List<Step> steps = getSteps();
        templateData.put("steps", steps);
        return templateManager.processTemplate("step", templateData);
    }

    private Step setStepData(JenkinsStep jenkinsStep) {
        log.info("Setting Stage Step Pojo");
        Step step = new Step();
        step.setType(jenkinsStep.getType());
        step.setPlatform(jenkinsStep.getPlatform());
        step.setPlatforms(jenkinsStep.getPlatforms());
        String stepCoreData = getStepCoreData(jenkinsStep.getScripts());
        step.setStepCoreData(stepCoreData);
        log.info("Completed Setting Stage Step Pojo");
        return step;
    }

    private String getStepCoreData(List<String> script) {
        log.info("Setting Stage Step Core Pojo");
        final Map<Character, Character> closeToOpen = new HashMap<>();
        closeToOpen.put('}', '{');

        String joinStr = String.join("", script);

        if(!PipelineUtil.isBalanced(joinStr, new LinkedList<>(), closeToOpen)){
            for (int i = script.size() - 1; i >= 0; i--) {
                if (script.get(i).equals("}")) {
                    script.remove(script.get(i));
                } else {
                    break;
                }
            }
        }
        String data = script.stream()
                .map(plain -> '"' + StringEscapeUtils.escapeJava(plain) + '"')
                .collect(Collectors.joining(",\n "));
        log.info("Completed Setting Stage Step Core Pojo");
        return data;
    }

}
