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
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

/**
 * Created by 784420 on 6/28/2019 3:23 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SonarQube extends PlainScript {
    private String template = "withSonarQubeEnv('%s') { %s }";
    private String env;

    @JsonIgnore
    @Override
    public String convert(String platform, int i) {
        if (CollectionUtils.isEmpty(script)) return "";
        return String.format(template, env, super.convert(platform, i));
    }
}
