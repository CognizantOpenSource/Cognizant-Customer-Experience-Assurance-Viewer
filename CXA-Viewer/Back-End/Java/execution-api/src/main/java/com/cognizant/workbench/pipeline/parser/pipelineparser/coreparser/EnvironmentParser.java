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
import com.cognizant.workbench.pipeline.parser.pipelineparser.model.JenkinsEnvironment;
import com.cognizant.workbench.pipeline.parser.utils.PipelineUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EnvironmentParser {
    public JenkinsEnvironment getEnvironment(List<String> environmentInfo) {

        log.info("Environment Data Parsing");
        List<Map<String, String>> envList = new ArrayList<>();

        JenkinsEnvironment jenkinsEnvironment = new JenkinsEnvironment();
        removeEnvTag(environmentInfo);

        try {
            for (String str : environmentInfo) {
                Map<String, String> map = new HashMap<>();
                String[] values = str.split("=");
                map.put("Key", values[0].trim());

                Pattern p = Pattern.compile("\"([^\"]+)\"|'([^']+)'|\\S+");
                Matcher matcher = p.matcher(values[1].trim());
                while (matcher.find()) {
                    String quoteStripppedString = getMatch(matcher);
                    map.putAll(getMap(quoteStripppedString, str));
                }
                envList.add(map);
            }
        } catch (Exception e) {
            jenkinsEnvironment.getEnvironmentUnhandled().addAll(environmentInfo);
        }

        jenkinsEnvironment.getEnvList().addAll(envList);
        return jenkinsEnvironment;
    }

    private Map<String, String> getMap(String quoteStripppedString, String str) {
        Map<String, String> map = new HashMap<>();
        if (quoteStripppedString.startsWith("credentials")) {
            map.put("Type", "cred");
            String val = "";
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(str);
            while (m.find()) {
                val = m.group(1);
            }
            map.put("Value", val.replace("'", ""));
        } else if (StringUtils.isNumeric(quoteStripppedString)) {
            map.put("Type", "integer");
            map.put("Value", quoteStripppedString);
        } else {
            map.put("Type", "string");
            map.put("Value", quoteStripppedString);
        }
        return map;
    }

    private void removeEnvTag(List<String> environmentInfo) {
        if (PipelineUtil.isPipelineTag(environmentInfo.get(0), PipelineConstants.ENVIRONMENT))
            environmentInfo.remove(0);

        for (int i = environmentInfo.size() - 1; i >= 0; i--) {
            if (environmentInfo.get(i).equals("}")) {
                environmentInfo.remove(environmentInfo.get(i));
            } else {
                break;
            }
        }
    }

    private String getMatch(Matcher matcher) {
        if (matcher.group(1) != null){
            return matcher.group(1);
        }else if (matcher.group(2) != null){
            return matcher.group(2);
        }else {
            return matcher.group();
        }
    }
}
