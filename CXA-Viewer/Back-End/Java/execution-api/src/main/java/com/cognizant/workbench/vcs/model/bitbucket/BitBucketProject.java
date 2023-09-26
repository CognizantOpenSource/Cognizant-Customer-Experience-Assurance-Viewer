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

package com.cognizant.workbench.vcs.model.bitbucket;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class BitBucketProject {
    private Owner owner;
    @SerializedName("updated_on")
    private String updatedOn;
    @SerializedName("is_private")
    private String isPrivate;
    private Object website;
    @SerializedName("fork_policy")
    private String forkPolicy;
    private Project project;
    private String description;
    private String language;
    private String type;
    private String uuid;
    @SerializedName("has_issues")
    private String hasIssues;
    private MainBranch mainbranch;
    @SerializedName("has_wiki")
    private String hasWiki;
    @SerializedName("full_name")
    private String fullName;
    private String size;
    @SerializedName("created_on")
    private String createdOn;
    private String name;
    private Links links;
    private String scm;
    private String slug;
}