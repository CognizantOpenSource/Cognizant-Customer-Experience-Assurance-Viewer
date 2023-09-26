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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Service
@Slf4j
public class ExecutionReportService {

    @Autowired
    PerformanceReportRepository performanceRepo;

    @Autowired
    SeoMetricsDataRepository seoMetricsDataRepository;

    @Autowired
    OmniChannelPerformanceRepository omniRepo;

    @Autowired
    AccessibilityMetricsDataRepository accessibilityRepo;

    @Autowired
    SecurityMetricsRepository securityMetricsRepository;

    @Autowired
    RegionRepositoryForPerformance repoForPerformanceRegion;

    @Autowired
    SeoMetricsDataRepository seoMetricsRepository;
    @Autowired
    ActiveMonitorRepository activeMonitorRepository;
    @Value("${urlforperformance}")
    String urlForPerformance;

    @Value("${saucelabs}")
    private boolean sauceLabs;
    @Value("${chromeDriverLocation}")
    private String chromeDriver;
    @Value("${edgeDriverLocation}")
    private String edgeDrivers;

    @Value("${firefoxBinaryLocation}")
    private String firefoxBinaryLocation;

    @Value("${firefoxDriverLocation}")
    private String firefoxDrivers;

    @Value("${machineName}")
    private String machineName;

    @Value("${urlforreadlogs}")
    private String urlForReadLogs;


    @Value("${urlforactivemonitor}")
    private String urlforactivemonitor;

    @Value("${urlforseo}")
    private String urlForSeo;


    @Value("${remoteUrlForOmnichannelPerformance}")
    private String remoteUrlForOmnichannelPerformance;

    @Value("${urlforsecurity}")
    String urlForSecurity;


    @Value("${urlforaccessibility}")
    String urlForAccessibility;

    private String authToken = null;

    public String generateExecutionId(JSONObject metrics) {
        log.info("Generate Execution Id");
        UUID uniqueKey = UUID.randomUUID();
        String instant = Instant.now().toString();

        Boolean seoCheck = metrics.get("seoCheck")!=null?(Boolean)metrics.get("seoCheck"):false;
        if(seoCheck) {
            SeoMetricsData seoMetricsData = new SeoMetricsData();
            seoMetricsData.setStatus("InProgress");
            seoMetricsData.setExecutionId(uniqueKey.toString());
            seoMetricsData.setUrl(metrics.get("url").toString());
            seoMetricsData.setTimestamp(instant);
            seoMetricsData.setProjectId(metrics.get("projectId").toString());
            seoMetricsRepository.save(seoMetricsData);
        }
        Boolean performancesCheck = (Boolean) metrics.get("performancesCheck");
        if (performancesCheck) {
            Performances performancesMetrics = new Performances();
            performancesMetrics.setStatus("InProgress");
            performancesMetrics.setExecutionId(uniqueKey.toString());
            performancesMetrics.setTimestamp(instant);
            performancesMetrics.setProjectId(metrics.get("projectId").toString());
            performanceRepo.save(performancesMetrics);


        }
        Boolean crossBrowser = metrics.get("crossBrowser") != null ? (Boolean) metrics.get("crossBrowser") : false;
if(crossBrowser){
    OmnichannelPerformance omniChannel = new OmnichannelPerformance();
    omniChannel.setTimestamp(instant);;
    omniChannel.setExecutionId(uniqueKey.toString());
    omniChannel.setProjectId(metrics.get("projectId").toString());
    omniChannel.setUrl(metrics.get("url").toString());
    omniChannel.setStatus("InProgress");
    omniRepo.save(omniChannel);
}
        Boolean activeMonitorCheck = metrics.get("activeMonitorCheck")!=null?(Boolean)metrics.get("activeMonitorCheck"):false;
        if(activeMonitorCheck) {
            ActiveMonitor activeMonitor = new ActiveMonitor();
            activeMonitor.setStatus("InProgress");
            activeMonitor.setExecutionId(uniqueKey.toString());
            activeMonitor.setUrl(metrics.get("url").toString());
            activeMonitor.setTimestamp(instant);
            activeMonitor.setInterval(metrics.get("interval") != null ? Integer.parseInt(metrics.get("interval").toString()) : 10);
            activeMonitor.setDuration(metrics.get("duration") != null ? Integer.parseInt(metrics.get("duration").toString()) : 4);
            activeMonitor.setProjectId(metrics.get("projectId").toString());
            activeMonitorRepository.save(activeMonitor);
        }
        Boolean accessibilityCheck = (Boolean) metrics.get("accessibilityCheck");
        if (accessibilityCheck) {
            AccessibilityMetricsData accessibilityMetricsData = new AccessibilityMetricsData();
            accessibilityMetricsData.setExecutionId(uniqueKey.toString());
            accessibilityMetricsData.setStatus("InProgress");
            accessibilityMetricsData.setTimestamp(instant);
            accessibilityMetricsData.setProjectId(metrics.get("projectId").toString());
            accessibilityRepo.save(accessibilityMetricsData);
        }
        Boolean securityCheck = (Boolean) metrics.get("securityCheck");
        if (securityCheck) {
            SecurityMetricsData securityMetricsData = new SecurityMetricsData();
            securityMetricsData.setExecutionId(uniqueKey.toString());
            securityMetricsData.setStatus("InProgress");
            securityMetricsData.setTimestamp(instant);
            securityMetricsData.setProjectId(metrics.get("projectId").toString());
            securityMetricsRepository.save(securityMetricsData);
        }

        return uniqueKey.toString();
    }

    public Boolean validatePerformanceUrl(String url) {
        try {

            Connection.Response res = Jsoup.connect(url).followRedirects(false) //
                    .timeout(200000) //
                    .method(Connection.Method.GET).execute();
            log.info(" Validate Performance : " + res + ","+ res.statusCode());

            return (res.statusCode() >= 200 && res.statusCode() <= 399)  ;
        } catch (SSLException ssl) {
            log.error("Exception while validating performance: ", ssl);
            return true;
        } catch (Exception ex) {
            log.error("Exception while validating performance: ", ex);
            return false;

        }
    }

    public Performances getPerformancesMetricsData(String projectId,String authToken) {

        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken.substring("Bearer ".length()));

        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        ResponseEntity<Performances> responseEntity = restTemplate.exchange(urlForPerformance
                + "/getPerformances?projectId="+projectId, HttpMethod.GET, entity, Performances.class);
        log.info("response from performance");
        return responseEntity.getBody();
    }
    public JSONObject getActiveMonitor(String projectId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject resultObject=new JSONObject();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        try {

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(authToken.substring("Bearer ".length()));

            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange( urlforactivemonitor + "/getActiveMonitorRegionAndLoadTime"+ "/?projectId="+projectId,
                    HttpMethod.GET, entity, JSONObject.class);
            resultObject = (JSONObject) responseEntity.getBody();

        }
        catch(Exception e) {
            log.error("Exception active Monitor"+e);
        }

        return resultObject;
    }
    public JSONObject getSeo(String projectId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject resultObject=new JSONObject();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        JSONArray resultArray = new JSONArray();

        try {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(authToken.substring("Bearer ".length()));

            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange( urlForSeo + "/getSeoMetricsData"+ "/?projectId="+projectId,
                    HttpMethod.GET, entity, JSONObject.class);
            resultObject = (JSONObject) responseEntity.getBody();

        }
        catch(Exception e) {
            log.error("Exception seo"+e);
        }
        return resultObject;
    }

    public AccessibilityMetricsData getAccessibilityData(String id, String authToken) {

        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken.substring("Bearer ".length()));

        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        ResponseEntity<AccessibilityMetricsData> responseEntity = restTemplate.exchange(urlForAccessibility
                + "/getAccessibilityMetricsData?projectId="+id, HttpMethod.GET, entity, AccessibilityMetricsData.class);
        log.info("get Accessibility data");
        return responseEntity.getBody();
    }

    public SecurityMetricsData getSecurity(String id, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken.substring("Bearer ".length()));

        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        ResponseEntity<SecurityMetricsData> responseEntity = restTemplate.exchange(
                urlForSecurity + "/getSecurityMetricsData?projectId="+id, HttpMethod.GET, entity, SecurityMetricsData.class);
        log.info("get Security data");
        return responseEntity.getBody();
    }

    public JSONArray getSecurityOwasp(String executionId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        JSONArray owaspGuidelines = null;
        try {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(authToken.substring("Bearer ".length()));

            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
            ResponseEntity<JSONArray> responseEntity = restTemplate.exchange(urlForSecurity + "/getSecurityOwaspData?executionId="+executionId,
                    HttpMethod.GET, entity, JSONArray.class);
            owaspGuidelines = responseEntity.getBody();
        }
        catch(Exception e) {
            log.error("Exception"+e);
        }

        return owaspGuidelines;
    }

    public JSONObject getSummaryMetrics(String projectId,String authToken) {
        Performances perfResponse = performanceRepo.findTopByProjectIdOrderByTimestampDesc(projectId);
        AccessibilityMetricsData accessibilityMetrics = accessibilityRepo.findTopByProjectIdOrderByTimestampDesc(projectId);
        SecurityMetricsData securityMetrics = securityMetricsRepository.findTopByProjectIdOrderByTimestampDesc(projectId);
        ActiveMonitor activeMonitor = activeMonitorRepository.findTopByProjectIdOrderByTimestampDesc(projectId);
        SeoMetricsData seoMetricsData = seoMetricsRepository.findTopByProjectIdOrderByTimestampDesc(projectId);
        OmnichannelPerformance omnichannelPerformance=omniRepo.findTopByProjectIdOrderByTimestampDesc(projectId);


        JSONObject jsonObj = new JSONObject();

        String timestamp = "";

        if (perfResponse != null) {
            if (timestamp.equals("")) {
                timestamp = perfResponse.getTimestamp();
            } else {
                int compare = timestamp.compareTo(perfResponse.getTimestamp());
                if (compare < 0) {
                    timestamp = perfResponse.getTimestamp();
                }
            }
            if (perfResponse.getStatus().equals("InProgress")) {
                jsonObj.put("speedIndex", "InProgress");
            } else {
                if (perfResponse.getError()) {
                    jsonObj.put("speedIndex", "Scan Not Supported");
                } else {
                    jsonObj.put("speedIndex", perfResponse.getSpeedIndex());
                }
            }
        } else {
            jsonObj.put("speedIndex", "");
        }

        if (accessibilityMetrics != null) {
            if (timestamp.equals("")) {
                timestamp = accessibilityMetrics.getTimestamp();
            } else {
                int compare = timestamp.compareTo(accessibilityMetrics.getTimestamp());
                if (compare < 0) {
                    timestamp = accessibilityMetrics.getTimestamp();
                }
            }
            if (accessibilityMetrics.getStatus().equals("InProgress")) {
                jsonObj.put("accessibility", "InProgress");
            } else {
                if (accessibilityMetrics.getError()) {

                    jsonObj.put("accessibility", "Scan Not Supported");
                } else {
                    JSONObject wcag = (JSONObject) accessibilityMetrics.getWcag();
                    int aaFail = (int) wcag.get("aaFail");
                    int aFail = (int) wcag.get("aFail");
                    if (aFail > 0 || aaFail > 0) {
                        jsonObj.put("accessibility", "Violation Found");
                    } else {
                        jsonObj.put("accessibility", "Violation Not Found");
                    }

                }
            }
        } else {
            jsonObj.put("accessibility", "");
        }
        if(omnichannelPerformance!=null){
            if(timestamp.equals("")){
                timestamp=omnichannelPerformance.getTimestamp();
            }else{
                int compare = timestamp.compareTo(omnichannelPerformance.getTimestamp());
                if (compare < 0) {
                    timestamp = omnichannelPerformance.getTimestamp();
                }if (omnichannelPerformance.getStatus().equals("InProgress")) {
                    jsonObj.put("omniChannel", "InProgress");
                }else{
                    if(omnichannelPerformance.isError()){
                        jsonObj.put("omniChannel","Scan Not Supported");
                    }else {
                        JSONObject omniObject=getOmnichannelPerformance(projectId);
                        JSONArray omniArray= (JSONArray) omniObject.get("omniResponse");
                        jsonObj.put("omniChannel",omniArray);
                    }
                }
            }
        }else{
            jsonObj.put("omniChannel","");
        }
        if (securityMetrics != null) {
            if (timestamp.equals("")) {
                timestamp = securityMetrics.getTimestamp();
            } else {
                int compare = timestamp.compareTo(securityMetrics.getTimestamp());
                if (compare < 0) {
                    timestamp = securityMetrics.getTimestamp();
                }
            }
            if (securityMetrics.getStatus().equals("InProgress")) {
                jsonObj.put("security", "InProgress");
            } else {
                if (securityMetrics.getError()) {


                        jsonObj.put("security", "Scan not supported");
                } else {

                    JSONObject passiveScanObj = (JSONObject) securityMetrics.getPassiveScan();
                    ArrayList securityChecksArr = (ArrayList) passiveScanObj.get("securityChecks");


                    Boolean securityVulnerableCheck = false;
                    for (Object securityChecks : securityChecksArr) {

                        JSONObject securityCheck = new JSONObject((Map) securityChecks);

                        if (securityCheck.get("Status").toString().equals("FAIL")) {
                            securityVulnerableCheck = true;
                        }
                    }
                    if (securityVulnerableCheck) {
                        jsonObj.put("security", "Vulnerability Found");
                    } else {
                        jsonObj.put("security", "Vulnerability Not Found");
                    }
                }
            }
        } else {
            jsonObj.put("security", "");
        }
        if(activeMonitor!=null){
            if(timestamp.equals("")){
                timestamp=activeMonitor.getTimestamp();
            }else{
                int compare = timestamp.compareTo(activeMonitor.getTimestamp());
                if (compare < 0) {
                    timestamp = activeMonitor.getTimestamp();
                }
            } if (activeMonitor.getStatus().equals("InProgress")) {
                jsonObj.put("activeMonitor", "InProgress");
            }else{
                if(activeMonitor.isError()){
                    jsonObj.put("activeMonitor","Scan Not Supported");
                }else {
                    JSONObject activeMonitorObject = getActiveMonitor(projectId, authToken);
                    ArrayList activeMonitorJson = (ArrayList) activeMonitorObject.get("activeResponse");
                    jsonObj.put("activeMonitor",activeMonitorJson);
                }
            }
        }else{
            jsonObj.put("activeMonitor","");
        }

        if(seoMetricsData != null) {
            if(timestamp.equals("")) {
                timestamp = seoMetricsData.getTimestamp();
            }
            else{
                int compare = timestamp.compareTo(seoMetricsData.getTimestamp());
                if (compare < 0) {
                    timestamp = seoMetricsData.getTimestamp();
                }
            }
            if (seoMetricsData.getStatus().equals("InProgress")) {
                jsonObj.put("seoScore", "InProgress");
            } else {
                if (seoMetricsData.getError()) {
                    jsonObj.put("seoScore", "Scan Not Supported");
                } else {
                    jsonObj.put("seoScore", seoMetricsData.getScore());
                }
            }
        } else {
            jsonObj.put("seoScore", "");
        }


        jsonObj.put("timestamp", timestamp);
        return jsonObj;
    }

    public String addActiveMonitorMetrics(String url, String executionId, String projectId, int interval, int duration, ArrayList region,String env, String authtoken) {
        authToken=authtoken;
        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", url);
        jsonObject.put("executionId", executionId);
        jsonObject.put("projectId", projectId);
        jsonObject.put("interval", interval);
        jsonObject.put("duration", duration);
        jsonObject.put("activeMonitorUrlAndRegion", region);
        jsonObject.put("env",env);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken.substring("Bearer ".length()));
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange( urlforactivemonitor + "/insertMetricsData", HttpMethod.POST, entity, String.class);
            return responseEntity.getBody();
        } catch (Exception ex) {
            log.error("insertMetricsData error" + ex);
            String instant = Instant.now().toString();

            return "error";
        }
    }

    public String saveOmnichannelMetrics(LinkedHashMap metrics) {
        OmnichannelPerformance omniChannel = omniRepo.findByExecutionId(metrics.get("executionId").toString());
        String url = metrics.get("url").toString();
        Boolean error = Boolean.valueOf(metrics.get("error").toString());
        ArrayList browsers = (ArrayList) metrics.get("browser");
        JSONArray browserJson=new JSONArray();
        try {
            for (int i = 0; i < browsers.size(); i++) {
                LinkedHashMap currentObject = (LinkedHashMap) browsers.get(i);
                Set<String> currentSet = currentObject.keySet();
                String browserName = "";
                for (String word : currentSet) {
                    browserName = word;
                }
                LinkedHashMap browserObject = (LinkedHashMap) currentObject.get(browserName);
                if (browserObject != null) {
                    String base64String = browserObject.get("screenShotPath").toString();

                    browserObject.put("screenShotPath", base64String);

                   browserJson.add(browsers.get(i));
                }

            }


            omniChannel.setPerformanceMetrics(browserJson);
            omniChannel.setErrorMessage("");
            omniChannel.setError(false);
            omniChannel.setUrl(metrics.get("url").toString());
            omniChannel.setTimestamp(Instant.now().toString());
            omniChannel.setStatus("Completed");

//        omniChannel.setPerformanceMetrics(resultArray);
            log.info("Updated performance metrics ======= ");
            omniRepo.save(omniChannel);
            return "Completed omnichannel from selenium project";
        }
        catch(Exception e) {
            //System.out.println("Exception in omnichannel adding" + e);
            omniChannel.setErrorMessage("Scan Not Supported");
            omniChannel.setError(true);
            omniChannel.setUrl(metrics.get("url").toString());
            omniChannel.setTimestamp(Instant.now().toString());
            omniChannel.setStatus("InComplete");
            return "Error in adding omnichannl metrics";

        }
    }

    public String TestforBrowsers(String remoteurl,JSONObject metrics,String executionId) {

        LinkedHashMap jsonObject = new LinkedHashMap();
        String url = metrics.get("url").toString();
        String region = metrics.get("region").toString();
        jsonObject.put("url", url);
        jsonObject.put("executionId", executionId);
        jsonObject.put("region", region);
        JSONArray jsonArray = new JSONArray();


        try {
            try {
                log.info("Started chrome");
                ChromeOptions options = new ChromeOptions();
                //System.setProperty("webdriver.http.factory", "jdk-http-client");
                options.addArguments("--headless");
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--enable-devtools-remote");
                LinkedHashMap browserName = new LinkedHashMap();
                LinkedHashMap browserObject = new LinkedHashMap();

                HashMap statusMap = new HashMap();
                WebDriver driver ;
                if(sauceLabs){
                    URL remoteUrls = new URL(remoteurl);
                    driver = new RemoteWebDriver(remoteUrls, options);
                    driver= new Augmenter().augment( driver );
                }
                else{
                    System.setProperty("webdriver.chrome.driver", chromeDriver);
                    driver  = new ChromeDriver(options);

                    DevTools devTools=((HasDevTools) driver).getDevTools();
                    devTools.createSession();
                    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                    devTools.addListener(Network.responseReceived(), response ->
                    {
                        org.openqa.selenium.devtools.v114.network.model.Response res = response.getResponse();
                        if(res.getStatus()!=null){
                            if(Integer.parseInt(res.getStatus().toString())>399&&Integer.parseInt(res.getStatus().toString())<600){
                                if(statusMap.get(res.getStatus())==null){

                                    statusMap.put(res.getStatus(),1);
                                }
                                else {
                                    statusMap.put(res.getStatus(),Integer.parseInt(statusMap.get(res.getStatus()).toString())+1);
                                }}}
                    });
                }
                Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();


                String version = capabilities.getBrowserVersion();
                driver.manage().window().setPosition(new Point(0, 0));
                driver.manage().window().fullscreen();
                driver.manage().window().setSize(new Dimension(1980, 1080));

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get(url);
                Thread.sleep(10000);

                JavascriptExecutor js = (JavascriptExecutor) driver;
                long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
                long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
                long totalLoadTime = loadEventEnd - fetchStart;
                browserObject.put("loadTime", totalLoadTime);
                String file = "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

                browserObject.put("httpCode",statusMap);
                browserObject.put("screenShotPath", file);
                browserObject.put("version", version);
                browserName.put("chrome", browserObject);
                jsonArray.add(browserName);
                driver.quit();

            } catch (Exception e) {
                log.error("Exception in Chrome" + e);
            }
            try {

                log.info("Started Edge");
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setHeadless(true);
                edgeOptions.addArguments("--remote-allow-origins=*");

                LinkedHashMap browserName = new LinkedHashMap();
                LinkedHashMap browserObject = new LinkedHashMap();
                WebDriver edgeDriver;

                HashMap statusMap = new HashMap();
                if(sauceLabs){
                URL remoteUrls = new URL(remoteurl);
                    edgeDriver = new RemoteWebDriver(remoteUrls, edgeOptions);

                }
                else{

                    System.setProperty("webdriver.edge.driver", edgeDrivers);
                     edgeDriver = new EdgeDriver(edgeOptions);
                    DevTools devTools=((HasDevTools) edgeDriver).getDevTools();
                    devTools.createSession();
                    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                    devTools.addListener(Network.responseReceived(), response ->
                    {
                        org.openqa.selenium.devtools.v114.network.model.Response res = response.getResponse();
                        if(res.getStatus()!=null){
                            if(Integer.parseInt(res.getStatus().toString())>399&&Integer.parseInt(res.getStatus().toString())<600){
                        if(statusMap.get(res.getStatus())==null){

                            statusMap.put(res.getStatus(),1);
                        }
                        else {
                            statusMap.put(res.getStatus(),Integer.parseInt(statusMap.get(res.getStatus()).toString())+1);
                        }}}
                    });
                }
                Capabilities capabilities = ((RemoteWebDriver) edgeDriver).getCapabilities();

                String version = capabilities.getBrowserVersion();
                edgeDriver.manage().window().setPosition(new Point(0, 0));
                edgeDriver.manage().window().fullscreen();
                edgeDriver.manage().window().setSize(new Dimension(1980, 1080));
                edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                edgeDriver.get(url);

                Thread.sleep(10000);
                JavascriptExecutor js = (JavascriptExecutor) edgeDriver;
                long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
                long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
                long totalLoadTime = loadEventEnd - fetchStart;
                browserObject.put("loadTime", totalLoadTime);
                String file = "data:image/png;base64," + ((TakesScreenshot) edgeDriver).getScreenshotAs(OutputType.BASE64);

                browserObject.put("screenShotPath", file);
                browserObject.put("version", version);
                browserObject.put("httpCode", statusMap);
                browserName.put("edge", browserObject);
                jsonArray.add(browserName);
                edgeDriver.quit();
            } catch (Exception e) {
              log.error("Exception in Edge" + e);
            }
            try {

                log.info("Started Firefox");
                FirefoxOptions options = new FirefoxOptions();
                LoggingPreferences loggingPreferences = new LoggingPreferences();
                loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
                options.addArguments("--headless");
                LinkedHashMap browserName=new LinkedHashMap();
                LinkedHashMap browserObject = new LinkedHashMap();
                HashMap statusMap = new HashMap();
                WebDriver firefoxDriver;
                if(sauceLabs){
                    URL remoteUrls = new URL(remoteurl);
                    firefoxDriver = new RemoteWebDriver(remoteUrls, options);

                }
                else{
                    options.setBinary(firefoxBinaryLocation);
                    System.setProperty("webdriver.gecko.driver",firefoxDrivers );
                    firefoxDriver = new FirefoxDriver(options);
                }
                Capabilities capabilities=((RemoteWebDriver) firefoxDriver).getCapabilities();


                String version=capabilities.getBrowserVersion();
                firefoxDriver.manage().window().setPosition(new Point(0, 0));
                firefoxDriver.manage().window().fullscreen();
                firefoxDriver.manage().window().setSize(new Dimension(1980, 1080));

                firefoxDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                log.info("StatusMap"+statusMap);
                firefoxDriver.get(url);
                Thread.sleep(10000);
                JavascriptExecutor js = (JavascriptExecutor) firefoxDriver;
                long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
                long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
                long totalLoadTime = loadEventEnd - fetchStart;
                browserObject.put("loadTime", totalLoadTime);
                String file = "data:image/png;base64,"+((TakesScreenshot) firefoxDriver).getScreenshotAs(OutputType.BASE64);


                browserObject.put("screenShotPath", file);
                browserObject.put("version", version);
                browserObject.put("httpCode", "null");
                browserName.put("FireFox",browserObject);
                jsonArray.add(browserName);
                firefoxDriver.quit();
            } catch (Exception e) {
              log.error("Exception in Firefox"+e);
            }
            if(sauceLabs)
            {try {

                log.info("Started Safari");
                SafariOptions browserOptions = new SafariOptions();
                browserOptions.setPlatformName("macOS 13");
                browserOptions.setBrowserVersion("latest");
                LinkedHashMap browserName = new LinkedHashMap();
                LinkedHashMap browserObject = new LinkedHashMap();
                WebDriver safariDriver;
                URL remoteUrls = new URL(remoteurl);
                safariDriver = new RemoteWebDriver(remoteUrls, browserOptions);
                Capabilities capabilities = ((RemoteWebDriver) safariDriver).getCapabilities();
                String version = capabilities.getBrowserVersion();
                safariDriver.manage().window().setPosition(new Point(0, 0));
                safariDriver.manage().window().fullscreen();
                safariDriver.manage().window().setSize(new Dimension(1980, 1080));
                safariDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

                safariDriver.get(url);
                Thread.sleep(10000);
                JavascriptExecutor js = (JavascriptExecutor) safariDriver;
                long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
                long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
                long totalLoadTime = loadEventEnd - fetchStart;
                browserObject.put("loadTime", totalLoadTime);
                String file = "data:image/png;base64," + ((TakesScreenshot) safariDriver).getScreenshotAs(OutputType.BASE64);
                browserObject.put("screenShotPath", file);
                browserObject.put("version", version);
                browserObject.put("httpCode","null");
                browserName.put("safari", browserObject);
                jsonArray.add(browserName);
                safariDriver.quit();
            } catch (Exception e) {
                log.error("Exception in safari" + e);
            }}


            jsonObject.put("browser", jsonArray);
            jsonObject.put("error", "false");
        } catch (Exception e) {

            jsonObject.put("error", "true");
            log.error("Exception in cross-browser");
        }

        try {
            String str = saveOmnichannelMetrics(jsonObject);
        } catch (Exception e) {
            log.error("exception"+e);
        }
        log.info("Cross Browser Completed");
        return "Cross Browser Completed";

    }

    public String requestForOmniChannel(JSONObject metrics,String executionId,String environment,String authtoken){
        System.out.println("inside omni");
        String env;
        if(sauceLabs){

             env="Sauce";
        }
        else if(environment=="Cloud"){
            env="Cloud";
        }else {
             env = environment;
        }
        System.out.println(env);
        String lighthouseResult="";
        switch (env){
            case "Sauce":{
            try {
               log.info("Inside Sauce");
                TestforBrowsers(remoteUrlForOmnichannelPerformance,metrics,executionId);
                log.info("completed sacuelabs");
            }
            catch (Exception ex){
                OmnichannelPerformance omniChannel = omniRepo.findTopByProjectIdOrderByTimestampDesc(metrics.get("projectId").toString());
                omniChannel.setErrorMessage("Scan Not Supported");
                omniChannel.setError(true);
                omniChannel.setStatus("InComplete");
               log.error("Exception Omnichannel metrics "+ex);
                omniRepo.save(omniChannel);
                return "Error";
            }
            break;
        }
            case "Local":{
                try {
                   log.info("Inside Local");
                    TestforBrowsers("Local",metrics,executionId);
                    log.info("Completed Local");
                }
                catch (Exception ex){
                    OmnichannelPerformance omniChannel = omniRepo.findTopByProjectIdOrderByTimestampDesc(metrics.get("projectId").toString());
                    omniChannel.setErrorMessage("Scan Not Supported");
                    omniChannel.setError(true);
                    omniChannel.setStatus("InComplete");
                    log.error("Exception Omnichannel metrics "+ex);
                    omniRepo.save(omniChannel);
                    return "Error";
                }
                break;
        }
            case "Cloud": {
            try {
                log.info("Inside Cloud");
                authToken = authtoken;
                String url = metrics.get("url") != null ? metrics.get("url").toString() : "";
                LinkedHashMap urlRegion=(LinkedHashMap)  metrics.get("urlAndRegion");
                String remoteUrlForOmniChannel=urlRegion.get("environmentUrl") == null ? null: urlRegion.get("environmentUrl").toString();
                String regionForOmniChannel=urlRegion.get("environmentRegion")==null ?  null :urlRegion.get("environmentRegion").toString();

                log.info(remoteUrlForOmniChannel);
                JSONObject startNodeExecution = new JSONObject();
                JSONObject OmniChannelObject = new JSONObject();
                log.info(" url =========== " + url);
                OmniChannelObject.put("performanceURL", url);
                OmniChannelObject.put("executionId", executionId);
                OmniChannelObject.put("region", regionForOmniChannel);
                OmniChannelObject.put("machine", "dev");
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
                    log.debug("Executing Start OmniChannel in Windows Machine");
                    HttpEntity<JSONObject>  entity = new HttpEntity<>(OmniChannelObject, headers);
                    responseEntity = restTemplate.exchange(remoteUrlForOmniChannel+":5001/selenium/crossBrowser", HttpMethod.POST, entity, String.class);
                    log.info("REsponse for start OmniChannel ====================    ");
                } catch (Exception e) {
                    log.error("Exception in start OmniChannel : " + e);
                    OmnichannelPerformance omniChannel = omniRepo.findTopByProjectIdOrderByTimestampDesc(metrics.get("projectId").toString());
                    if(omniChannel != null) {
                        omniChannel.setUrl(url);
                        omniChannel.setStatus("InComplete");
                        omniChannel.setError(true);
                        omniChannel.setErrorMessage("Error in remote machine");
                        omniRepo.save(omniChannel);
                    }}
                log.info("Completed Cloud");
            }
            catch (Exception ex){
                OmnichannelPerformance omniChannel = omniRepo.findTopByProjectIdOrderByTimestampDesc(metrics.get("projectId").toString());
                omniChannel.setErrorMessage("Scan Not Supported");
                omniChannel.setError(true);
                omniChannel.setStatus("InComplete");
                log.error("Exception Omnichannel metrics "+ex);
                omniRepo.save(omniChannel);
                return "Error";
            }
            break;
        }
        }
        return lighthouseResult;
    }

    public JSONObject getOmnichannelPerformance(String projectId) {
        JSONArray result = new JSONArray();
        OmnichannelPerformance omniChannel = omniRepo.findTopByProjectIdOrderByTimestampDesc(projectId);
        JSONObject omniResponse=new JSONObject();

        if(omniChannel != null && omniChannel.getErrorMessage() != null && !omniChannel.getErrorMessage().equals("")){
            return omniResponse;
        }

        if(omniChannel != null && omniChannel.getPerformanceMetrics()!=null) {
            JSONArray performanceMetrics = omniChannel.getPerformanceMetrics();

            for(int i = 0; i < performanceMetrics.size(); i++) {
                LinkedHashMap tempObject = (LinkedHashMap) performanceMetrics.get(i);
                if(tempObject.containsKey("chrome")) {

                    LinkedHashMap temp = (LinkedHashMap) tempObject.get("chrome");
                    temp.put("Browser", "Chrome");
                    result.add(temp);
                }
                else if(tempObject.containsKey("safari")) {
                    LinkedHashMap temp = (LinkedHashMap) tempObject.get("safari");
                    temp.put("Browser", "Safari");

                    if((long)temp.get("loadTime")>0){
                        result.add(temp);}
                }
                else if(tempObject.containsKey("edge")) {
                    LinkedHashMap temp = (LinkedHashMap) tempObject.get("edge");
                    temp.put("Browser", "Edge");

                    result.add(temp);
                }
                else if(tempObject.containsKey("FireFox")) {
                    LinkedHashMap temp = (LinkedHashMap) tempObject.get("FireFox");
                    temp.put("Browser", "Firefox");

                    result.add( temp);
                }

            }
            omniResponse.put("url",omniChannel.getUrl());
            omniResponse.put("errorMessage",omniChannel.getErrorMessage());
            omniResponse.put("timestamp",omniChannel.getTimestamp());
            omniResponse.put("omniResponse",result);
        }

        return omniResponse;
    }

    public String saveSecuritymetrics(JSONObject responseObj) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String instant = Instant.now().toString();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            headers.setBearerAuth(authToken.substring("Bearer ".length()));
            HttpEntity<JSONObject> entity = new HttpEntity<>(responseObj, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(urlForSecurity + "/insertMetricsData",
                    HttpMethod.POST, entity, String.class);
            //System.out.println("response for addsecurity====== " + responseEntity.getBody());

        } catch (Exception ex) {
            //System.out.println("Exception for addSecurity====" + ex);
            return "Error in adding securitymetrics";
        }
        return "Security Data Inserted successfully";
    }

    public String savePerformanceMetrics(JSONObject responseObj) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();;
        String instant = Instant.now().toString();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(authToken.substring("Bearer ".length()));
            HttpEntity<JSONObject> entity = new HttpEntity<>(responseObj, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(urlForPerformance + "/insertMetricsData",
                    HttpMethod.POST, entity, String.class);
           log.info("addPerformance ====== ");

        } catch (Exception ex) {
            log.error("Exception for addPerformance====" + ex);
            return "Error in adding PerformanceMetrics";
        }
        return "PerformanceMetrics Data Inserted successfully";
    }

    public String saveSeoMetrics(JSONObject responseObj) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();;
        String instant = Instant.now().toString();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            headers.setBearerAuth(authToken.substring("Bearer ".length()));
            HttpEntity<JSONObject> entity = new HttpEntity<>(responseObj, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(urlForSeo + "/insertMetricsData",
                    HttpMethod.POST, entity, String.class);
           log.info("response for addSeo ====== ");

        } catch (Exception ex) {
          log.error("Exception for addSeo====" + ex);
            return "Error in adding seometrics";
        }
        return "SeoMetrics Data Inserted successfully";
    }

    public String saveAccessibilityMetrics(JSONObject responseObj) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();;
        String instant = Instant.now().toString();

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            headers.setBearerAuth(authToken.substring("Bearer ".length()));
            HttpEntity<JSONObject> entity = new HttpEntity<>(responseObj, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(urlForAccessibility + "/insertMetricsData",
                    HttpMethod.POST, entity, String.class);
            log.info("addaccessibility ====== ");

        } catch (Exception ex) {
            log.error("Exception for addAccessibility====" + ex);
            return "Error in adding accessibilitymetrics";
        }
        return "AccessibilityMetrics Data Inserted successfully";
    }

    public String requestLHAPiForPerformancesAccessibilitySecuritySeo(String url, String executionId, JSONArray modules, String environment,String urlAndRegion,String environmentRegion, String authtoken) {
        authToken = authtoken;
        boolean errorInStartPerformance = false;
        log.info("Started Scans");
        Performances performance = performanceRepo.findByExecutionId(executionId);
        AccessibilityMetricsData accessibilityMetricsData = accessibilityRepo.findByExecutionId(executionId);
        SecurityMetricsData securityMetricsData = securityMetricsRepository.findByExecutionId(executionId);
        SeoMetricsData seoMetricsData = seoMetricsRepository.findByExecutionId(executionId);

        JSONObject startNodeExecution = new JSONObject();
        List<RegionForPerformance> allRegions = repoForPerformanceRegion.findAll();
        String restApiForLightHouse="";
        if(environment.equalsIgnoreCase("cloud")){
            restApiForLightHouse=urlAndRegion;
        }else{
            restApiForLightHouse="http://localhost";

            environmentRegion="Local";
        }

        JSONObject performances = new JSONObject();
       log.info(" url =========== " + url);
        performances.put("url", url);
        performances.put("executionId", executionId);
        performances.put("region",environmentRegion);
        performances.put("machine", machineName);
        performances.put("modules", modules);

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(1500000);
        httpRequestFactory.setReadTimeout(1500000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JSONObject> entity = new HttpEntity<>(startNodeExecution, headers);
        ResponseEntity<String> responseEntity = null;
        String responseForStartPerformance = "";

        try {
            log.debug("Executing Start PerformanceCommand in Windows Machine");
            entity = new HttpEntity<>(performances, headers);
            responseEntity = restTemplate.exchange(restApiForLightHouse+":5003/lighthouseAPI/startLighthouseServices", HttpMethod.POST, entity, String.class);
            responseForStartPerformance = responseEntity.getBody();
            log.info("started Lighthouse");
        } catch (Exception e) {
            log.error("Exception in start performance : " + e);
            if(performance != null) {
                performance.setUrl(url);
                performance.setRegion(environment); //  For region
                performance.setStatus("InComplete");
                performance.setError(true);
                performance.setErrorMessage("LG not available");
                Performances perf = performanceRepo.save(performance);
            }
            if(accessibilityMetricsData != null) {
                accessibilityMetricsData.setUrl(url);
                accessibilityMetricsData.setStatus("InComplete");
                accessibilityMetricsData.setError(true);
                accessibilityMetricsData.setErrorMessage("LG not available");
                AccessibilityMetricsData accessibilityMetricsData1 = accessibilityRepo.save(accessibilityMetricsData);
            }
            if(securityMetricsData != null) {
                securityMetricsData.setUrl(url);
                securityMetricsData.setStatus("InComplete");
                securityMetricsData.setError(true);
                securityMetricsData.setErrorMessage("LG not available");
                SecurityMetricsData securityMetricsData1 = securityMetricsRepository.save(securityMetricsData);
            }
            if (seoMetricsData != null) {
                seoMetricsData.setUrl(url);

                seoMetricsData.setStatus("InComplete");
                seoMetricsData.setError(true);
                seoMetricsData.setErrorMessage("LG not available");
                SeoMetricsData seoMetricsData1 = seoMetricsDataRepository.save(seoMetricsData);
            }
            errorInStartPerformance = true;
        }
        return errorInStartPerformance ? "error in adding metrics" : "Metrics added successfully";
    }
    public String saveActiveMonitorMetrics(String url, String executionId,String region,JSONObject responseObj) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", url);
        jsonObject.put("executionId", executionId);
        jsonObject.put("region", region);
        jsonObject.put("metrics",responseObj);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authToken.substring("Bearer ".length()));
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);
        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange( urlforactivemonitor + "/saveMetricsData", HttpMethod.POST, entity, String.class);
            log.error("addActiveMonitor ====== " );
            return responseEntity.getBody();
        } catch (Exception ex) {
            log.error("error" + ex);
            String instant = Instant.now().toString();

            return "error";
        }
    }

}
