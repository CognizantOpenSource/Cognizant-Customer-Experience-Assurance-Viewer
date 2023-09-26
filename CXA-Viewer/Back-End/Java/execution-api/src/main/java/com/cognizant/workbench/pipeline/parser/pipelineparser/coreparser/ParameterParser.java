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

import com.cognizant.workbench.pipeline.parser.model.Parameters;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants.PARAM_TYPE;

public class ParameterParser {

    public Parameters getParameters(List<String> paramsInfo) {
        Parameters parameters = new Parameters();
        CopyOnWriteArrayList<String> parametersList = new CopyOnWriteArrayList<>(paramsInfo);

        removeParamTag(parametersList);

        parametersList.removeAll(Collections.singleton(""));
        parametersList.removeIf(String::isEmpty);

        JsonArray jsonArray = new JsonArray();
        for (String str : parametersList) {
            JsonObject agentObj;
            if (str.startsWith("string")) {
                agentObj = getStringParam(str);
                jsonArray.add(agentObj);
            } else if (str.startsWith("boolean")) {
                agentObj = getBooleanParam(str);
                jsonArray.add(agentObj);
            } else if (str.startsWith("text")) {
                agentObj = getTextParam(str);
                jsonArray.add(agentObj);
            } else if (str.startsWith("password")) {
                agentObj = getPasswordParam(str);
                jsonArray.add(agentObj);
            } else if (str.startsWith("file")) {
                agentObj = getFileParam(str);
                jsonArray.add(agentObj);
            } else if (str.startsWith("choice")) {
                agentObj = getChoiceParam(str);
                jsonArray.add(agentObj);
            } else {
                parameters.getParametersUnhandled().add(str);
            }

        }

        String strParametersData = jsonArray.toString();
        strParametersData = strParametersData.substring(1, strParametersData.length() - 1);

        parameters.setParametersData(strParametersData);

        return parameters;
    }

    private void removeParamTag(CopyOnWriteArrayList<String> parametersList) {
        for (int i = parametersList.size() - 1; i >= 0; i--) {
            if (parametersList.get(i).equals("}")) {
                parametersList.remove(parametersList.get(i));
            } else {
                break;
            }
        }
    }

    private JsonObject getStringParam(String str) {
        JsonObject agentObj = new JsonObject();
        agentObj.addProperty(PARAM_TYPE, "string");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        if (null == value) return agentObj;
        String[] firstSplit = value.split(",");
        for (String splitStr : firstSplit) {
            String[] secondSplit = splitStr.split(":");
            String secondValue = null;
            Pattern regex = Pattern.compile("[\"'](.+)[\"']");
            Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
            while (regexMatcher.find()) {
                secondValue = regexMatcher.group(1);
            }
            agentObj.addProperty(secondSplit[0].trim(), secondValue);
        }
        return agentObj;
    }

    private JsonObject getBooleanParam(String str) {
        JsonObject agentObj = new JsonObject();
        agentObj.addProperty(PARAM_TYPE, "booleanParam");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        if (null == value) return agentObj;
        String[] firstSplit = value.split(",");
        for (String splitStr : firstSplit) {
            String[] secondSplit = splitStr.split(":");
            String secondValue = null;
            Pattern regex = Pattern.compile("[\"'](.+)[\"']");
            Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
            while (regexMatcher.find()) {
                secondValue = regexMatcher.group(1);
            }
            if (secondValue == null)
                secondValue = secondSplit[1].trim();
            agentObj.addProperty(secondSplit[0].trim(), secondValue);
        }
        return agentObj;
    }

    private JsonObject getTextParam(String str) {
        JsonObject agentObj = new JsonObject();
        agentObj.addProperty(PARAM_TYPE, "text");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        if (null == value) return agentObj;
        String[] firstSplit = value.split(",");
        for (String splitStr : firstSplit) {
            String[] secondSplit = splitStr.split(":");
            String secondValue = null;
            Pattern regex = Pattern.compile("[\"'](.+)[\"']");
            Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
            while (regexMatcher.find()) {
                secondValue = regexMatcher.group(1);
            }
            if (secondValue == null)
                secondValue = secondSplit[1].trim();
            agentObj.addProperty(secondSplit[0].trim(), secondValue.replace("'", ""));
        }
        return agentObj;
    }

    private JsonObject getPasswordParam(String str) {
        JsonObject agentObj = new JsonObject();
        agentObj.addProperty(PARAM_TYPE, "password");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        if (null == value) return agentObj;
        String[] firstSplit = value.split(",");
        for (String splitStr : firstSplit) {
            String[] secondSplit = splitStr.split(":");
            String secondValue = null;
            Pattern regex = Pattern.compile("[\"'](.+)[\"']");
            Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
            while (regexMatcher.find()) {
                secondValue = regexMatcher.group(1);
            }
            if (secondValue == null)
                secondValue = secondSplit[1].trim();
            agentObj.addProperty(secondSplit[0].trim(), secondValue);
        }
        return agentObj;
    }

    private JsonObject getFileParam(String str) {
        JsonObject agentObj = new JsonObject();
        agentObj.addProperty(PARAM_TYPE, "file");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }
        if (null == value) return agentObj;
        String[] firstSplit = value.split(",");
        for (String splitStr : firstSplit) {
            String[] secondSplit = splitStr.split(":");
            String secondValue = null;
            Pattern regex = Pattern.compile("[\"'](.+)[\"']");
            Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
            while (regexMatcher.find()) {
                secondValue = regexMatcher.group(1);
            }
            if (secondValue == null)
                secondValue = secondSplit[1].trim();
            agentObj.addProperty(secondSplit[0].trim(), secondValue);
        }
        return agentObj;
    }

    private JsonObject getChoiceParam(String str) {
        JsonObject agentObj = new JsonObject();
        List<String> tempChoiceList = new ArrayList<>();
        agentObj.addProperty(PARAM_TYPE, "choice");
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
        String value = null;
        while (m.find()) {
            value = m.group(1);
        }

        if (null == value) return agentObj;
        String firstString = value.substring(0, value.indexOf(","));
        tempChoiceList.add(firstString);
        Pattern p = Pattern.compile("(choices.*),(.*)");
        Matcher matcher = p.matcher(value);
        while (matcher.find()) {
            tempChoiceList.add(matcher.group(1).trim());
            tempChoiceList.add(matcher.group(2).trim());
        }

        for (int i = 0; i < tempChoiceList.size(); i++) {
            String tempChoice = tempChoiceList.get(i);
            if (tempChoice.startsWith("choices")) {
                String[] secondSplit = tempChoice.split(":");
                JsonArray array = getChoiceArray(secondSplit);
                agentObj.add(secondSplit[0].trim(), array);
            } else {
                String[] secondSplit = tempChoice.split(":");
                String secondValue = getChoiceSecondValue(secondSplit);
                agentObj.addProperty(secondSplit[0].trim(), secondValue);
            }
        }
        return agentObj;
    }

    private String getChoiceSecondValue(String[] secondSplit) {
        String secondValue = null;
        Pattern regex = Pattern.compile("[\"'](.+)[\"']");
        Matcher regexMatcher = regex.matcher(secondSplit[1].trim());
        while (regexMatcher.find()) {
            secondValue = regexMatcher.group(1);
        }
        if (secondValue == null) secondValue = secondSplit[1].trim();
        return secondValue;
    }

    private JsonArray getChoiceArray(String[] secondSplit) {
        String choicesValue = null;
        JsonArray array = new JsonArray();
        String choices = secondSplit[1];
        Pattern p1 = Pattern.compile("\\[(.*?)\\]");
        Matcher m1 = p1.matcher(choices);
        while (m1.find()) {
            choicesValue = m1.group(1);
        }
        if (choicesValue == null) return array;
        for (String eachChoice : choicesValue.split(",")) {
            array.add(new JsonPrimitive(eachChoice.replace("'", "")));
        }
        return array;
    }
}
