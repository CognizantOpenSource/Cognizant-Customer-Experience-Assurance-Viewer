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

package com.cognizant.report.services;

import com.cognizant.report.models.*;
import com.cognizant.report.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
@Service
@Slf4j
public class ExecutionReportService {
    @Autowired
    ActiveMonitorRepository activeMonitorRepository;
    @Value("${chromeDriverLocation}")
    private String chromeDriver;


    public String saveActiveMonitorMetrics(LinkedHashMap metrics) {
        String executionId = metrics.get("executionId") != null ? metrics.get("executionId").toString() : "";
        String url = metrics.get("url") != null ? metrics.get("url").toString() : "";
        log.info("inside save");
        ActiveMonitor activeMonitor = activeMonitorRepository.findByExecutionId(executionId);
        log.info("active monitor save" +metrics);
        ArrayList<LinkedHashMap> browser = (ArrayList<LinkedHashMap>) metrics.get("browser");
        String reg = metrics.get("region").toString();
        log.info(reg);
        if (activeMonitor != null) {
            log.info("inside mon");
            activeMonitor.setTimestamp(Instant.now().toString());
            activeMonitor.setUrl(url);

            activeMonitor.setExecutionId(executionId);
            activeMonitor.setStatus("Completed");
            activeMonitor.setError(false);
            JSONObject regionAndLoadTime = new JSONObject();
            JSONObject result = new JSONObject();
            JSONArray resArray = new JSONArray();
            for (int i = 0; i < browser.size(); i++) {
                LinkedHashMap obj = (LinkedHashMap) browser.get(i);
                LinkedHashMap chromeObject = (LinkedHashMap) obj.get("chrome");
                log.info("Chrome object is = >" + chromeObject);
                result.put("timeStamp", chromeObject != null ? chromeObject.get("timeStamp").toString() : "");
                result.put("loadTime", chromeObject != null ? chromeObject.get("loadTime").toString() : "");
                resArray.add(result);
                log.info("ResArray is => " + resArray);
            }
            JSONObject regions = activeMonitor.getResponseTime();
            log.info("Regions are from db " + regions);
            if (regions == null) {
                log.info("If regions are null");
                log.info("Regions is " + reg);
                log.info("Resarray is " + resArray);

                regionAndLoadTime.put(reg, resArray);
            } else if (!regions.containsKey(reg)) {
                regionAndLoadTime = regions;
                log.info("If region is not present in DB");
                log.info("Region is " + reg);
                log.info("ResArray is   " + resArray);
                regionAndLoadTime.put(reg, resArray);
            } else if (regions != null) {
                log.info("Region is " + reg);
                regionAndLoadTime = regions;

                ArrayList arr = (ArrayList) regions.get(reg);
                arr.add(result);
                log.info("Arraylsit is " + arr);
                regionAndLoadTime.put(reg, arr);
            }
            activeMonitor.setErrorMessage("");
            activeMonitor.setResponseTime(regionAndLoadTime);
            activeMonitorRepository.save(activeMonitor);

        }
        return "done";
    }

    public String runActiveMonitor(String url,String remoteUrl,String executionId,boolean sauceLabs,String regionForActivemonitor,JSONObject metrics){
        LinkedHashMap jsonObject = new LinkedHashMap();
        String region = regionForActivemonitor;//metrics.get("region").toString();
        jsonObject.put("url", url);
        jsonObject.put("executionId", executionId);
        jsonObject.put("region", regionForActivemonitor);
        JSONArray jsonArray = new JSONArray();
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--remote-allow-origins=*");
            LinkedHashMap browserName = new LinkedHashMap();
            LinkedHashMap browserObject = new LinkedHashMap();

            WebDriver driver ;
            if(sauceLabs){
                URL remoteUrls = new URL(remoteUrl);
                driver = new RemoteWebDriver(remoteUrls, options);
            }
            else{
                System.setProperty("webdriver.chrome.driver", chromeDriver);
                driver  = new ChromeDriver(options);
            }
            driver.manage().window().setPosition(new Point(0, 0));
            driver.manage().window().fullscreen();
            driver.manage().window().setSize(new Dimension(1980, 1080));

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.get(url);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
            long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
            long totalLoadTime = loadEventEnd - fetchStart;
            browserObject.put("timeStamp",Instant.now());
            browserObject.put("loadTime", totalLoadTime);
            browserName.put("chrome", browserObject);
            jsonArray.add(browserName);
            driver.quit();

        } catch (Exception e) {
            log.info("Exception in Chrome" + e);
        }
        jsonObject.put("browser",jsonArray);
        saveActiveMonitorMetrics(jsonObject);
        return "active monitor Response";
    }
    public String addActiveMonitorMetrics(String url, String projectId, String executionId, ArrayList regionAndUrl,String env,JSONObject metrics,String authToken) {
        int interval = Integer.parseInt(metrics.get("interval").toString());
        int duration = Integer.parseInt(metrics.get("duration").toString());
        ScheduledExecutorService schduler= Executors.newScheduledThreadPool(1);
        final long[] startTime = {0};
        ActiveMonitor activeMonitor = activeMonitorRepository.findByExecutionId(executionId);


        Runnable task=new Runnable() {
            @Override
            public void run() {
                if(startTime[0] ==0){
                    startTime[0] =System.currentTimeMillis();
                }
                switch (env){
                    case "Saucelabs":{
                    try {

                        log.info("Inside Task");

                        for (int ri = 0; ri < regionAndUrl.size(); ri++) {
                            Map regionUrl=(LinkedHashMap)regionAndUrl.get(ri);
                            String remoteUrlForActiveMonitor = regionUrl.get("activeMonitorUrl").toString();
                            String regionForActivemonitor=regionUrl.get("activeMonitorRegion").toString();
                            log.info(remoteUrlForActiveMonitor);
                            log.info(runActiveMonitor(url,remoteUrlForActiveMonitor,executionId,true,regionForActivemonitor,metrics));

                        }
                    } catch (Exception e) {
                        log.info("Exception in addactivemonitormetrics =========   " + e);
                        activeMonitor.setExecutionId(executionId);
                        activeMonitor.setError(true);
                        activeMonitor.setErrorMessage(e.toString());
                        activeMonitor.setProjectId(projectId);
                        activeMonitor.setUrl(url);
                        activeMonitorRepository.save(activeMonitor);
                    }
                    break;
                    }
                    case "Local":{
                        try{
                            runActiveMonitor(url,null,executionId,false,"Local",metrics);
                        }catch (Exception e) {
                            log.info("Exception in addactivemonitormetrics == =======   " + e);
                            activeMonitor.setExecutionId(executionId);
                            activeMonitor.setError(true);
                            activeMonitor.setErrorMessage(e.toString());
                            activeMonitor.setProjectId(projectId);
                            activeMonitor.setUrl(url);
                            activeMonitorRepository.save(activeMonitor);
                        }
                        break;
                    }
                    case "Cloud":{
                        for (int ri = 0; ri < regionAndUrl.size(); ri++) {
                            Map regionUrl=(LinkedHashMap)regionAndUrl.get(ri);
                            String remoteUrlForActiveMonitor = regionUrl.get("activeMonitorUrl").toString();
                            String regionForActivemonitor=regionUrl.get("activeMonitorRegion").toString();
                            log.info(remoteUrlForActiveMonitor);
                        JSONObject startNodeExecution = new JSONObject();
                            JSONObject activeMonitorObject = new JSONObject();
                            log.info(" url =========== " + url);
                            activeMonitorObject.put("url", url);
                            activeMonitorObject.put("executionId", executionId);
                            activeMonitorObject.put("region", regionForActivemonitor);
                            activeMonitorObject.put("machine", "dev");
                        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                        httpRequestFactory.setConnectTimeout(1500000);
                        httpRequestFactory.setReadTimeout(1500000);
                        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        headers.setBearerAuth(authToken.substring("Bearer ".length()));

                        ResponseEntity<String> responseEntity = null;
                        String responseForStartPerformance = "";

                        try {
                            log.debug("Executing Start PerformanceCommand in Windows Machine");
                            HttpEntity<JSONObject>  entity = new HttpEntity<>(activeMonitorObject, headers);
                            responseEntity = restTemplate.exchange(remoteUrlForActiveMonitor+":5001/selenium/activeMonitor", HttpMethod.POST, entity, String.class);
                            log.info("REsponse for start perfomance ====================    ");
                        } catch (Exception e) {
                            log.error("Exception in start performance : " + e);
                            if(activeMonitor != null) {
                                activeMonitor.setUrl(url);
                                activeMonitor.setStatus("InComplete");
                                activeMonitor.setError(true);
                                activeMonitor.setErrorMessage("Error in remote machine");
                                activeMonitorRepository.save(activeMonitor);
                            }}

                        }
                        break;
                    }
                }
            }
        };
        int delay=0;
        int period=10;
//        int duration=4;
        Future<?> future= schduler.scheduleAtFixedRate(task,delay,interval, TimeUnit.MINUTES);
        ScheduledFuture<?> canceller=schduler.schedule(new Runnable() {
            @Override
            public void run() {
                future.cancel(true);
                log.info("Task cancelled");
            }
        },duration,TimeUnit.HOURS);
        return "Execution Completed";

    }

    public JSONObject getActiveMonitorAverageLoadTime(String projectId) {
        JSONArray result = new JSONArray();
        JSONObject resultObject=new JSONObject();
        ActiveMonitor activeMonitor = activeMonitorRepository.findTopByProjectIdOrderByTimestampDesc(projectId);
        if(activeMonitor != null) {
            JSONObject responseTimeObject = (JSONObject) activeMonitor.getResponseTime();
            Set<String> keys = new HashSet<>();
            if(responseTimeObject != null) {
                keys = responseTimeObject.keySet();
            }
            for(String key : keys) {
                ArrayList region = (ArrayList) responseTimeObject.get(key);
                int count = 0;
                double loadTime = 0.0;
                JSONArray loadTimeAndtimeStampArray = new JSONArray();

                int interval = 0;
                String prevTimeStamp = null;

                for(int i = 0; i < region.size(); i++) {
                    JSONObject currentloadTimeAndTimeStampObject = new JSONObject();
                    LinkedHashMap currentObject = (LinkedHashMap) region.get(i);
                    LinkedHashMap firstTimeStamp=(LinkedHashMap) region.get(0);
                    String currentLoadTime = currentObject.get("loadTime").toString();
                    String currentTimeStamp = currentObject.get("timeStamp").toString();
                    String firstTimeStampValue = firstTimeStamp.get("timeStamp").toString();
                    currentloadTimeAndTimeStampObject.put("timeStamp", currentTimeStamp);
                    if(prevTimeStamp == null) {
                        currentloadTimeAndTimeStampObject.put("Interval", 0);
                        currentloadTimeAndTimeStampObject.put("loadTime", currentLoadTime);
                    }
                    else {

                        interval += activeMonitor.getInterval();
                        currentloadTimeAndTimeStampObject.put("Interval", interval);
                        currentloadTimeAndTimeStampObject.put("loadTime", currentLoadTime);
                    }
                    loadTimeAndtimeStampArray.add(currentloadTimeAndTimeStampObject);
                    loadTime += Double.parseDouble(currentLoadTime);
                    count += 1;
                    prevTimeStamp = currentTimeStamp;
                }
                double averageLoadtime = loadTime / (double) count;
                JSONObject currentObject = new JSONObject();
                currentObject.put("Region", key);
                currentObject.put("AverageLoadTime", averageLoadtime);
                currentObject.put("LoadTimeAndTimeStamp", loadTimeAndtimeStampArray);
                result.add(currentObject);
            }
            resultObject.put("url",activeMonitor.getUrl());
            resultObject.put("error",activeMonitor.isError());
            resultObject.put("errorMessage",activeMonitor.getErrorMessage());
            resultObject.put("activeResponse",result);
            resultObject.put("timestamp",activeMonitor.getTimestamp());
            resultObject.put("status",activeMonitor.getStatus());

            resultObject.put("interval",activeMonitor.getInterval());

            resultObject.put("duration",activeMonitor.getDuration());
        }
        return resultObject;

    }

 }
