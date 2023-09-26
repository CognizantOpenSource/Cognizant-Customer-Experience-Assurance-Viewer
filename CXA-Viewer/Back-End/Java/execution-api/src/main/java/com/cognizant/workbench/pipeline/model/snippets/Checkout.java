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

package com.cognizant.workbench.pipeline.model.snippets;

import com.cognizant.workbench.pipeline.model.Auth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.Locale;

/**
 * Created by 784420 on 6/26/2019 4:19 PM
 */
@Data
@NoArgsConstructor
public class Checkout {

    private static final String GIT_TEMPLATE = "checkout([$class: 'GitSCM', branches: [[name: '%s']], doGenerateSubmoduleConfigurations: false, extensions: [%s], userRemoteConfigs: [[credentialsId: '%s', url: '%s']]])%n";
    private static final String MERCURIAL_TEMPLATE = "checkout([$class: 'MercurialSCM', credentialsId: '%s', revision: '%s', revisionType: '%s', source: '%s'])%n";
    private static final String SV_TEMPLATE = "checkout([$class: 'SubversionSCM', filterChangelog: false, ignoreDirPropChanges: false, locations: [[cancelProcessOnExternalsFail: true, credentialsId: '%s', depthOption: '%s', " +
            "ignoreExternalsOption: true, local: '%s', remote: '%s']], quietOperation: true, workspaceUpdater: [$class: '%s']])%n";

    @NotBlank(message = "SCM should not be empty")
    private String type;
    @NotBlank(message = "Repository URL should not be empty")
    private String repo;
    private String credentialId;
    private String branch = "master";
    private String svnCheckOutStrategy;
    private String revisionType;
    private String revision;
    private String repositoryDepth;
    @Value(".")
    private String localModuleDirectory;
    private String subDirectory;
    private Auth auth;

    @JsonIgnore
    public String convert() {
        String returnStr;
        switch (type.toLowerCase(Locale.ENGLISH)) {
            case "git":
            case "github":
            case "gitlab":
                returnStr = toGit();
                break;
            case "mercurial":
                returnStr = toMercurial();
                break;
            case "subversion":
                returnStr = toSubversion();
                break;
            default:
                returnStr = "";
                break;
        }
        return returnStr;
    }

    @JsonIgnore
    private String toSubversion() {
        return String.format(SV_TEMPLATE, credentialId, repositoryDepth, localModuleDirectory, repo, svnCheckOutStrategy);
    }

    @JsonIgnore
    private String toMercurial() {
        return String.format(MERCURIAL_TEMPLATE, credentialId, revision, revisionType, repo);
    }

    @JsonIgnore
    private String toGit() {
        String tempSubDir = StringUtils.isEmpty(subDirectory) ? "" : String.format("[$class: 'RelativeTargetDirectory', relativeTargetDir: '%s']", subDirectory);
        return String.format(GIT_TEMPLATE, branch, tempSubDir, credentialId, repo);
    }
}
