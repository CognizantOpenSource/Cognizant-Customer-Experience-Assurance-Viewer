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

package com.cognizant.workbench.vcs.service;

import com.cognizant.workbench.base.services.WhiteListed;
import com.cognizant.workbench.vcs.model.common.SCMDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * Created by 784420 on 9/20/2019 4:10 PM
 */
@Validated
public interface ISCMService {
    ResponseEntity pushJenkinsFile(String projectOrRepoId);

    ResponseEntity pushFileToSCM(SCMDetails details, String pipelineScript);

    Map getProjectDetails(@WhiteListed String baseURI, String username, String token);

    ResponseEntity testConnection(@WhiteListed String baseURI, String username, String token);

}
