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

import com.cognizant.workbench.tools.beans.TIcon;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by 784420 on 8/16/2019 4:32 PM
 */
@Data
public class ToolDeployDTO {
    @NotBlank(message = "Name should not be blank")
    @Size(min = 2, message = "Name should have at least 2 characters")
    @Id
    private String name;
    @NotBlank(message = "Group should not be blank")
    @Size(min = 3, message = "Group should have at least 3 characters")
    private String group;
    @NotNull(message = "Icon should be NotNull")
    private TIcon icon;
}
