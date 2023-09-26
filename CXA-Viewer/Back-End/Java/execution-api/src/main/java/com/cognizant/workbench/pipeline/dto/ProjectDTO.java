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

package com.cognizant.workbench.pipeline.dto;

import com.cognizant.workbench.pipeline.model.Ci;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
public class ProjectDTO {
    @Id
    private String id;
    //@NotBlank(message = "Version should not be empty")
    private String version;
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;
    //@NotBlank(message = "Platform should not be empty")
    @Size(min = 3, message = "Platform should have at least 3 characters")
    private String platform;
    //@NotNull(message = "CI should not be null")
    private Ci ci;
    //@Nullable
    private Map<String, String> links;
    private String applicationId;
    private String applicationName;
}
