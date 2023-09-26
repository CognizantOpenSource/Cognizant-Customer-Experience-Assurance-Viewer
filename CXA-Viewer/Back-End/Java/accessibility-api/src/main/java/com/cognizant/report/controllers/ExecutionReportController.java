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

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

@Slf4j
@RestController
@RequestMapping(value = "/checkAccessibility")
//@CrossOrigin("*")
public class ExecutionReportController {

    @Autowired
    ExecutionReportService service;

    @GetMapping(value = "/getAccessibilityMetricsData")
    @PreAuthorize("hasPermission('TestReport', 'cxa.report.read')")
    public AccessibilityMetricsData getAccessibility(@RequestParam String projectId) {
         return service.getAccessibilityData(projectId);
    }

    @PostMapping(value = "/insertMetricsData")
    @PreAuthorize("hasPermission('TestReport','cxa.report.write')")
    public ResponseEntity addMetrics(@RequestBody JSONObject metrics) throws ParseException {
        String accSaveResponse="";
        boolean flag=true;
            try {
                Set<String> keys = metrics.keySet();

                for(String key : keys) {
                    String currentValue =null;
                    if (metrics.get(key) != null) {
                         if(key.equalsIgnoreCase("error") && (metrics.get(key).toString().equals("") || metrics.get(key).toString().equals("true") || metrics.get(key).toString().equals("false"))) {
                             continue;
                         }
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9\\-]+$");
                        if (key.equalsIgnoreCase("url")) {
                            pattern = java.util.regex.Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#?/%=~_|]");
                        }
                        if (key.equalsIgnoreCase("region")) {
                            pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
                        }
                        if(!key.equals("audits"))
                        {
                           currentValue= metrics.get(key).toString();
                        Matcher matcher = pattern.matcher(currentValue);
                        if (matcher.matches()) {
                        } else {
                            flag = false;
                            break;
                        }}
                        if(key.equals("audits") ){
                            LinkedHashMap jsonObject = (LinkedHashMap) metrics.get("audits");
                            if(jsonObject.size() == 0) {
                                throw new Exception();
                            }}
                }
                }

                if(flag) {
                    accSaveResponse = service.saveAccessibilityData(metrics);
                }
            } catch (Exception ex) {
                log.error("Exception" +ex);
            }

        return ResponseEntity.status(200).body(accSaveResponse);
    }

}
