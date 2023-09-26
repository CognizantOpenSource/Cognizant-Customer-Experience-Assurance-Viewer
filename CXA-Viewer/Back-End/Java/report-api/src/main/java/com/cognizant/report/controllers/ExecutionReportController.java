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

import com.cognizant.report.models.*;
import com.cognizant.report.services.ExecutionReportService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;


@Slf4j
@RestController
@RequestMapping(value = "/test/reports")
//@CrossOrigin("*")
public class ExecutionReportController {

    @Autowired
    ExecutionReportService service;




    @GetMapping(value = "/validatePerformance")
    @PreAuthorize("hasPermission('TestReport','cxa.report.read')")
    public JSONObject validatePerformanceDetails(@RequestParam String url) {

        Boolean checkValidation = true;// service.validatePerformanceUrl(url);
        JSONObject result = new JSONObject();
        if (checkValidation) {
            result.put("Message", "valid Url");
            return result;
        } else {
            result.put("Message", "invalid Url");
            return result;
        }
    }



    @GetMapping(value = "/getPerformances")
    @PreAuthorize("hasPermission('TestReport','cxa.report.read')")
    public Performances getPerformancesMetrics(@RequestParam String projectId, @RequestHeader("Authorization") String authToken) {
        log.info("Get Performance data");
        return service.getPerformancesMetricsData(projectId, authToken);
    }


    @GetMapping(value = "/getAccessibilityMetricsData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public AccessibilityMetricsData getAccessibility(@RequestHeader("Authorization") String authToken, @RequestParam String projectId) {
        log.info("Get Accessibility data");
        return service.getAccessibilityData(projectId, authToken);
    }

    @GetMapping(value = "/getSecurityMetricsData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public SecurityMetricsData getSecurity(@RequestHeader("Authorization") String authToken, @RequestParam String projectId) {
        log.info("Get Security data");
        return service.getSecurity(projectId, authToken);
    }

    @GetMapping(value = "/getOmnichannelPerformance")
    @PreAuthorize("hasPermission('TestReport','cxa.report.read')")
    public JSONObject getOmnichannelPerformance(@RequestParam String projectId, @RequestHeader("Authorization") String authToken) {
        log.info("Get CrossBrowser data");
        return service.getOmnichannelPerformance(projectId);
    }

    @GetMapping(value = "/getSecurityOwaspData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public org.json.simple.JSONArray getSecurityOwasp(@RequestHeader("Authorization") String authToken, @RequestParam String executionId) {
        log.info("Get SecurityOwaspData data");
        org.json.simple.JSONArray securityMetricsData = service.getSecurityOwasp(executionId, authToken);

        if (securityMetricsData == null) {
            return new org.json.simple.JSONArray();
        } else {
            return securityMetricsData;
        }
    }

    @GetMapping(value = "/getSummaryDetails")
    @PreAuthorize("hasPermission('TestReport','cxa.report.read')")
    public JSONObject getSummaryData(@RequestParam String projectId, @RequestHeader("Authorization") String authToken) {
        log.info("Get Summary data");
        return service.getSummaryMetrics(projectId, authToken);
    }


    @GetMapping(value = "/getActiveMonitorRegionAndLoadTime")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public JSONObject getActiveMonitor(@RequestParam String projectId, @RequestHeader("Authorization") String authToken) {
        log.info("Get ActiveMonitor data");
        return service.getActiveMonitor(projectId, authToken);

    }

    @GetMapping(value = "/getSeoDetails")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public JSONObject getSeo(@RequestParam String projectId, @RequestHeader("Authorization") String authToken) {
        log.info("Get SEO data");
        return service.getSeo(projectId, authToken);

    }

    @PostMapping(value = "/insertMetricsData")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public ResponseEntity addMetrics(@RequestBody JSONObject metrics, @RequestHeader("Authorization") String authToken) throws ParseException {

        log.info("insertMetricsData Execution Completed");
        String executionId;
        String applicationId = "";
        executionId = service.generateExecutionId(metrics);
        Boolean checkPerformances = (Boolean) metrics.get("performancesCheck");
        String projectId = metrics.get("projectId").toString();
        Boolean checkAccessibility = metrics.get("accessibilityCheck") != null ? (Boolean) metrics.get("accessibilityCheck") : false;
        Boolean checkSeo = metrics.get("seoCheck") != null ? (Boolean) metrics.get("seoCheck") : false;
        Boolean checkSecurity = metrics.get("securityCheck") != null ? (Boolean) metrics.get("securityCheck") : false;
        Boolean activeMonitorCheck = metrics.get("activeMonitorCheck") != null ? (Boolean) metrics.get("activeMonitorCheck") : false;
        Boolean crossBrowser = metrics.get("crossBrowser") != null ? (Boolean) metrics.get("crossBrowser") : false;
        JSONArray modules = new JSONArray();

        if (checkPerformances) {
            modules.add("performances");
        }
        if (checkAccessibility) {
            modules.add("accessibility");
            String accessibilityURL = metrics.get("url").toString();
            try {
            } catch (Exception ex) {
                log.error("Exception" + ex);
            }
        }
        if (checkSeo) {
            modules.add("seo");
            String seoURL = metrics.get("url").toString();
        }
        if (checkSecurity) {
            modules.add("security");
            String securityURL = metrics.get("url").toString();
        }
        if(crossBrowser){
            String environment = metrics.get("environment") != null ? metrics.get("environment").toString() : "Local";
            service.requestForOmniChannel(metrics,executionId,environment,authToken);
        }
        if (modules.size() > 0) {
            String url = metrics.get("url") != null ? metrics.get("url").toString() : "";
            String environment = metrics.get("environment") != null ? metrics.get("environment").toString() : "Local";
            String urlAndRegion="";
            String environmentRegion="";
            if(environment.equalsIgnoreCase("cloud")){
                LinkedHashMap urlRegion=(LinkedHashMap)  metrics.get("urlAndRegion");
                urlAndRegion=urlRegion.get("environmentUrl") == null ? null: urlRegion.get("environmentUrl").toString();
                environmentRegion=urlRegion.get("environmentRegion")==null ?  null :urlRegion.get("environmentRegion").toString();
            }
            service.requestLHAPiForPerformancesAccessibilitySecuritySeo(url, executionId, modules, environment,urlAndRegion,environmentRegion, authToken);
//            if (checkPerformances) {
//                service.requestForOmniChannel(metrics,executionId,environment);
//            }
        }

        if (activeMonitorCheck) {
            int interval = Integer.parseInt(metrics.get("interval").toString());
            int duration = Integer.parseInt(metrics.get("duration").toString());
            String activeMonitorUrl = metrics.get("url").toString();
            ArrayList region = (ArrayList) metrics.get("activeMonitorUrlAndRegion");
            String env=metrics.get("activeMonitorEnvir").toString();
            service.addActiveMonitorMetrics(activeMonitorUrl, executionId, projectId, interval, duration, region,env, authToken);
        }
        log.info("insertMetricsData Execution Completed");
        return ResponseEntity.status(200).body("Execution Completed");
    }
    @PostMapping(value = "/insertActivemonitor")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String saveActivemonitordata(@RequestBody JSONObject responseObj) throws Exception {

        log.info("save ActiveMonitor data Execution Started");
        Boolean flag = true;
        Set<String> keys = responseObj.keySet();
        String executionId = responseObj.get("executionId").toString();
        String activeMonitorUrl = responseObj.get("url").toString();
        String regionAndUrl =responseObj.get("region").toString();
        service.saveActiveMonitorMetrics(executionId,executionId,regionAndUrl,responseObj);

        log.info("save ActiveMonitor data Execution Completed");
        return "Actovemonitor response";
    }

    @PostMapping(value = "/insertAccessibility")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String saveAccessibilityData(@RequestBody JSONObject responseObj) throws Exception {

        log.info("Save Acccessibility Execution Started");
        Boolean flag = true;
        Set<String> keys = responseObj.keySet();

        for (String key : keys) {
            String currentValue = null;
            if (responseObj.get(key) != null) {
                if(key.equalsIgnoreCase("error") && (responseObj.get(key).toString().equals("") || responseObj.get(key).toString().equals("true") || responseObj.get(key).toString().equals("false"))) {
                    continue;
                }
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9\\-]+$");
                if (key.equalsIgnoreCase("url")) {
                    pattern = java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                }
                if (key.equalsIgnoreCase("region")) {
                    pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
                }
                if (!key.equals("audits")) {
                    currentValue = responseObj.get(key).toString();
                    Matcher matcher = pattern.matcher(currentValue);
                    if (matcher.matches()) {
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (key.equals("audits")) {
                    LinkedHashMap jsonObject = (LinkedHashMap) responseObj.get("audits");
                    if (jsonObject.size() == 0) {
                        throw new Exception();
                    }
                }
            }
        }
        if (flag) {
            service.saveAccessibilityMetrics(responseObj);

            log.info("Save Acccessibility Execution Completed");
            return "Completed";
        }

        log.info("Save Acccessibility Execution Incompleted");
        return "incompleted";
    }



    @PostMapping(value = "/insertSecurity")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String saveSecurityData(@RequestBody JSONObject responseObj) throws Exception {

        Boolean flag = true;
        Set<String> keys = responseObj.keySet();
        String securitySaveResponse = null;

        String currentValue = null;
        try{
            for (String key : keys) {
                if (responseObj.get(key) != null && key.equalsIgnoreCase("error")) {
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9\\-]+$");
                    if (key.equalsIgnoreCase("url")) {
                        pattern = java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                    }
                    if (key.equalsIgnoreCase("region")) {
                        pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
                    }
                    if (!key.equals("audits")) {
                        currentValue = responseObj.get(key).toString();
                        Matcher matcher = pattern.matcher(currentValue);
                        if (matcher.matches()) {
                        } else {
                            flag = false;
                            break;
                        }
                    }
                    if (key.equals("audits") || key.equalsIgnoreCase("securityheaders")) {
                        LinkedHashMap jsonObject = (LinkedHashMap) responseObj.get("audits");
                        if (jsonObject.size() == 0) {
                            throw new Exception();
                        }
                        jsonObject = (LinkedHashMap) responseObj.get("securityheaders");
                        if (jsonObject.size() == 0) {
                            throw new Exception();
                        }
                    }
                }
            }
            if (flag) {
                securitySaveResponse = service.saveSecuritymetrics(responseObj);

                log.info("Save Security Execution completed");
                return "Insertion of securityMetrics Failed while validating the response";
            }
            else{
                log.error("Invalid Field - Security");

            }
        }
        catch(Exception e) {
            return "Insertion of securityMetrics Failed";
        }
            return "completed";
        }

    @PostMapping(value = "/insertSeo")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String saveSeoData(@RequestBody JSONObject responseObj) throws Exception {

        log.info("Save SEO Execution Started");
        try {
            Boolean flag = true;
            Set<String> keys = responseObj.keySet();

            for (String key : keys) {
                String currentValue = null;

                if (responseObj.get(key) != null) {
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9\\-]+$");
                    if (key.equalsIgnoreCase("url")) {
                        pattern = java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                    }
                    if (key.equalsIgnoreCase("region")) {
                        pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
                    }
                    if(key.equalsIgnoreCase("score")) {
                        pattern = java.util.regex.Pattern.compile("[0-9]*\\.?[0-9]*");
                    }
                    if (!key.equals("audits")) {
                        currentValue = responseObj.get(key).toString();
                        Matcher matcher = pattern.matcher(currentValue);
                        if (matcher.matches()) {
                        } else {
                            flag = false;
                            break;
                        }
                    }
                    if (key.equals("audits")) {
                        ArrayList jsonObject = (ArrayList) responseObj.get("audits");
                        if (jsonObject.size() == 0) {
                            throw new Exception();
                        }
                    }
                }
            }
            if (flag) {
                service.saveSeoMetrics(responseObj);
            }
        } catch (Exception ex) {
            log.error("Exception" +ex);
        }

        //System.out.println(" inside Seo");
        return "completed";
    }


        @PostMapping(value = "/insertPerformance")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String savePerformanceData(@RequestBody JSONObject responseObj) throws Exception {

            log.info("Save Performance Execution Started");
        Boolean flag = true;
        Set<String> keys = responseObj.keySet();
        for(String key : keys) {
            if(responseObj.get(key) != null && !key.equals("error") && !key.equalsIgnoreCase("screenshot")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9:.,/?=&_\\-\\s]+$");
                if(key.equalsIgnoreCase("performanceURL")) {
                    pattern =java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                }
                String temp = "";
                String currentValue = responseObj.get(key).toString();
                for(int i = 0; i < currentValue.length(); i++) {
                    int j = Integer.valueOf(currentValue.charAt(i));
                    if(j == 160) {
                        temp += " ";
                    }
                    else {
                        temp += (char)j;
                    }
                }
                Matcher matcher = pattern.matcher(temp);
                if(!matcher.matches()) {
                    flag = false;
                    break;
                }
            }
        }
        if(flag) {
            String res = service.savePerformanceMetrics(responseObj);
            log.info("Save Performance Execution Completed");
            return "completed";
        }

            log.error("Save Performance Execution Incompleted");
        return "NotCompleted";
    }

    @PostMapping(value = "/insertOmnichannel")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public String saveOmniChannel(@RequestBody LinkedHashMap responseObj) throws  Exception{
        log.info("Save Omni Execution Started");

        LinkedHashMap metrics=responseObj;
        return service.saveOmnichannelMetrics(metrics);

    }


}
