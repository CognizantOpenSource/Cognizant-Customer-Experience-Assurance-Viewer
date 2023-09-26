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

package com.cognizant.workbench.pipeline.model;

import com.cognizant.workbench.pipeline.model.snippets.StepData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Post {
    private List<StepData> always;
    private List<StepData> success;
    private List<StepData> failure;
    private List<StepData> changed;
    private List<StepData> unstable;
    private List<StepData> unsuccessful;

    @JsonIgnore
    private static String tab(int num) {
        return new String(new char[num]).replace("\0", "\t");
    }

    @JsonIgnore
    public String convert(String globalOS, int i) {

        if (CollectionUtils.isEmpty(always)
                && CollectionUtils.isEmpty(success)
                && CollectionUtils.isEmpty(failure)
                && CollectionUtils.isEmpty(changed)
                && CollectionUtils.isEmpty(unstable)
                && CollectionUtils.isEmpty(unsuccessful)) {
            return "";
        }

        final String TEMPLATE_START = "%s%s{%n";
        final String TEMPLATE_END = "%s}%n";
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s" + "post{" + "%n", tab(i)));
        Map<String, List<StepData>> map = new HashMap<>();

        if (!CollectionUtils.isEmpty(always)) {
            map.put(Constants.ALWAYS, always);
        }
        if (!CollectionUtils.isEmpty(success)) {
            map.put(Constants.SUCCESS, success);
        }
        if (!CollectionUtils.isEmpty(failure)) {
            map.put(Constants.FAILURE, failure);
        }
        if (!CollectionUtils.isEmpty(changed)) {
            map.put(Constants.CHANGED, changed);
        }
        if (!CollectionUtils.isEmpty(unstable)) {
            map.put(Constants.UNSTABLE, unstable);
        }
        if (!CollectionUtils.isEmpty(unsuccessful)) {
            map.put(Constants.UNSUCCESSFUL, unsuccessful);
        }

        map.forEach((key, value) -> {
            builder.append(String.format(TEMPLATE_START, tab(i + 1), key));
            builder.append(
                    value.stream().map(stepData -> tab(i + 2) + stepData.convert(globalOS, i + 2) + "\n").collect(Collectors.joining())
            );
            builder.append(String.format(TEMPLATE_END, tab(i + 1)));
        });

        builder.append(String.format("%s" + "}" + "%n", tab(i)));
        return builder.toString();
    }
}
