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

import com.cognizant.workbench.error.InvalidValueException;
import com.cognizant.workbench.pipeline.parser.model.enums.StageDataType;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStage;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStageData;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StageParser {
    public List<JenkinsStage> getJenkinsStage(List<List<String>> stages) {

        List<JenkinsStage> jenkinsStages = new ArrayList<>();
        log.info("Stages Parsing started");

        for (List<String> singleStage : stages) {
            try {
                JenkinsStage jenkinsStage = new JenkinsStage();
                JenkinsStageData jenkinsStageData;
                Map<String, List<String>> stepMap = new HashMap<>();
                getMainStageDetails(singleStage, jenkinsStage, stepMap);
                if (jenkinsStage.isParallel()) {
                    // Change below - Passing Empty Object to avoid error
                    jenkinsStageData = new JenkinsStageData();
                    jenkinsStageData.setStageDataType(StageDataType.TEST);
                    jenkinsStage.setStageData(jenkinsStageData);
                } else {

                    jenkinsStageData = new StepParser().getJenkinsStageData(stepMap);
                    jenkinsStage.setStageData(jenkinsStageData);
                }
                jenkinsStages.add(jenkinsStage);
                log.info("Stage   parsing completed");

            } catch (Exception e) {
                log.info("Error while Parsing Stages Parsing " + e.getMessage());
                throw new InvalidValueException(" Error while Parsing Stages Parsing ");
            }
        }
        return jenkinsStages;
    }

    private void getMainStageDetails(List<String> singleStage, JenkinsStage jenkinsStage, Map<String, List<String>> stepMap) {
        int i = 0;
        while (i < singleStage.size()){
            String line = singleStage.get(i).trim();
            if (PipelineUtil.isStage(line)) {
                boolean isBreak = getSingleStageWithParallel(singleStage, jenkinsStage, line);
                if (isBreak) break;
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.OPTIONS)) {
                log.info("Options data adding started to stage");
                List<String> optionsInfo = new ArrayList<>();
                int cnt = getOptions(singleStage, optionsInfo, i);
                i = cnt - 1;
                stepMap.put("Options", optionsInfo);
                log.info("Options Data adding completed to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.ENVIRONMENT)) {
                log.info("Environment Data adding started to stage");
                List<String> envInfo = new ArrayList<>();
                int cnt = getEnvironment(singleStage, envInfo, i);
                i = cnt - 1;
                stepMap.put("Environment", envInfo);
                log.info("Environment Data adding completed to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.INPUT)) {
                log.info("Input Data adding started to stage");
                List<String> inputInfo = new ArrayList<>();
                int cnt = getInput(singleStage, inputInfo, i);
                i = cnt - 1;
                stepMap.put("Input", inputInfo);
                log.info("Input Data adding completed to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.AGENT)) {
                log.info("Agent Data adding started to stage");
                jenkinsStage.setAgent(true);
                List<String> agentInfo = new ArrayList<>();
                int cnt = getAgent(singleStage, agentInfo, i);
                i = cnt - 1;
                stepMap.put("Agent", agentInfo);
                log.info("Agent Data adding completed to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.WHEN)) {
                log.info("When Data adding started to stage");
                List<String> whenInfo = new ArrayList<>();
                int cnt = getWhen(singleStage, whenInfo, i);
                i = cnt - 1;
                stepMap.put("When", whenInfo);
                log.info("When Data adding completed to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.STEPS)) {
                log.info("Steps Data adding started to stage");
                List<String> stepsInfo = new ArrayList<>();
                int cnt = getSteps(singleStage, stepsInfo, i);
                i = cnt - 1;
                stepMap.put("Step", stepsInfo);
                log.info("Steps Data completed started to stage");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.POST)) {
                log.info("Post Data adding started to stage");
                List<String> postInfo = new ArrayList<>();
                int cnt = getPost(singleStage, postInfo, i);
                i = cnt - 1;
                stepMap.put("Post", postInfo);
                log.info("Post Data adding completed to stage");
            } else {
                jenkinsStage.getStageUnhandled().add(line);
            }
            i++;
        }
    }

    private boolean getSingleStageWithParallel(List<String> singleStage, JenkinsStage jenkinsStage, String line) {
        boolean isBreak = false;
        Matcher matcher = Pattern.compile("[\"'](.+)[\"']").matcher(line);
        if (matcher.find()) {
            String stageName = matcher.group(1);
            log.info("Stage " + stageName + " parsing started");
            jenkinsStage.setId(stageName);
            jenkinsStage.setDescription("");
            if (PipelineUtil.isPipelineTag(singleStage.get(1), PipelineConstants.PARALLEL)) {
                //Change below code, hard coded
                singleStage.remove(0);
                singleStage.remove(0);
                jenkinsStage.setParallel(true);
                jenkinsStage.setType("group");
                jenkinsStage.setToolId("");
                List<JenkinsStage> childStages = getChildJenkinsStage(singleStage);
                jenkinsStage.setJenkinsStages(childStages);
                isBreak = true;
            } else {
                if (singleStage.get(singleStage.size() - 1).equals("}")) {
                    singleStage.remove(singleStage.size() - 1);
                }
                jenkinsStage.setType("test");
                jenkinsStage.setToolId("test-test");
                jenkinsStage.setParallel(false);
            }
        }
        return isBreak;
    }

    public List<JenkinsStage> getChildJenkinsStage(List<String> allParallelStages) {
        List<List<String>> allChildStages = PipelineUtil.splitPipeline(allParallelStages);
        allChildStages.removeAll(Collections.singleton(" "));
        allChildStages.removeIf(List::isEmpty);
        List<JenkinsStage> childJenkinsStages = new ArrayList<>();
        log.info("Stages Parsing started");

        for (List<String> singleStage : allChildStages) {
            JenkinsStage childJenkinsStage = new JenkinsStage();
            JenkinsStageData childJenkinsStageData;
            Map<String, List<String>> stepMap = new HashMap<>();
            try {
                getSingleStageDetails(singleStage, childJenkinsStage, stepMap);
                childJenkinsStageData = new StepParser().getJenkinsStageData(stepMap);
                childJenkinsStage.setStageData(childJenkinsStageData);
                childJenkinsStages.add(childJenkinsStage);
            } catch (Exception e) {
                log.info("Error while Parsing Stages Parsing " + e.getMessage());
                throw new InvalidValueException(" Error while Parsing Stages Parsing ");
            }
        }
        return childJenkinsStages;
    }

    private void getSingleStageDetails(List<String> singleStage, JenkinsStage childJenkinsStage, Map<String, List<String>> stepMap) {
        int i = 0;
        while (i < singleStage.size()) {
            String line = singleStage.get(i).trim();
            if (PipelineUtil.isStage(line)) {
                childJenkinsStage = getSingleStage(singleStage, line, childJenkinsStage);
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.OPTIONS)) {
                log.info("Options Data adding to Child stage started");
                List<String> optionsInfo = new ArrayList<>();
                int cnt = getOptions(singleStage, optionsInfo, i);
                i = cnt - 1;
                stepMap.put("Options", optionsInfo);
                log.info("Options Data adding to child stage started");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.ENVIRONMENT)) {
                log.info("Environment Data adding to child stage started");
                List<String> envInfo = new ArrayList<>();
                int cnt = getEnvironment(singleStage, envInfo, i);
                i = cnt - 1;
                stepMap.put("Environment", envInfo);
                log.info("Environment Data adding to child stage completed");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.INPUT)) {
                log.info("Input Data adding to child stage started");
                List<String> inputInfo = new ArrayList<>();
                int cnt = getInput(singleStage, inputInfo, i);
                i = cnt - 1;
                stepMap.put("Input", inputInfo);
                log.info("Input Data adding to child stage completed");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.AGENT)) {
                log.info("Agent Data adding to child stage started");
                childJenkinsStage.setAgent(true);
                List<String> agentInfo = new ArrayList<>();
                int cnt = getAgent(singleStage, agentInfo, i);
                i = cnt - 1;
                stepMap.put("Agent", agentInfo);
                log.info("Agent Data adding to child stage Completed");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.WHEN)) {
                log.info("When Data adding to child stage started");
                List<String> whenInfo = new ArrayList<>();
                int cnt = getWhen(singleStage, whenInfo, i);
                i = cnt - 1;
                stepMap.put("When", whenInfo);
                log.info("When Data adding to child stage completed");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.STEPS)) {
                log.info("Steps Data adding to child stage started");
                List<String> stepsInfo = new ArrayList<>();
                int cnt = getSteps(singleStage, stepsInfo, i);
                i = cnt - 1;
                stepMap.put("Step", stepsInfo);
                log.info("Steps Data adding to child stage completed");
            } else if (PipelineUtil.isPipelineTag(line, PipelineConstants.POST)) {
                log.info("Post Data adding to child stage started");
                List<String> postInfo = new ArrayList<>();
                int cnt = getPost(singleStage, postInfo, i);
                i = cnt - 1;
                stepMap.put("Post", postInfo);
                log.info("Post Data adding to child stage completed");
            } else {
                childJenkinsStage.getStageUnhandled().add(line);
            }
            i++;
        }
    }

    private JenkinsStage getSingleStage(List<String> singleStage, String line, JenkinsStage childJenkinsStage) {
        Matcher matcher = Pattern.compile("[\"'](.+)[\"']").matcher(line);
        if (matcher.find()) {
            String stageName = matcher.group(1);
            log.info("Stage " + stageName + " parsing started");
            childJenkinsStage.setId(stageName);
            childJenkinsStage.setType("test");
            childJenkinsStage.setToolId("test-test");
            childJenkinsStage.setDescription("");
            childJenkinsStage.setParallel(false);
            if (singleStage.get(singleStage.size() - 1).equals("}")) {
                singleStage.remove(singleStage.size() - 1);
            }
        }
        return childJenkinsStage;
    }

    private int getPost(List<String> singleStage, List<String> postInfo, int cnt) {
        while (!(PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))
                && (cnt != singleStage.size())) {
            postInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getSteps(List<String> singleStage, List<String> stepsInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.POST))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))) {
            stepsInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getWhen(List<String> singleStage, List<String> whenInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))) {
            whenInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getAgent(List<String> singleStage, List<String> agentInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))) {
            agentInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getInput(List<String> singleStage, List<String> inputInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))) {
            inputInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getOptions(List<String> singleStage, List<String> optionsInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.ENVIRONMENT))) {
            optionsInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }

    private int getEnvironment(List<String> singleStage, List<String> envInfo, int cnt) {
        while ((!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.STEPS))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.WHEN))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.INPUT))
                && (!PipelineUtil.isPipelineTag(singleStage.get(cnt), PipelineConstants.OPTIONS))) {
            envInfo.add(singleStage.get(cnt).trim());
            cnt++;
            if (cnt == singleStage.size()) {
                break;
            }
        }
        return cnt;
    }
}
