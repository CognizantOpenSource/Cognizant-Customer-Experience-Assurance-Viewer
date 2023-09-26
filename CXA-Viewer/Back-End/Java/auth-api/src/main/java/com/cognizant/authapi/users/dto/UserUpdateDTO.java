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

package com.cognizant.authapi.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Created by 784420 on 7/17/2019 3:41 PM
 */
@Data
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String image;
    @NotBlank(message = "Company/Organization Name should not be empty")
    @Size(min = 2, message = "Company/Organization Name should have at least 2 characters")
    private String org;
    private Boolean isMFA = Boolean.FALSE;

    @JsonIgnore
    public Boolean getMFA() {
        return Objects.requireNonNullElse(isMFA, Boolean.FALSE);
    }

    @JsonIgnore
    public void setMFA(Boolean MFA) {
        isMFA = Objects.requireNonNullElse(MFA, Boolean.FALSE);
    }
}
