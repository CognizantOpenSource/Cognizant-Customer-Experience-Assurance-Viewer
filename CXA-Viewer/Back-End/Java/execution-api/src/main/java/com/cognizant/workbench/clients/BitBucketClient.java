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

package com.cognizant.workbench.clients;

import com.cognizant.workbench.vcs.model.bitbucket.BitBucketProjectDetails;
import com.cognizant.workbench.vcs.model.bitbucket.branch.BitBucketBranchesDetails;
import com.cognizant.workbench.vcs.model.bitbucket.team.TeamDetails;
import feign.*;

import java.net.URI;
import java.util.Map;

/**
 * Created by 784420 on 9/20/2019 4:37 PM
 */
public interface BitBucketClient {

    @RequestLine("GET /2.0/repositories/{username}/")
    BitBucketProjectDetails getProjects(URI baseURI,
                                        @Param("username") String username,
                                        @HeaderMap Map<String, String> requestHeader
    );

    @RequestLine("GET /")
    BitBucketBranchesDetails getBranches(URI baseURI,
                                         @HeaderMap Map<String, String> requestHeader
    );

    @RequestLine(value = "POST /2.0/repositories/{repository}/src", decodeSlash = false)
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Response pushFile(URI baseURI,
                      @Param("repository") String repository,
                      Map<String, ?> body,
                      @HeaderMap Map<String, String> requestHeader
    );

    @RequestLine("GET /")
    Response testConnection(URI baseURI,
                            @HeaderMap Map<String, String> requestHeader
    );

    @RequestLine("GET /2.0/teams?role={member}")
    TeamDetails getTeams(URI baseURI,
                         @Param("member") String member,
                         @HeaderMap Map<String, String> requestHeader
    );

}
