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

import com.cognizant.workbench.pipeline.model.directives.Environment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "toolTests")
public class ToolTest {
    @NotBlank(message = "Id should not be blank")
    @Size(min = 4, message = "Id should have at least 4 characters")
    private String id;
    @NotBlank(message = "Name should not be blank")
    @Size(min = 4, message = "Name should have at least 4 characters")
    private String name;
    private String link;
    private String type = "test";
    @NotBlank(message = "Group should not be blank")
    @Size(min = 3, message = "Group should have at least 3 characters")
    private String group;
    @NotNull(message = "Icon should be NotNull")
    private TIcon icon;
    @NotNull(message = "Data should be NotNull")
    private TTestData data;

    private ToolAgent agent;
    private List<Environment> environments;
    /* User Auditing fields*/
    @JsonIgnore
    @CreatedBy
    private String createdUser;
    @JsonIgnore
    @CreatedDate
    private Date createdDate;
    @JsonIgnore
    @LastModifiedBy
    private String lastModifiedUser;
    @JsonIgnore
    @LastModifiedDate
    private Instant lastModifiedDate;

    @Data
    public static class TTestData {
        @NotBlank(message = "Type should not be blank")
        private String type;
        @NotBlank(message = "Client should not be blank")
        private String client;
        @NotEmpty(message = "Steps should not be empty")
        @Size(min = 1, message = "Steps List should have at least 1 step")
        private List<TStep> steps;
    }
}