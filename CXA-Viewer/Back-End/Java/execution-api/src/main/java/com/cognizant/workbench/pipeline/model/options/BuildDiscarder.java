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

package com.cognizant.workbench.pipeline.model.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Objects;

@Data
public class BuildDiscarder {
    private String artifactDaysToKeepStr;
    private String artifactNumToKeepStr;
    private String daysToKeepStr;
    private String numToKeepStr;

    @JsonIgnore
    public String toConvert() {
        return "buildDiscarder( logRotator(" +
                "artifactDaysToKeepStr:'" + Objects.toString(artifactDaysToKeepStr, "") + '\'' +
                ", artifactNumToKeepStr:'" + Objects.toString(artifactNumToKeepStr, "") + '\'' +
                ", daysToKeepStr:'" + Objects.toString(daysToKeepStr, "") + '\'' +
                ", numToKeepStr:'" + Objects.toString(numToKeepStr, "") + '\'' +
                "))";
    }
}
