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

package com.cognizant.workbench.tools.beans;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 784420 on 11/7/2019 2:15 PM
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "id",
        "data",
        "params"
})
@Data
public class TGroup {

    @JsonProperty("type")
    @NotBlank(message = "Type should not be empty/null")
    @Size(min = 3, message = "Type should contain at least 3 characters")
    private String type;
    @NotBlank(message = "Id should not be empty/null")
    @Size(min = 3, message = "Id should contain at least 3 characters")
    @JsonProperty("id")
    private String id;
    @JsonProperty("data")
    private Object data;
    @JsonProperty("params")
    private List<TParam> params = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
