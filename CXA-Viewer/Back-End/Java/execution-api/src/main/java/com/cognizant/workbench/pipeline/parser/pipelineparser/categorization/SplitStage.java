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

package com.cognizant.workbench.pipeline.parser.pipelineparser.categorization;

import com.cognizant.workbench.error.InvalidValueException;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.pipeline.PipelineScript;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import com.cognizant.workbench.pipeline.parser.utils.ReadPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants.MAX_STAGE_COUNT;

@Slf4j
public abstract class SplitStage {

    protected final List<String> agentList = new ArrayList<>();
    private final List<String> optionsList = new ArrayList<>();
    private final List<String> paramsList = new ArrayList<>();
    protected final List<String> environmentList = new ArrayList<>();
    private final List<String> toolsList = new ArrayList<>();
    private final List<String> triggerList = new ArrayList<>();
    protected final List<List<String>> stagesList = new ArrayList<>();
    protected final List<String> postList = new ArrayList<>();

    protected final PipelineScript pipelineModel = new PipelineScript();


    protected List<String> getPipelineList(String pipelineData) {
        ReadPipeline readPipeline = new ReadPipeline();
        return readPipeline.getPipelineDataAsList(pipelineData);
    }

    protected List<List<String>> splitPipeline(List<String> pipelineRawList) {

        List<List<String>> stages = new ArrayList<>();
        log.info("Splitting pipeline into list of stages");
        List<String> stage = new ArrayList<>();
        try {
            for (String s : pipelineRawList) {

                String dataClean = s.trim();
                stage.add(dataClean);

                if (dataClean.contains("stages")) {
                    stages.add(stage);
                    stage = new ArrayList<>();
                }

                if (PipelineUtil.isStage(dataClean)) {
                    stage.remove(dataClean);
                    stages.add(stage);
                    stage = new ArrayList<>();
                    stage.add(dataClean);
                }
            }
            // Handle to add the last Stage Data into List
            if (stage.size() > 1)
                stages.add(stage);
            log.info("Split into List of stages was successful");

        } catch (Exception e) {
            log.error("Error while Splitting pipeline into List of Stages " + e.getMessage());
            throw new InvalidValueException(" Error while Splitting pipeline into List of Stages ");
        }
        return stages;
    }

    protected void drillDownPipeline(List<List<String>> stagesList) {
        log.info("Drill Down & polishing the pipeline list");
        List<List<String>> polishStageList = new ArrayList<>();
        try {
            stagesList.removeIf(List::isEmpty);
            int size = stagesList.size();
            if (size > MAX_STAGE_COUNT) size = MAX_STAGE_COUNT;
            for (int i = 0; i < size; i++) {
                // Handle for Agent ,Option, Parameters Environment or anything above stages tag
                if (i == 0) {
                    getPipelineList(stagesList.get(i));
                //Handle for Post as Post details will be added to last list
                }else if (i == size - 1) {
                    List<List<String>> lastList = getLastStageList(stagesList.get(i));
                    if (lastList.size() > 1) {
                        polishStageList.add(lastList.get(0));
                        postList.addAll(lastList.get(1));
                    } else {
                        polishStageList.addAll(lastList);
                    }
                } else if (i == 1) {
                    List<String> strings = stagesList.get(i);
                    strings.stream().filter(s -> s.contains("stage")).findFirst().ifPresent(s -> polishStageList.add(strings));
                } else {
                    polishStageList.add(stagesList.get(i));
                }
            }
            this.stagesList.addAll(polishStageList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while drill down & polishing the stages list  " + e.getMessage());
            throw new InvalidValueException(" Error while drill down & polishing the stages list ");
        }
    }

    protected void handleParallelStage() {

        final Map<Character, Character> closeToOpen = new HashMap<>();
        closeToOpen.put('}', '{');

        stagesList.removeAll(Collections.singleton(" "));
        stagesList.removeAll(Collections.singleton(""));
        stagesList.removeIf(List::isEmpty);

        List<List<String>> allStages = new ArrayList<>();
        int i = 0;
        while (i < stagesList.size()) {
            if (PipelineUtil.isPipelineTag(stagesList.get(i).get(1), PipelineConstants.PARALLEL)) {
                int cnt = i;
                StringBuilder firstJoin = new StringBuilder(String.join("", stagesList.get(cnt)));
                cnt = getStageDetails(stagesList, firstJoin, allStages, closeToOpen, cnt);
                i = cnt;
            } else {
                allStages.add(stagesList.get(i));
            }
            i++;
        }
        pipelineModel.setStagesInfo(allStages);
    }

    private int getStageDetails(List<List<String>> stagesList, StringBuilder firstJoin, List<List<String>> allStages,
                                Map<Character, Character> closeToOpen, int cnt) {
        List<String> parallelStage = new ArrayList<>();
        while (cnt < stagesList.size()) {
            if (!PipelineUtil.isBalanced(firstJoin.toString(), new LinkedList<>(), closeToOpen)) {
                parallelStage.addAll(stagesList.get(cnt));
                cnt++;
                if (cnt < stagesList.size()) {
                    String innerJoin = String.join("", stagesList.get(cnt));
                    firstJoin.append(innerJoin);
                }
                // Handle if there are no further stages after parallel stage
                if (cnt == stagesList.size()) {
                    allStages.add(parallelStage);
                    break;
                }
            } else {
                parallelStage.addAll(stagesList.get(cnt));
                allStages.add(parallelStage);
                break;
            }
        }
        return cnt;
    }


    protected void getPipelineList(List<String> pipelineListData) {

        log.info("Getting pipeline data before stages like agent, environment..");
        List<String> pipelineList = pipelineListData.stream().filter(s -> !StringUtils.isEmpty(s.trim())).collect(Collectors.toList());

        pipelineList = removePipelineTag(pipelineList);
        int i = 0;
        while (i < pipelineList.size()) {
            String comp = pipelineList.get(i).trim();
            if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.STAGES)) {
                break;
            } else if (comp.contains(PipelineConstants.AGENT_ANY)) {
                agentList.add(comp);
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.AGENT)
                    && !pipelineList.get(i + 1).trim().equals("")) {
                agentList.add(comp);
                int cnt = getAgent(agentList, pipelineList, i+1);
                i = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.ENVIRONMENT)) {
                int cnt = getEnvironment(environmentList, pipelineList, i+1);
                i = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.OPTIONS)) {
                int cnt = getOptions(optionsList, pipelineList, i+1);
                i = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.PARAMETERS)) {
                int cnt = getParameters(paramsList, pipelineList, i+1);
                i = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.TOOLS)) {
                int cnt = getTools(toolsList, pipelineList, i+1);
                i = cnt - 1;
            } else if (PipelineUtil.isPipelineTag(pipelineList.get(i), PipelineConstants.TRIGGERS)) {
                int cnt = getTriggers(triggerList, pipelineList, i+1);
                i = cnt - 1;
            }
            i++;
        }

        log.info(" Pipeline data before stages like agent, environment.. are retrieved");
    }

    private int getTriggers(List<String> triggerList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.PARAMETERS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TRIGGERS))) {
            triggerList.add(pipelineList.get(cnt));
            cnt++;
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    private int getTools(List<String> toolsList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.PARAMETERS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TRIGGERS))) {
            toolsList.add(pipelineList.get(cnt));
            cnt++;
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    private int getParameters(List<String> paramsList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TOOLS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TRIGGERS))) {
            paramsList.add(pipelineList.get(cnt));
            cnt++;
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    private int getOptions(List<String> optionsList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.PARAMETERS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TOOLS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TRIGGERS))) {
            optionsList.add(pipelineList.get(cnt));
            cnt++;
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    private int getEnvironment(List<String> environmentList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.AGENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.PARAMETERS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TOOLS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.TRIGGERS))) {
            environmentList.add(pipelineList.get(cnt));
            cnt++;
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    private int getAgent(List<String> agentList, List<String> pipelineList, int cnt) {
        String str = pipelineList.get(cnt);
        while ((!PipelineUtil.isPipelineTag(str, PipelineConstants.STAGES))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.ENVIRONMENT))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.OPTIONS))
                && (!PipelineUtil.isPipelineTag(str, PipelineConstants.PARAMETERS))) {
            agentList.add(pipelineList.get(cnt).trim());
            cnt++;
            if (cnt == pipelineList.size()) {
                break;
            }
            str = pipelineList.get(cnt);
        }
        return cnt;
    }

    protected void cleansePipeline() {

        agentList.removeAll(Collections.singleton(" "));
        agentList.removeAll(Collections.singleton(""));
        agentList.removeIf(String::isEmpty);
        pipelineModel.getAgentInfo().addAll(agentList);

        environmentList.removeAll(Collections.singleton(" "));
        environmentList.removeAll(Collections.singleton(""));
        environmentList.removeIf(String::isEmpty);
        pipelineModel.getEnvironmentInfo().addAll(environmentList);

        optionsList.removeAll(Collections.singleton(" "));
        optionsList.removeAll(Collections.singleton(""));
        optionsList.removeIf(String::isEmpty);
        pipelineModel.setOptionsInfo(optionsList);

        paramsList.removeAll(Collections.singleton(" "));
        paramsList.removeAll(Collections.singleton(""));
        paramsList.removeIf(String::isEmpty);
        pipelineModel.setParamsInfo(paramsList);

        toolsList.removeAll(Collections.singleton(" "));
        toolsList.removeAll(Collections.singleton(""));
        toolsList.removeIf(String::isEmpty);
        pipelineModel.setToolsInfo(toolsList);


        triggerList.removeAll(Collections.singleton(" "));
        triggerList.removeAll(Collections.singleton(""));
        triggerList.removeIf(String::isEmpty);
        pipelineModel.setTriggerInfo(triggerList);

        // Below should be handled dynamically - Change required
        if (postList.contains(PipelineConstants.POST_BRACES)
                || postList.contains(PipelineConstants.POST_BRACES_SPACE)) {
            postList.removeAll(Collections.singleton(" "));
            postList.removeAll(Collections.singleton(""));
            postList.removeIf(String::isEmpty);
            pipelineModel.getPostInfo().addAll(postList);
        }
    }

    protected List<List<String>> getLastStageList(List<String> lastStageList) {

        log.info("Getting the Post information");

        List<List<String>> stages = new ArrayList<>();

        boolean isPostFound = false;

        List<String> stage = new ArrayList<>();
        int size = lastStageList.size();
        if (size > MAX_STAGE_COUNT) size = MAX_STAGE_COUNT;
        for (int i = 0; i < size; i++) {
            String line = lastStageList.get(i);
            stage.add(line);
            if (!isPostFound &&
                    line.equals("}") &&
                    lastStageList.get(i + 1).equals("}") &&
                    lastStageList.get(i + 2).equals("}")) {
                stage.add(line);
                stage.add(lastStageList.get(i + 1));
                stage.add(lastStageList.get(i + 1));

                isPostFound = true;
                stages.add(stage);
                stage = new ArrayList<>();
            }
        }
        // Handle to eliminate list creation if stages doesn't have post call at the end
        // If there is post after stages, data will be greater than 4
        if (stage.size() > 4) {
            stages.add(stage);
        }
        log.info("POST Information retrieved");

        return stages;
    }

    private List<String> removePipelineTag(List<String> pipelineList) {
        // To remove any commented sections - Change required
        if (pipelineList.get(0).startsWith("#")) pipelineList.remove(0);

        // To remove pipeline tag - Change required
        if (pipelineList.get(0).contains(PipelineConstants.PIPELINE)) {
            pipelineList.remove(0);
            if (pipelineList.get(pipelineList.size() - 1).equals("}")) pipelineList.remove(pipelineList.size() - 1);
        }
        return pipelineList;
    }

}
