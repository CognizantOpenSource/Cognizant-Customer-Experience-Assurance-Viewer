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

import com.cognizant.workbench.pipeline.parser.model.PipelineTriggers;
import com.cognizant.workbench.pipeline.parser.pipelineparser.constants.PipelineConstants;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriggersParser {

    public PipelineTriggers getTriggers(List<String> triggersInfo) {
        PipelineTriggers triggers = new PipelineTriggers();
        removeTriggersTag(triggersInfo);

        JSONObject jsonObject = new JSONObject();
        for (String str : triggersInfo) {
            if (!str.equals("}")) {
                String value = null;
                Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
                while (m.find()) {
                    value = m.group(1);
                }
                jsonObject.put("name", str.replace("(" + value + ")", ""));
                JSONObject jsonObject1 = new JSONObject();
                if (null != value) {
                    for (String inside : split(value)) {
                        String[] insideValues = inside.split(":");
                        jsonObject1.put(insideValues[0].trim(), insideValues[1].replace("'", "").trim());
                    }
                }

                jsonObject.put("value", jsonObject1);
            }
        }

        String strTriggersData = jsonObject.toString();
        strTriggersData = strTriggersData.substring(1, strTriggersData.length() - 1);

        triggers.setTriggersData(strTriggersData);

        return triggers;
    }

    private void removeTriggersTag(List<String> triggersInfo) {
        if (PipelineUtil.isPipelineTag(triggersInfo.get(0), PipelineConstants.TRIGGERS)) {
            triggersInfo.remove(0);
            if (triggersInfo.remove(triggersInfo.size() - 1).equalsIgnoreCase("}")) {
                triggersInfo.remove(triggersInfo.size() - 1);
            }
        }
    }

    public String[] split(String s) {
        int index = 0;
        for (int i = 0; i < 2; i++)
            index = s.indexOf(",", index + 1);

        return new String[]{
                s.substring(0, index),
                s.substring(index + 1)
        };
    }
}
