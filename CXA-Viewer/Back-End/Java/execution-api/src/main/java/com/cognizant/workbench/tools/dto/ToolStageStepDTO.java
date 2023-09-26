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

import com.cognizant.workbench.tools.beans.TStepGroup;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by 784420 on 8/16/2019 7:13 PM
 */
@Data
public class ToolStageStepDTO {
    @NotBlank(message = "Name should not be blank")
    @Size(min = 3, message = "Name should have at least 3 characters")
    @Id
    private String name;
    @NotEmpty(message = "Groups should not be empty")
    @Size(min = 1, message = "Groups List should have at least 1 Group")
    private List<TStepGroup> group;
}
