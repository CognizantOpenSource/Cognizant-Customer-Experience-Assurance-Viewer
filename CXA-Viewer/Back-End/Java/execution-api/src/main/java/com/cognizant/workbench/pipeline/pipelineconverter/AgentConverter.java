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
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
class AgentConverter {
    private static final String AGENT_ANY = "%s" + "agent any%n";
    private static final String AGENT_NONE = "%s" + "agent none%n";
    private static final String AGENT = "%s" + "agent{%n";

    private static final String ARGS_TEMPLATE = "args '%s'%n";
    private static final String REG_CRED_ID_TEMPLATE = "registryCredentialsId '%s'%n";
    private static final String REG_URL_TEMPLATE = "registryUrl '%s'%n";
    private static final String RE_USE_NODE_TEMPLATE = "reuseNode '%s'%n";
    private static final String LABEL_TEMPLATE = "label '%s'%n";
    private static final String CUSTOM_WORKSPACE_TEMPLATE = "customWorkspace '%s'%n";
    private static final String ALWAYS_PULL_TEMPLATE = "alwaysPull '%s'%n";
    private static final String IMAGE_TEMPLATE = "image '%s'%n";
    private static final String AD_BUILD_ARGS_TEMPLATE = "additionalBuildArgs '%s'%n";
    private static final String DIR_TEMPLATE = "dir '%s'%n";
    private static final String FILENAME_TEMPLATE = "filename '%s'%n";

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    String generateAgent(Agent agent, int tab) {

        log.info("generateAgent() : started");
        final StringBuilder agentStr = new StringBuilder();
        if (agent == null) {
            return String.format(AGENT_ANY, tabs(tab));
        }

        String agentType = agent.getAgentType();
        switch (agentType) {
            case Constants.DOCKER:
                agentStr.append(String.format(AGENT, tabs(tab)));
                agentStr.append(generateDocker(agent.getDataAsDocker(), tab + 1));
                agentStr.append(tabs(tab)).append("}\n");
                break;
            case Constants.DOCKERFILE:
                agentStr.append(String.format(AGENT, tabs(tab)));
                agentStr.append(generateDockerFile(agent.getDataAsDockerFile(), tab + 1));
                agentStr.append(tabs(tab)).append("}\n");
                break;
            case Constants.NODE:
                agentStr.append(String.format(AGENT, tabs(tab)));
                agentStr.append(tabs(tab + 1)).append("node{\n");
                agentStr.append(
                        (agent.getDataAsList()).stream().map(list -> tabs(tab + 2) + list + "'\n").collect(Collectors.joining())
                ).append(tabs(tab + 1)).append("}\n")
                        .append(tabs(tab)).append("}\n");
                break;
            case Constants.LABEL:
                agentStr.append(String.format(AGENT, tabs(tab)));
                agentStr.append(tabs(tab + 1)).append(String.format("%s %s", agentType, agent.getDataAsString()));
                agentStr.append(tabs(tab)).append("}\n");
                break;
            case Constants.NONE:
                agentStr.append(String.format(AGENT_NONE, tabs(tab)));
                break;
            case Constants.KUBERNETES:
                agentStr.append(String.format(AGENT, tabs(tab)));
                agentStr.append(generateKubernetes(agent.getDataAsKubernetes(), tab + 1));
                agentStr.append(tabs(tab)).append("}\n");
                break;
            case Constants.ANY:
            default:
                agentStr.append(String.format(AGENT_ANY, tabs(tab)));
                break;

        }

        log.info("generateAgent() : Ended");
        return agentStr.toString();
    }

    private String generateDocker(Docker docker, int tab) {
        final StringBuilder dockerStr = new StringBuilder();
        if (!Objects.nonNull(docker)) {
            return dockerStr.toString();
        }
        dockerStr.append(tabs(tab)).append("docker{\n");
        if (docker.isAlwaysPull())
            dockerStr.append(tabs(tab + 1)).append(String.format(ALWAYS_PULL_TEMPLATE, docker.isAlwaysPull()));
        if (!StringUtils.isEmpty(docker.getArgs()))
            dockerStr.append(tabs(tab + 1)).append(String.format(ARGS_TEMPLATE, docker.getArgs()));
        if (!StringUtils.isEmpty(docker.getCustomWorkspace()))
            dockerStr.append(tabs(tab + 1)).append(String.format(CUSTOM_WORKSPACE_TEMPLATE, docker.getCustomWorkspace()));
        if (!StringUtils.isEmpty(docker.getImage()))
            dockerStr.append(tabs(tab + 1)).append(String.format(IMAGE_TEMPLATE, docker.getImage()));
        if (!StringUtils.isEmpty(docker.getLabel()))
            dockerStr.append(tabs(tab + 1)).append(String.format(LABEL_TEMPLATE, docker.getLabel()));
        if (!StringUtils.isEmpty(docker.getRegistryCredentialsId()))
            dockerStr.append(tabs(tab + 1)).append(String.format(REG_CRED_ID_TEMPLATE, docker.getRegistryCredentialsId()));
        if (!StringUtils.isEmpty(docker.getRegistryUrl()))
            dockerStr.append(tabs(tab + 1)).append(String.format(REG_URL_TEMPLATE, docker.getRegistryUrl()));
        if (docker.isReuseNode())
            dockerStr.append(tabs(tab + 1)).append(String.format(RE_USE_NODE_TEMPLATE, docker.isReuseNode()));
        dockerStr.append(tabs(tab)).append("}\n");
        return dockerStr.toString();
    }

    private String generateDockerFile(DockerFile dockerFile, int tab) {
        final StringBuilder dockerFileStr = new StringBuilder();
        if (!Objects.nonNull(dockerFile)) {
            return dockerFileStr.toString();
        }
        dockerFileStr.append(tabs(tab)).append("dockerfile{\n");
        if (!StringUtils.isEmpty(dockerFile.getAdditionalBuildArgs()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(AD_BUILD_ARGS_TEMPLATE, dockerFile.getAdditionalBuildArgs()));
        if (!StringUtils.isEmpty(dockerFile.getArgs()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(ARGS_TEMPLATE, dockerFile.getArgs()));
        if (!StringUtils.isEmpty(dockerFile.getCustomWorkspace()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(CUSTOM_WORKSPACE_TEMPLATE, dockerFile.getCustomWorkspace()));
        if (!StringUtils.isEmpty(dockerFile.getDir()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(DIR_TEMPLATE, dockerFile.getDir()));
        if (!StringUtils.isEmpty(dockerFile.getFilename()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(FILENAME_TEMPLATE, dockerFile.getFilename()));
        if (!StringUtils.isEmpty(dockerFile.getLabel()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(LABEL_TEMPLATE, dockerFile.getLabel()));
        if (!StringUtils.isEmpty(dockerFile.getRegistryCredentialsId()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(REG_CRED_ID_TEMPLATE, dockerFile.getRegistryCredentialsId()));
        if (!StringUtils.isEmpty(dockerFile.getRegistryUrl()))
            dockerFileStr.append(tabs(tab + 1)).append(String.format(REG_URL_TEMPLATE, dockerFile.getRegistryUrl()));
        if (dockerFile.isReuseNode())
            dockerFileStr.append(tabs(tab + 1)).append(String.format(RE_USE_NODE_TEMPLATE, dockerFile.isReuseNode()));
        dockerFileStr.append(tabs(tab)).append("}\n");
        return dockerFileStr.toString();
    }

    private String generateKubernetes(KubernetesAgent kubernetesAgent, int tab) {
        final StringBuilder kubernetesStr = new StringBuilder();
        if (Objects.nonNull(kubernetesAgent)) {
            kubernetesStr.append(tabs(tab)).append("kubernetes {\n");
            kubernetesStr.append(tabs(tab + 1)).append(String.format("defaultContainer '%s'%n", kubernetesAgent.getName().toLowerCase()));
            kubernetesStr.append(tabs(tab + 1)).append("yaml \"\"\"\n");
            kubernetesStr.append(kubernetesAgent.toYamlString());
            /*TAB removed due to yml indentation based issue (Do not use \t(TAB) for indentation)*/
            kubernetesStr.append("\"\"\"\n");
            kubernetesStr.append(tabs(tab)).append("}\n");
        }
        return kubernetesStr.toString();
    }
}
