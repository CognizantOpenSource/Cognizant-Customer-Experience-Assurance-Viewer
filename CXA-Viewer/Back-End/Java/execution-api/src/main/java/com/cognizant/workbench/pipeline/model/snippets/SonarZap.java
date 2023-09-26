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

package com.cognizant.workbench.pipeline.model.snippets;

import com.cognizant.workbench.pipeline.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 784420 on 7/3/2019 6:27 PM
 */
@Data
public class SonarZap implements SnippetsConverter {

    private String env;
    private String projectName = "";
    private String projectVersion = "";
    private String projectKey = "";
    private List<String> script;

    @JsonIgnore
    @Override
    public String convert(String globalType, String platform, int i) {

        List<String> tempList = new ArrayList<>();
        script.forEach(
                temp -> {
                    String tempStr = temp.replace("#projectName", projectName)
                            .replace("#projectVersion", projectVersion)
                            .replace("#projectKey", projectKey);
                    tempList.add(tempStr);
                }
        );

        PlainScript plainScriptClass = new PlainScript();
        plainScriptClass.setScript(tempList);

        return (
                new StringBuilder().append(String.format("withSonarQubeEnv('%s') {%n", env))
                        .append(Util.tabs(i + 1))
                        .append(plainScriptClass.convert(platform, i+1)).append("\n")
                        .append(Util.tabs(i)).append("}")
        ).toString();
    }
}
