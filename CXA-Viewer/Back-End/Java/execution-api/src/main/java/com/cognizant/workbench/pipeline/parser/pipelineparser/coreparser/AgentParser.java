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

package com.cognizant.workbench.pipeline.parser.pipelineparser.coreparser;

import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsAgent;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants.IMAGE;

@Slf4j
public class AgentParser {
    public JenkinsAgent getAgent(List<String> agentList) {

        JenkinsAgent agent = new JenkinsAgent();

        if (agentList.get(0).equalsIgnoreCase("agent any")) {
            agent.setAgentDataExists(false);
            agent.setDocker(false);
            agent.setKubernetes(false);

            agent.setId("any");
            agent.setName("agentAny");
            agent.setType("Agent");
            agent.setAgentType("any");
            return agent;
        }

        removeAgentTag(agentList);

        List<String> cleanseAgentList = new ArrayList<>(agentList);

        agent.setAgentDataExists(true);
        if (PipelineUtil.isPipelineTag(cleanseAgentList.get(0), PipelineConstants.DOCKER)) {
            agent = getDockerAgent(cleanseAgentList);
        } else if (PipelineUtil.isPipelineTag(cleanseAgentList.get(0), PipelineConstants.KUBERNETES)) {
            agent = getKubernetes(cleanseAgentList);
        } else {
            agent.getAgentUnhandled().addAll(cleanseAgentList);
        }
        return agent;
    }

    private void removeAgentTag(List<String> agentList) {
        if (PipelineUtil.isPipelineTag(agentList.get(0), PipelineConstants.AGENT))
            agentList.remove(0);

        for (int i = agentList.size() - 1; i >= 0; i--) {
            if (agentList.get(i).equals("}")) {
                agentList.remove(agentList.get(i));
            } else {
                break;
            }
        }
    }

    private JenkinsAgent getDockerAgent(List<String> cleanseAgentList){
        JenkinsAgent agent = new JenkinsAgent();
        JSONObject agentObj = new JSONObject();
        String strAgentData;

        log.info("Parsing Agent Docker Data");
        cleanseAgentList.remove(0);
        agent.setId("docker");
        agent.setName("Docker");
        agent.setAgentType("docker");
        agent.setType("agent");
        agent.setDocker(true);

        //setting default values
        agentObj.put("alwaysPull", false);
        agentObj.put("reuseNode", false);

        int i = 0;
        while (i < cleanseAgentList.size()) {
            if (cleanseAgentList.get(i).contains("args")) {
                Matcher m = Pattern.compile("'([^']*)'").matcher(cleanseAgentList.get(i));
                while (m.find()) {
                    String args = m.group(1);
                    agentObj.put("args", args);
                }
            } else if (cleanseAgentList.get(i).contains(IMAGE)) {
                Matcher m = Pattern.compile("'([^']*)'").matcher(cleanseAgentList.get(i));
                while (m.find()) {
                    String image = m.group(1);
                    agentObj.put("image", image);
                }
            } else if (cleanseAgentList.get(i).contains(" ")) {
                String[] values = cleanseAgentList.get(i).split(" ");
                agentObj.put(values[0], values[1]);
            }
            i++;
        }
        strAgentData = agentObj.toString();
        strAgentData = strAgentData.substring(1, strAgentData.length() - 1);
        agent.setAgentData(strAgentData);
        return agent;
    }

    private JenkinsAgent getKubernetes(List<String> cleanseAgentList) {
        JenkinsAgent agent = new JenkinsAgent();
        String strAgentData;

        log.info("Parsing Agent Kubernetes Data");
        cleanseAgentList.remove(0);
        agent.setId("kubernetes");
        agent.setName("Kubernetes");
        agent.setAgentType("kubernetes");
        agent.setType("agent");
        agent.setKubernetes(true);

        JSONObject dataObj = new JSONObject();
        // Pending Logic below - Change Required
        dataObj.put("name", "update-name");
        dataObj.put("image", "update-image");
        dataObj.put("command", "update-command");
        dataObj.put("replicas", 2);
        dataObj.put("replicaId", 1);
        dataObj.put("ttyEnabled", true);

        strAgentData = dataObj.toString();
        strAgentData = strAgentData.substring(1, strAgentData.length() - 1);
        agent.setAgentData(strAgentData);
        return agent;
    }
}
