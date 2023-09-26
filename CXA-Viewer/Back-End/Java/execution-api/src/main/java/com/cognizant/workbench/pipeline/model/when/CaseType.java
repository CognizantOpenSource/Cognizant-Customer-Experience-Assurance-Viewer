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

package com.cognizant.workbench.pipeline.model.when;

import com.cognizant.workbench.pipeline.util.CaseTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = CaseTypeDeserializer.class)
public class CaseType {
    private String caseType;
    private Object value;
    private String caseValue;

    @JsonIgnore
    public String getValueAsString() {
        return (String) value;
    }

    @JsonIgnore
    public WhenEnv getValueAsWhenEnv() {
        return (WhenEnv) value;
    }

    @JsonIgnore
    public WhenEquals getValueAsWhenEquals() {
        return (WhenEquals) value;
    }

    @JsonIgnore
    public WhenChangeRequest getValueAsWhenChangeReq() {
        return (WhenChangeRequest) value;
    }

    @JsonIgnore
    public WhenChangeSet getValueAsWhenChangeSet() {
        return (WhenChangeSet) value;
    }

    @JsonIgnore
    public WhenTag getValueAsWhenTag() {
        return (WhenTag) value;
    }

    @JsonIgnore
    public TriggeredBy getValueAsTriggeredBy() {
        return (TriggeredBy) value;
    }
}
