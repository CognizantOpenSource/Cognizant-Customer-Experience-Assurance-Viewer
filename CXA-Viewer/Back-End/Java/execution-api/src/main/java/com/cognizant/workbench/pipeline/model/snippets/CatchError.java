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
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 784420 on 6/26/2019 2:22 PM
 */
@Data
public class CatchError {
    private String message;
    private String buildResult;
    private String stageResult;
    private boolean catchInterruptions;

    @JsonIgnore
    public String convert() {
        List<String> list = new ArrayList<>();
        if (!StringUtils.isEmpty(buildResult) && !"FAILURE".equals(buildResult))
            list.add(String.format("buildResult: '%s'", buildResult));
        if (!catchInterruptions)
            list.add(String.format("catchInterruptions: '%s'", catchInterruptions));
        if (!StringUtils.isEmpty(message))
            list.add(String.format("message: '%s'", message));
        if (!StringUtils.isEmpty(stageResult) && !"SUCCESS".equals(stageResult))
            list.add(String.format("stageResult: '%s'", stageResult));

        if (!CollectionUtils.isEmpty(list))
            return String.format("catchError(%s) {}", String.join(",", list));
        else
            return "catchError{}";
    }
}
