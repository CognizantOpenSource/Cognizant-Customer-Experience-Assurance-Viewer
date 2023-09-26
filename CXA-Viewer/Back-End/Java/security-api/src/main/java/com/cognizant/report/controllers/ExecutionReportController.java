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

package com.cognizant.report.controllers;

import com.cognizant.report.models.SecurityMetricsData;
import com.cognizant.report.repos.SecurityMetricsRepository;
import com.cognizant.report.services.ExecutionReportService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;


@Slf4j
@RestController
@RequestMapping(value = "/checkSecurity/")
//@CrossOrigin("*")
public class ExecutionReportController {

    @Autowired
    ExecutionReportService service;

    @Autowired
    SecurityMetricsRepository securityMetricsRepository;


    @PostMapping(value = "/insertMetricsData")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public ResponseEntity addMetrics(@RequestBody JSONObject metrics) {
        Boolean flag = true;
        Set<String> keys = metrics.keySet();
        String securitySaveResponse = null;

        String currentValue = null;
        try{
        for (String key : keys) {
            if (metrics.get(key) != null && key.equalsIgnoreCase("error")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9\\-]+$");
                if (key.equalsIgnoreCase("url")) {
                    pattern = java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                }
                if (key.equalsIgnoreCase("region")) {
                    pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
                }
                if (!key.equals("audits")) {
                    currentValue = metrics.get(key).toString();
                    Matcher matcher = pattern.matcher(currentValue);
                    if (matcher.matches()) {
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (key.equals("audits") || key.equalsIgnoreCase("securityheaders")) {
                    LinkedHashMap jsonObject = (LinkedHashMap) metrics.get("audits");
                    if (jsonObject.size() == 0) {
                        throw new Exception();
                    }
                    jsonObject = (LinkedHashMap) metrics.get("securityheaders");
                    if (jsonObject.size() == 0) {
                        throw new Exception();
                    }
                }
            }
        }
        if (flag) {
             securitySaveResponse = service.saveSecurityData(metrics);
            log.info("securitSaveReposne ====   " + securitySaveResponse);
            return ResponseEntity.status(200).body("Insertion of securityMetrics Failed while validating the response");
        }
        else{
            log.info("Invalid field == == == " +  currentValue);

        }
        }
        catch(Exception e) {
            log.info("Exception in validating security to add" + e.toString());
            return ResponseEntity.status(200).body("Insertion of securityMetrics Failed");
        }
        return ResponseEntity.status(200).body("Inserted security Successfully");

       }

    @GetMapping(value = "/getSecurityMetricsData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public SecurityMetricsData getSecurity(@RequestParam String projectId) {
        SecurityMetricsData securityMetricsData = service.getSecurity(projectId);
        return securityMetricsData;
    }

    @GetMapping(value = "/getSecurityOwaspData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public org.json.simple.JSONArray getSecurityOwasp(@RequestParam String executionId) {
        org.json.simple.JSONArray securityMetricsData = service.getSecurityOwasp(executionId);

        if(securityMetricsData==null){
            org.json.simple.JSONArray securityData=new org.json.simple.JSONArray();
            return securityData;
        } else{
            return securityMetricsData;
        }
    }

}
