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

import com.cognizant.workbench.pipeline.parser.converter.ParameterConverter;
import com.cognizant.workbench.pipeline.parser.model.Parameters;
import com.cognizant.workbench.pipeline.parser.model.enums.StageDataType;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsAgent;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsEnvironment;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStageData;
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsStep;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants.*;

@Slf4j
public class StepParser {

    public JenkinsStageData getJenkinsStageData(Map<String, List<String>> stepMap) {

        JenkinsStageData jenkinsStageData = new JenkinsStageData();
        List<JenkinsStep> jenkinsSteps = null;
        JenkinsAgent jenkinsAgent;
        JenkinsEnvironment jenkinsEnvironment;

        if (stepMap.containsKey("Environment")) {
            log.info("Parsing Environment started");
            jenkinsEnvironment = new EnvironmentParser().getEnvironment(stepMap.get("Environment"));
            jenkinsStageData.setJenkinsEnvironment(jenkinsEnvironment);
            jenkinsStageData.setIsEnvironment(true);
            log.info("Parsing Environment completed");
        }

        if (stepMap.containsKey("Input")) {
            log.info("Parsing Input started");
            jenkinsStageData.setInput(true);
            String inputData = readInputData(stepMap.get("Input"));
            jenkinsStageData.setInputData(inputData);
            log.info("Parsing Input completed");
        }

        if (stepMap.containsKey("Options")) {
            log.info("Parsing Options started");
            jenkinsStageData.setOptions(true);
            String optionsData = readOptions(stepMap.get("Options"));
            jenkinsStageData.setOptionsData(optionsData);
            log.info("Parsing Options completed");
        }


        if (stepMap.containsKey("Agent")) {
            log.info("Parsing Agent started");
            jenkinsAgent = readAgent(stepMap.get("Agent"));
            jenkinsStageData.setIsAgent(true);
            jenkinsStageData.setAgent(jenkinsAgent);
            log.info("Parsing Agent completed");
        }

        if (stepMap.containsKey("When")) {
            log.info("Parsing When started");
            jenkinsStageData.setWhen(true);
            String whenData = readWhen(stepMap.get("When"));
            jenkinsStageData.setWhenData(whenData);
            log.info("Parsing When completed");
        }

        if (stepMap.containsKey("Step")) {
            log.info("Parsing Steps started");
            jenkinsSteps = readSteps(stepMap.get("Step"));
            jenkinsStageData.setStep(true);
            getStageDataFromStep(jenkinsStageData, jenkinsSteps.get(0).getScripts());
            log.info("Parsing Steps completed");
        }

        jenkinsStageData.setSteps(jenkinsSteps);
        return jenkinsStageData;
    }

    private String readWhen(List<String> whenList) {
        String whenStr;
        removeWhenTag(whenList);

        JSONObject whenObj = new JSONObject();
        JSONObject whenData = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (String str : whenList) {
            int commas = (int) str.chars().filter(c -> c == (int) ',').count();
            if (PipelineUtil.isPipelineTag(str, PipelineConstants.ALLOF)) {
                whenData.put("type", "all");
            } else if (PipelineUtil.isPipelineTag(str, PipelineConstants.ANYOF)) {
                whenData.put("type", "any");
            } else if (PipelineUtil.isPipelineTag(str, PipelineConstants.NOT)) {
                whenData.put("type", "not");
            } else if (str.startsWith("beforeAgent") || str.startsWith("beforeInput")) {
                String[] strArr = str.split(" ");
                whenData.put(strArr[0], strArr[1]);
            } else if (commas == 0 && !str.contains(",")) {
                jsonArray.put(commasZero(str));
            } else if (commas == 1) {
                jsonArray.put(commasOnce(str));
            } else if (commas > 1) {
                jsonArray.put(commasMoreThanOne(str));
            }
        }
        whenData.put("cases", jsonArray);
        whenObj.put("when", whenData);

        whenStr = whenObj.toString();
        whenStr = whenStr.substring(1, whenStr.length() - 1);
        return whenStr;
    }

    private JSONObject commasMoreThanOne(String str) {
        JSONObject valueObj = new JSONObject();
        JSONObject caseObj = new JSONObject();
        String[] strArr = str.split(",");

        for (int i = 0; i < strArr.length; i++) {
            String[] eachValues = strArr[i].split(":");
            if (i == 0) {
                caseObj.put(CASE_TYPE, eachValues[0].split(" ")[0]);
                valueObj.put(eachValues[0].split(" ")[1], eachValues[1].replace("'", "").trim());
            } else {
                valueObj.put(eachValues[0].trim(), eachValues[1].replace("'", "").trim());
            }
        }
        caseObj.put(VALUE, valueObj);
        caseObj.put(CASE_VALUE, DEFAULT);
        return caseObj;
    }

    private JSONObject commasOnce(String str) {
        JSONObject valueObj = new JSONObject();
        JSONObject caseObj = new JSONObject();
        String[] strArr = str.split(",");
        //1st Array
        String[] eachValues = strArr[0].split(":");
        caseObj.put("caseType", eachValues[0].split(" ")[0]);
        valueObj.put(eachValues[0].split(" ")[1], eachValues[1].replace("'", "").trim());
        //2nd Array
        String[] eachValues1 = strArr[1].split(":");
        valueObj.put(eachValues1[0].trim(), eachValues1[1].replace("'", "").trim());
        caseObj.put(VALUE, valueObj);
        caseObj.put(CASE_VALUE, DEFAULT);
        return caseObj;
    }

    private JSONObject commasZero(String str) {
        JSONObject caseObj = new JSONObject();
        String[] strArr = str.split(" ");
        Matcher m = Pattern.compile("[\"'](.+)[\"']").matcher(strArr[1].trim());
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        caseObj.put("caseType", strArr[0]);
        caseObj.put(VALUE, value);
        caseObj.put("caseValue", "default");
        return caseObj;
    }

    private void removeWhenTag(List<String> whenList) {
        if (PipelineUtil.isPipelineTag(whenList.get(0), PipelineConstants.WHEN))
            whenList.remove(0);

        whenList.removeAll(Collections.singleton(""));

        for (int i = whenList.size() - 1; i >= 0; i--) {
            if (whenList.get(i).equals("}")) {
                whenList.remove(whenList.get(i));
            } else {
                break;
            }
        }
    }

    private String readOptions(List<String> optionsList) {
        String optionStr;
        if (PipelineUtil.isPipelineTag(optionsList.get(0), PipelineConstants.OPTIONS))
            optionsList.remove(0);

        optionsList.removeAll(Collections.singleton(""));

        for (int i = optionsList.size() - 1; i >= 0; i--) {
            if (optionsList.get(i).equals("}")) {
                optionsList.remove(optionsList.get(i));
            } else {
                break;
            }
        }
        JSONObject optionObj = new JSONObject();
        JSONObject optionData;
        JSONArray array = new JSONArray();

        for (String str : optionsList) {
            optionData = new JSONObject();
            if (str.contains("retry")) {
                optionData.put("name", "retry");
                Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
                while (m.find()) {
                    optionData.put("value", m.group(1));
                }
            } else if (str.contains(" ")) {
                String[] values = str.split(" ");
                optionData.put("name", values[0]);
                optionData.put("value", values[1]);
            } else {
                optionData.put("name", str);
            }
            array.put(optionData);
        }
        optionObj.put("stageOptions", array);

        optionStr = optionObj.toString();
        optionStr = optionStr.substring(1, optionStr.length() - 1);

        return optionStr;
    }

    private String readInputData(List<String> inputList) {
        String inputStr;
        removeInputTag(inputList);

        inputList.removeAll(Collections.singleton(""));
        JSONObject inputObj = new JSONObject();
        JSONObject inputData = new JSONObject();

        int i = 0;
        while ( i < inputList.size()) {
            String input = inputList.get(i);
            if (input.equals("}")) continue;
            if (PipelineUtil.isPipelineTag(input, PipelineConstants.PARAMETERS)) {
                int cnt = i + 1;
                List<String> paramsList = new ArrayList<>();
                while (cnt < inputList.size()) {
                    if (!inputList.get(cnt).equals("}")) paramsList.add(inputList.get(cnt));
                    cnt++;
                }
                Parameters parameters = new ParameterParser().getParameters(paramsList);
                ParameterConverter parameterConverter = new ParameterConverter(parameters);
                String out = parameterConverter.getParametersFtl();
                JSONObject jsonObject = new JSONObject("{" + out + "}");
                inputData.put("parameters", jsonObject.get("parameters"));
                i = cnt - 1;
            } else {
                JSONObject jsonObject = readInput(input);
                jsonObject.keySet().stream().forEach(key -> inputData.put(key, jsonObject.get(key)));
            }
            i++;
        }
        inputObj.put("input", inputData);

        inputStr = inputObj.toString();
        inputStr = inputStr.substring(1, inputStr.length() - 1);

        return inputStr;
    }

    private void removeInputTag(List<String> inputList) {
        if (PipelineUtil.isPipelineTag(inputList.get(0), PipelineConstants.INPUT))
            inputList.remove(0);
    }

    private JSONObject readInput(String input) {
        JSONObject inputData = new JSONObject();
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(input.trim());
        String value = null;
        String name = null;
        while (m.find()) {
            value = m.group(1);
        }
        name = input.replace("\"" + value + "\"", "");
        inputData.put(name, value);
        return inputData;
    }

    private JenkinsAgent readAgent(List<String> agentList) {
        return new AgentParser().getAgent(agentList);
    }

    private void getStageDataFromStep(JenkinsStageData jenkinsStageData, List<String> scripts) {

        final Map<Character, Character> closeToOpen = new HashMap<>();
        closeToOpen.put('}', '{');

        String joinStr = String.join("", scripts);

        if (!PipelineUtil.isBalanced(joinStr, new LinkedList<>(), closeToOpen)) {
            for (int i = scripts.size() - 1; i >= 0; i--) {
                if (scripts.get(i).equals("}")) {
                    scripts.remove(scripts.get(i));
                } else {
                    break;
                }
            }
        }

        if (scripts.get(0).contains("git")) {
            jenkinsStageData.setStageDataType(StageDataType.GIT);
            String line = scripts.get(0);
            //Below Logic also has to be changed accordingly
            String[] pipelineArray = line.split("[\\'||\\']");
            jenkinsStageData.setType("git");
            jenkinsStageData.setBranch(pipelineArray[1]);
            jenkinsStageData.setRepo(pipelineArray[3]);
        } else {
            jenkinsStageData.setStageDataType(StageDataType.TEST);
            jenkinsStageData.setType("UI");
            jenkinsStageData.setClient("Tool");
        }
    }

    private List<JenkinsStep> readSteps(List<String> stepList) {

        List<JenkinsStep> jenkinsSteps = new ArrayList<>();
        JenkinsStep jenkinsStep = new JenkinsStep();
        List<String> codeList = new ArrayList<>();

        if (PipelineUtil.isPipelineTag(stepList.get(0), PipelineConstants.STEPS)) {
            stepList.remove(0);
            if (stepList.get(stepList.size() - 1).equals("}")) {
                stepList.remove(stepList.size() - 1);
            }
        }

        int i = 0;
        while ( i < stepList.size()) {
            String eachStep = stepList.get(i).trim();
            if (PipelineUtil.isPipelineTag(eachStep, PipelineConstants.SCRIPT)) {
                stepList.remove(eachStep);
                if (stepList.get(stepList.size() - 1).equals("}")) {
                    stepList.remove(stepList.size() - 1);
                }
            }

            jenkinsStep.setType(PipelineConstants.GROOVY_SCRIPT);
            jenkinsStep.setPlatform("");
            int cnt = i;
            while ((cnt != stepList.size())
                    && (!PipelineUtil.isPipelineTag(stepList.get(cnt), PipelineConstants.SCRIPT))) {
                codeList.add(stepList.get(cnt).trim());
                cnt++;
                if (cnt == stepList.size()) {
                    break;
                }
            }
            i = cnt - 1;
            jenkinsStep.setScripts(codeList);
            jenkinsSteps.add(jenkinsStep);
            i++;
        }

        return jenkinsSteps;
    }

}
