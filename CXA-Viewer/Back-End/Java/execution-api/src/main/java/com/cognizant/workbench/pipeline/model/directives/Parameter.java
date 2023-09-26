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

package com.cognizant.workbench.pipeline.model.directives;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class Parameter {
    @NotBlank(message = "PramType should not be null/empty")
    @Size(min = 3, message = "ParamType length at least 3 characters")
    private String paramType;
    @NotBlank(message = "Name should not be null/empty")
    @Size(min = 3, message = "Name length at least 3 characters")
    private String name;
    private String defaultValue;
    private String description;
    private List<String> choices = null;
}
