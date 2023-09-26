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

package com.cognizant.workbench.vcs.model.common;

import com.cognizant.workbench.base.services.WhiteListed;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Created by 784420 on 9/20/2019 4:14 PM
 */
@Data
@Validated
public class SCMCredDetails {
    @URL
    @NotBlank(message = "Api or Host URL should not be blank")
    @WhiteListed
    private String apiOrHostUrl ;
    @NotBlank(message = "Token should not be blank")
    private String token;
    @NotBlank(message = "username should not be blank")
    private String username;
}
