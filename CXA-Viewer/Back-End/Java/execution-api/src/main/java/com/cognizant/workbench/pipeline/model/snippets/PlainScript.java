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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by 784420 on 6/26/2019 12:04 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlainScript {

    protected List<String> script;
    private Object windows;
    private Object linux;

    @JsonIgnore
    private static String tabs(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    @JsonIgnore
    public String convert(String platform, int i) {
        String scriptStr = "";
        if (CollectionUtils.isEmpty(script)) return scriptStr;
        if (script.size() > 1)
            scriptStr = String.format("'''%s''' ", String.join("\n"+tabs(i), script));
        else
            scriptStr = String.format("'%s' ", String.join("\n", script));
        if ("windows".equalsIgnoreCase(platform))
            return "bat " + scriptStr;
        else
            return "sh " + scriptStr;
    }
}
