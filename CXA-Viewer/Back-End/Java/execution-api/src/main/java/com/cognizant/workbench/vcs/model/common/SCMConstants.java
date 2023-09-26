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

/**
 * Created by 784420 on 9/25/2019 12:58 PM
 */
public class SCMConstants {
    private SCMConstants(){}
    public static final String GITLAB = "gitlab";
    public static final String GITHUB = "github";
    public static final String BITBUCKET = "bitbucket";

    public static final String SCM = "scm";
    public static final String PIPELINE = "pipeline";

    public static final String TEST_CONNECTION_FAILED = "Test Connection Failed.";
    public static final String TEST_CONNECTION_SUCCESSFUL = "Test Connection Successful.";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";

    public static final String JENKINSFILE = "Jenkinsfile";
    public static final String COMMIT_MESSAGE = "Update from leap";

    public static final String USERNAME_IS_MISMATCH_WITH_TOKEN = " Username is mismatch with token (Username is case sensitive).";
    public static final String PROVIDED_SCM_TYPE_S_IS_INVALID = "Provided SCM Type (%s) is Invalid.";
    public static final String PLEASE_UPDATE_USER_SETTINGS = "SCM Details are not updated for the provided SCM Type (%s). Please Update UserSettings (Repo URL, Username and Token).";

    public static final String REPOSITORY_BRANCH_DETAILS_ARE_NOT_FOUND_IN_PROJECT = "Repository/Branch details are not found in Project, Project Name is: %s";
    public static final String REPOSITORY_DETAILS_ARE_NOT_FOUND_IN_PROJECT = "Repository details are not found in Project, Project Name is: %s";
    public static final String BRANCH_DETAILS_ARE_NOT_FOUND_IN_THE_PROJECT = "Branch details are not found in the project, Project Name is: %s";
    public static final String OWNER_DETAILS_ARE_NOT_FOUND_IN_THE_PROJECT = "Owner details are not found in the project, Project Name is: %s";
}
