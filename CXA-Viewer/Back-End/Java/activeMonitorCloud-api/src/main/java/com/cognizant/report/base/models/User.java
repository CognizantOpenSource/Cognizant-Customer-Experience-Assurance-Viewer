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

package com.cognizant.report.base.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * Created by  on 7/17/2019 3:41 PM
 */
@Data
public class User {
    @JsonIgnore
    protected String password;
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;
    private String org;
    private Date modifiedDate = new Date();
    private boolean active;
    private Account account;

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

    public String getName() {
        return getFirstName() + (!Objects.isNull(getLastName()) ? " " + getLastName() : "");
    }
}
