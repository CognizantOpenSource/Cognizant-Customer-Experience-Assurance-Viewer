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

package com.cognizant.workbench.vcs.controller;

import com.cognizant.workbench.vcs.model.common.SCMConstants;
import com.cognizant.workbench.vcs.model.common.SCMCredDetails;
import com.cognizant.workbench.vcs.service.BitBucketService;
import com.cognizant.workbench.vcs.service.SCMCommonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created by 784420 on 9/19/2019 6:58 PM
 */
@RestController
@RequestMapping("/bitbucket")
@Slf4j
@AllArgsConstructor
public class BitBucketController {

    private BitBucketService service;
    private SCMCommonService commonService;

    /**
     * Getting all project and branch details
     *
     * @return Project names and branch details with
     */
    @GetMapping("/projectNames")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('BitBucket','scm.project.read')")
    public Map<String, List> getProjectDetails() {
        log.info("getting the Project details from BitBucket");
        SCMCredDetails scm = commonService.getSCMCredDetails(SCMConstants.BITBUCKET);
        return service.getProjectDetails(scm.getApiOrHostUrl(), scm.getUsername(), scm.getToken());
    }

    /**
     * Testing connection with Api or Host url
     *
     * @param scmCredDetails  Api info
     * @return response message with success or failure
     */
    @PostMapping("/test")
    @ResponseStatus(OK)
    @PreAuthorize("hasPermission('BitBucket','scm.test.connection')")
    public ResponseEntity testConnection(@RequestBody SCMCredDetails scmCredDetails) {
        log.info("testing connection of BitBucket using credentials");
        return service.testConnection(scmCredDetails.getApiOrHostUrl(), scmCredDetails.getUsername(), scmCredDetails.getToken());
    }
}
