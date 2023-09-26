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
 * Created by 784420 on 10/08/2019 04:52 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroovyScript {

    protected List<String> script;

    @JsonIgnore
    private static String tab(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    @JsonIgnore
    public String convertAsDef(int i) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(script)) return builder.toString();

        builder.append(String.format("%n"));
        if (script.size() > 1)
            builder.append(tab(i + 1)).append(String.join("\n" + tab(i + 1), script));
        else
            builder.append(tab(i + 1)).append(String.join("\n", script));
        builder.append(String.format("%n"));

        return builder.toString();
    }

    @JsonIgnore
    public String convert(String type, String platform, int i) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(script)) return builder.toString();

        builder.append(String.format("script{%n"));
        if (script.size() > 1)
            builder.append(tab(i + 1)).append(String.join("\n" + tab(i + 1), script));
        else
            builder.append(tab(i + 1)).append(String.join("\n", script));
        builder.append(String.format("%n%s}", tab(i)));

        return builder.toString();
    }
}
