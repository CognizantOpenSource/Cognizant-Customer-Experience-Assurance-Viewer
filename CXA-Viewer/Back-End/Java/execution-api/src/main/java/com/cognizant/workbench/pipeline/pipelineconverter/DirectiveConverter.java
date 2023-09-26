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

import com.cognizant.workbench.pipeline.model.directives.Environment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
class DirectiveConverter {

    private static final String KEY_VAL = "%s%s = '%s'%n";
    private static final String KEY_CRED_VAL = "%s%s = credentials('%s')%n";
    private static final String ENV = "%s" + "environment{%n";

    private static final String MAVEN = "%s" + "maven '%s'%n";
    private static final String JDK = "%s" + "jdk '%s'%n";
    private static final String GRADLE = "%s" + "gradle '%s'%n";
    private static final String TOOLS = "%s" + "tools {%n";

    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    String convertEnv(List<Environment> environments, int i) {
        log.info("convertEnv() : Started");
        StringBuilder response = new StringBuilder();

        if (Objects.nonNull(environments)) {
            for (Environment env : environments) {
                if (!StringUtils.isEmpty(env.getKey()) && !StringUtils.isEmpty(env.getValue().getValue())) {
                    if (env.getValue().getType().equals("cred")) {
                        response.append(String.format(KEY_CRED_VAL, tabs(i + 1), env.getKey(), env.getValue().getValue()));
                    }
                    if (env.getValue().getType().equals("string")) {
                        response.append(String.format(KEY_VAL, tabs(i + 1), env.getKey(), env.getValue().getValue()));
                    }
                }
            }
            if (StringUtils.isEmpty(response.toString())) {
                return "";
            } else {
                response.insert(0, String.format(ENV, tabs(i)));
                response.append(tabs(i)).append("}\n");
            }
        }

        log.info("convertEnv() : Ended");
        return response.toString();
    }

    String convertTools(Map<String, String> tools, int i) {
        log.info("convertTools() : Started");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(TOOLS, tabs(i)));
        tools.forEach((key, value) -> {
            if (!StringUtils.isEmpty(value)) {
                switch (key) {
                    case "jdk":
                        stringBuilder.append(String.format(JDK, tabs(i + 1), value));
                        break;
                    case "maven":
                        stringBuilder.append(String.format(MAVEN, tabs(i + 1), value));
                        break;
                    case "gradle":
                        stringBuilder.append(String.format(GRADLE, tabs(i + 1), value));
                        break;
                    default:
                        break;
                }
            }
        });
        stringBuilder.append(tabs(i)).append("}\n");
        log.info("convertTools() : Ended");
        return stringBuilder.toString();
    }
}
