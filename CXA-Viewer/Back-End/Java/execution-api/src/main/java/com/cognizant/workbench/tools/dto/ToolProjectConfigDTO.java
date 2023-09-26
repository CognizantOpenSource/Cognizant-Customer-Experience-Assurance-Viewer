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

package com.cognizant.workbench.tools.dto;

import com.cognizant.workbench.tools.beans.TGroup;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 784420 on 11/7/2019 12:33 PM
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "allowed",
        "group"
})
@Data
public class ToolProjectConfigDTO {

    @Id
    @JsonProperty("name")
    @NotBlank(message = "Name should not be empty/null")
    @Size(min = 3, message = "Name should contain at least 3 characters")
    private String name;
    @JsonProperty("allowed")
    @NotBlank(message = "Allowed should not be empty/null")
    @Size(min = 3, message = "Allowed should contain at least 3 characters")
    private String allowed;
    @JsonProperty("group")
    @NotEmpty(message = "Group should not be empty/null")
    private List<TGroup> group = null;
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
