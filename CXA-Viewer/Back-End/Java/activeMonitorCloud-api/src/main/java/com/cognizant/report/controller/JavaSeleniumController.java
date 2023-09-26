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

package com.cognizant.report.controller;

import com.cognizant.report.service.JavaSeleniumService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/selenium")
public class JavaSeleniumController {
    @Autowired
    JavaSeleniumService javaSeleniumService;

    @PostMapping("/crossBrowser")
   // @PreAuthorize("hasPermission('TestReport', 'cxa.report.write')")
    public String startCrossBrowser(@RequestBody JSONObject metrics) throws IOException {
        System.out.println("Inside");
      String jsonArray=javaSeleniumService.StartCrossBrowser(metrics);
      return "All Execution Completed";
    }

    @PostMapping("/activeMonitor")
   // @PreAuthorize("hasPermission('TestReport', 'cxa.report.write')")
    public String startActiveMonitor(@RequestBody JSONObject metrics) {
        String str=javaSeleniumService.StartActiveMonitor(metrics);
        return  "All Execution Completed";
    }
}
