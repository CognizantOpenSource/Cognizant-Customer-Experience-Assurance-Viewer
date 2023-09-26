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
import com.cognizant.workbench.pipeline.model.snippets.Checkout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

@Slf4j
class StageConverter {

    private static final String STAGE = "%s" + "stage('%s'){%n";
    private static final String PARALLEL = "%s" + "parallel{%n";
    private static final String STEPS = "%s" + "steps{%n";

    private static final String CHANGE_DIR = "dir('%s'){%n";
    private boolean isChangeDir;
    private String changeSubDirectory;

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    String generateStageAs(String name, Stage stage, int i, PipelineConfigBean configBean) {
        //i->2
        log.info("generateStage() : Started");
        isChangeDir = false;
        StringBuilder generatedStage = new StringBuilder();
        Agent agent = stage.getAgent();
        Map<String, String> tools = stage.getTools();

        if (stage == null) generatedStage.toString();

        generatedStage.append(String.format(STAGE, tabs(i), name));

        //Agent Section
        if (Objects.nonNull(agent))
            generatedStage.append(new AgentConverter().generateAgent(agent, i + 1));

        //Stage Options
        if (!CollectionUtils.isEmpty(stage.getStageOptions())) {
            generatedStage.append(new StageOptionsConverter().convert(stage.getStageOptions(), i + 1));
        }

        //Tools Section //This is ignored if agent none is specified.
        if (Objects.nonNull(agent) && !"none".equalsIgnoreCase(agent.getAgentType()) && Objects.nonNull(tools))
            generatedStage.append(new DirectiveConverter().convertTools(tools, i + 1));

        //Environments
        if (Objects.nonNull(stage.getEnvironments()))
            generatedStage.append(new DirectiveConverter().convertEnv(stage.getEnvironments(), i + 1));

        //When
        if (Objects.nonNull(stage.getWhen()))
            generatedStage.append(new WhenConverter().convert(stage.getWhen(), i + 1));

        //Input
        if (Objects.nonNull(stage.getInput()))
            generatedStage.append(new InputConverter().convert(stage.getInput(), i + 1));

        //Steps
        generatedStage.append(String.format(STEPS, tabs(i + 1)));
        if ("source".equals(stage.getType()) && !stage.isParallel())
            generatedStage.append(tabs(i + 2)).append(generateSource(stage.getDataAsData()));
        if (Objects.nonNull(stage.getSource()))
            generatedStage.append(tabs(i + 2)).append(generateCheckout(stage.getSource()));
        if (stage.getDataAsData().getScript() != null || stage.getDataAsData().getSteps() != null) {
            if (isChangeDir) {
                generatedStage.append(tabs(i + 2)).append(String.format(CHANGE_DIR, changeSubDirectory));
                i++;
            }
            generatedStage.append(new StepsConverter().convert(stage.getDataAsData(), i + 2, configBean));
            if (isChangeDir) {
                i--;
                generatedStage.append(tabs(i + 2)).append("}\n");
            }
        }
        generatedStage.append(tabs(i + 1)).append("}\n");
        generatedStage.append(tabs(i)).append("}");

        log.info("generateStage() : Ended");
        return generatedStage.toString();
    }

    String generateStage(Stage stage, int i, PipelineConfigBean configBean, boolean isParallel) {
        if (Objects.nonNull(stage.getAgent()) && Objects.nonNull(stage.getAgent().getData())
                && stage.getAgent().getData() instanceof KubernetesAgent) {
            int replicas = stage.getAgent().getDataAsKubernetes().getReplicas();
            if (replicas > 1) {
                StringBuilder generatedStage = new StringBuilder();
                if (!isParallel) {
                    generatedStage.append(String.format(STAGE, tabs(i), stage.getId()));
                    generatedStage.append(String.format(PARALLEL, tabs(i + 1), stage.getId()));
                }
                for (int replica = 1; replica <= replicas; replica++) {
                    ((KubernetesAgent) stage.getAgent().getData()).setReplicaId(replica);
                    generatedStage.append(generateStageAs(stage.getId() + "-" + replica, stage, i + 2, configBean)).append("\n");
                }
                if (!isParallel) {
                    generatedStage.append(tabs(i + 1)).append("}\n");
                    generatedStage.append(tabs(i)).append("}\n");
                }
                return generatedStage.toString();
            }
        }
        return generateStageAs(stage.getId(), stage, i, configBean);
    }

    private String generateSource(Data data) {
        log.info("generateSource() : Started");
        String sourceStr = "";
        if (data == null) return sourceStr;
        String credentialId = data.getCredentialId();
        String branch = data.getBranch();
        String repo = data.getRepo();

        if (data.getType().toLowerCase().contains("git")) {
            sourceStr = "git branch: " + (!StringUtils.isEmpty(branch) ? String.format("'%s'", branch) : "'master'")
                    + (!StringUtils.isEmpty(credentialId) ? ",credentialsId: " + String.format("'%s'", data.getCredentialId()) : "")
                    + ",url: " + String.format("'%s'", repo)
                    + "\n";
        } //git
        return sourceStr;
    }

    private String generateCheckout(Source source) {
        log.info("generateCheckout() : Started");
        Checkout checkout = source.getData();
        if (!StringUtils.isEmpty(checkout.getSubDirectory())) {
            isChangeDir = true;
            changeSubDirectory = checkout.getSubDirectory();
        }
        return checkout.convert();
    }
}
