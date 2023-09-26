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

package com.cognizant.workbench.user.model;

import com.cognizant.workbench.charts.beans.DashboardConfig;
import com.cognizant.workbench.jenkins.model.JenkinsDetails;
import com.cognizant.workbench.pipeline.model.ExternalApps;
import com.cognizant.workbench.vcs.model.common.SCMCredDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * Created by 784420 on 8/1/2019 12:11 PM
 */
@Data
@Document(collection = "userSettings")
@NoArgsConstructor
public class UserSettings {
    @Id
    @Email
    @NotBlank(message = "Id should not be empty")
    @Size(min = 4, message = "Id should have at least 4 characters")
    private String id;
    private JenkinsDetails jenkins;
    private SCMCredDetails gitlab;
    private SCMCredDetails github;
    private SCMCredDetails bitbucket;
    private DashboardConfig dashboard;

    private ExternalApps externalApps;

    @JsonIgnore
    @CreatedBy
    private String user;
    @JsonIgnore
    @CreatedDate
    private Instant createdDate;
    @JsonIgnore
    @LastModifiedBy
    private String lastModifiedUser;
    @JsonIgnore
    @LastModifiedDate
    private Instant lastModifiedDate;
}
