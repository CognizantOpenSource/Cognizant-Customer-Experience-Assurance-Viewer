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

import com.cognizant.workbench.pipeline.model.AppStatus;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Created by 784420 on 11/20/2019 7:56 PM
 */

@Data
public class AppStatusDTO {
    @NotBlank(message = "Name should not be blank or null")
    private String name;
    @NotBlank(message = "Name should not be blank or null")
    private String type;
    @NotBlank(message = "Name should not be blank or null")
    private String status;
    @NotEmpty(message = "Task list should not be empty")
    @Size(min = 1, message = "Task list size should be at least 1")
    private List<String> tasks;
    private Date modifiedDate;
    private boolean refresh;

    public AppStatus getEntity() {
        AppStatus appStatus = new AppStatus();
        BeanUtils.copyProperties(this, appStatus);
        return appStatus;
    }
}
