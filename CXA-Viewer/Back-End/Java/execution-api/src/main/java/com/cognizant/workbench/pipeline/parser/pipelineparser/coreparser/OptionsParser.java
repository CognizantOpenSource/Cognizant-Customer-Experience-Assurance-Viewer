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

import com.cognizant.workbench.pipeline.parser.model.Options;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants.VALUE;

public class OptionsParser {

    public Options getOptions(List<String> optionsInfo) {
        final Map<Character, Character> closeToOpen = new HashMap<>();
        closeToOpen.put('(', ')');
        Options options = new Options();
        CopyOnWriteArrayList<String> optionList = new CopyOnWriteArrayList<>(optionsInfo);

        removeOptionsTag(optionList);
        optionList.removeAll(Collections.singleton(""));
        optionList.removeIf(String::isEmpty);

        JSONArray jsonArray = new JSONArray();
        for (String str : optionList) {
            JSONObject optionObj = new JSONObject();
            if (!PipelineUtil.isBalanced(str, new LinkedList<>(), closeToOpen)) {
                String value = "test";
                Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
                while (m.find()) {
                    value = m.group(1);
                }
                if (!value.equalsIgnoreCase("test")) {
                    JSONObject nameValueObj = getNonTestOptions(value, str);
                    nameValueObj.keySet().forEach(key -> optionObj.put(key, nameValueObj.get(key)));
                } else {
                    optionObj.put(VALUE, value);
                    optionObj.put("name", str.replace("()", ""));
                }
            } else if (str.contains(" ")) {
                String[] values = str.split(" ");
                optionObj.put("name", values[0]);
                optionObj.put(VALUE, values[1].replace("'", ""));
            } else {
                options.getOptionUnhandled().add(str);
            }
            jsonArray.put(optionObj);
        }

        String strOptionsData = jsonArray.toString();
        strOptionsData = strOptionsData.substring(1, strOptionsData.length() - 1);

        options.setOptionsData(strOptionsData);

        return options;
    }

    private JSONObject getNameValue(String value, String str) {
        JSONObject nameValueObj = new JSONObject();
        String[] values;
        String name;
        //Below code need to be changed later
        if (value.contains("(")) {
            value = value + ")";
            Matcher m1 = Pattern.compile("\\(([^)]+)\\)").matcher(value);
            while (m1.find()) {
                value = m1.group(1);
            }
        }
        values = value.split(",");
        JSONObject jsonObject = new JSONObject();
        for (String val : values) {
            jsonObject.put(val.split(":")[0].trim(), val.split(":")[1].trim().replace("'", ""));
        }
        name = str.replace("(" + value + ")", "");
        if (name.contains("(")) {
            String extraVal = null;
            Matcher m2 = Pattern.compile("\\(([^)]+)\\)").matcher(name);
            while (m2.find()) {
                extraVal = m2.group(1);
            }
            name = name.replace("(" + extraVal + ")", "");
        }
        nameValueObj.put("name", name);
        nameValueObj.put(VALUE, jsonObject);
        return nameValueObj;
    }

    private void removeOptionsTag(CopyOnWriteArrayList<String> optionList) {
        for (int i = optionList.size() - 1; i >= 0; i--) {
            if (optionList.get(i).equals("}")) {
                optionList.remove(optionList.get(i));
            } else {
                break;
            }
        }
    }

    private JSONObject getNonTestOptions(String value, String str) {
        JSONObject nameValueObj = new JSONObject();
        if (value.contains(",")) {
            nameValueObj = getNameValue(value, str);
        } else if (value.contains(":")) {
            nameValueObj.put(VALUE, value.split(":")[1].trim());
            nameValueObj.put("name", str.replace("(" + value + ")", ""));
        } else {
            nameValueObj.put(VALUE, value);
            nameValueObj.put("name", str.replace("(" + value + ")", ""));
        }
        return nameValueObj;
    }

}
