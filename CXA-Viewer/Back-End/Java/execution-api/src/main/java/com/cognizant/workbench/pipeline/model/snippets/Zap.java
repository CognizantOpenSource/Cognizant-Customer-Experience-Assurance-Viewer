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

import com.cognizant.workbench.pipeline.util.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * Created by 784420 on 7/3/2019 2:52 PM
 */
@Data
public class Zap implements SnippetsConverter {

    private static final String START_ZAP = "startZap(host: \"%s\" , port: %s, timeout: %s, zapHome: \"%s\" , allowedHosts:[%s],sessionPath:\"\")%n";
    private static final String RUN_ZAP_CRAWLER = "runZapCrawler(host: \"%s\")%n";
    private static final String RUN_ZAP_ATTACK = "runZapAttack(scanPolicyName:'%s',userId:%s)%n";
    private static final String ARCHIVE_ZAP = "archiveZap(failAllAlerts: %s, failHighAlerts: %s, failMediumAlerts:%s, failLowAlerts: %s, falsePositivesFilePath: \"%s\")";

    private String zapHost ;
    private String zapPort ;
    private String zapHome ;
    private String timeout ;
    private List<String> allowedHosts ;
    private String sessionPath ;
    private String targetHost ;
    private String scanPolicyName ;
    private String userId ;
    private String env ;
    private String projectName ;
    private String projectKey ;
    private String projectVersion ;
    private String failAllAlerts ;
    private String failHighAlerts ;
    private String failMediumAlerts ;
    private String failLowAlerts ;
    private String falsePositivesFilePath ;

    @JsonIgnore
    @Override
    public String convert(String type, String platform, int i) {
        return (new StringBuilder().append(convertStartZap())
                .append(Util.tabs(i)).append(convertRunZapCrawler())
                .append(Util.tabs(i)).append(convertRunZapAttack())
                .append(Util.tabs(i)).append(convertArchiveZap())
                ).toString();
    }

    @JsonIgnore
    private String convertStartZap(){
        return String.format(START_ZAP, zapHost, zapPort, timeout, zapHome, Util.joinListWithCommaQuote(allowedHosts));
    }

    @JsonIgnore
    private String convertRunZapCrawler(){
        return String.format(RUN_ZAP_CRAWLER, targetHost);
    }

    @JsonIgnore
    private String convertRunZapAttack(){
        return String.format(RUN_ZAP_ATTACK, scanPolicyName, userId);
    }

    @JsonIgnore
    private String convertArchiveZap(){
        return String.format(ARCHIVE_ZAP, failAllAlerts, failHighAlerts, failMediumAlerts, failLowAlerts, falsePositivesFilePath);
    }
}
