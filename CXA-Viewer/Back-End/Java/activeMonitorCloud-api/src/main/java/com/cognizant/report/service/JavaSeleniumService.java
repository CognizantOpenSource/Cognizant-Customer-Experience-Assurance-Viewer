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

package com.cognizant.report.service;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v116.network.Network;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Slf4j
@Service
public class JavaSeleniumService {
    @Value("${chromeDriverLocation}")
    private String chromeDriver;
    @Value("${edgeDriverLocation}")
    private String edgeDrivers;

    @Value("${platform}")
    private String platform;


    @Value("${firefoxDriverLocation}")
    private String firefoxDrivers;

    @Value("${devUrl}")
    private String devUrl;

    @Value("${cxauser}")
    private String cxauser;

    @Value("${firefoxBinary}")
    private String firefoxBinary;
    @Value("${password}")
    private String password;

    public String StartCrossBrowser(JSONObject metrics) throws IOException {
        System.out.println("started");
        JSONObject jsonObject=new JSONObject();
        String url=metrics.get("performanceURL").toString();
        String executionId=metrics.get("executionId").toString();
        String region=metrics.get("region").toString();
        jsonObject.put("url",url);
        jsonObject.put("executionId",executionId);
        jsonObject.put("region",region);
        JSONArray jsonArray=new JSONArray();
        try {
            try {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170";
                options.addArguments(String.format("user-agent=%s", userAgent));
                //options.addArguments("disable-blink-features=AutomationControlled");
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("--enable-devtools-remote");
                options.addArguments("--disable-blink-features=AutomationControlled");
                JSONObject browserName = new JSONObject();
                JSONObject browserObject = new JSONObject();

                HashMap statusMap = new HashMap();
                WebDriver driver ;

                    System.setProperty("webdriver.chrome.driver", chromeDriver);
                    driver  = new ChromeDriver(options);

                    DevTools devTools=((HasDevTools) driver).getDevTools();
                    devTools.createSession();
                    devTools.send(org.openqa.selenium.devtools.v114.network.Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                    devTools.addListener(org.openqa.selenium.devtools.v114.network.Network.responseReceived(), response ->
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

                Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();


                String version = capabilities.getBrowserVersion();
                driver.manage().window().setPosition(new Point(0, 0));
                driver.manage().window().fullscreen();
                driver.manage().window().setSize(new Dimension(1980, 1080));

                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get(url);
                System.out.println(statusMap);
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
                log.info("Exception in Chrome" + e);
            }
            try {
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setHeadless(true);
                edgeOptions.addArguments("--remote-allow-origins=*");
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170";
                 edgeOptions.addArguments(String.format("user-agent=%s", userAgent));
                JSONObject browserName = new JSONObject();
                JSONObject browserObject = new JSONObject();
                WebDriver edgeDriver;

                HashMap statusMap = new HashMap();


                    System.setProperty("webdriver.edge.driver", edgeDrivers);
                    edgeDriver = new EdgeDriver(edgeOptions);
                    DevTools devTools=((HasDevTools) edgeDriver).getDevTools();
                    devTools.createSession();
                    devTools.send(org.openqa.selenium.devtools.v116.network.Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                    devTools.addListener(org.openqa.selenium.devtools.v116.network.Network.responseReceived(), response ->
                    {
                        org.openqa.selenium.devtools.v116.network.model.Response res = response.getResponse();
                        if(res.getStatus()!=null){
                            if(Integer.parseInt(res.getStatus().toString())>399&&Integer.parseInt(res.getStatus().toString())<600){
                                if(statusMap.get(res.getStatus())==null){

                                    statusMap.put(res.getStatus(),1);
                                }
                                else {
                                    statusMap.put(res.getStatus(),Integer.parseInt(statusMap.get(res.getStatus()).toString())+1);
                                }}}
                    });

                Capabilities capabilities = ((RemoteWebDriver) edgeDriver).getCapabilities();

                String version = capabilities.getBrowserVersion();
                edgeDriver.manage().window().setPosition(new Point(0, 0));
                edgeDriver.manage().window().fullscreen();
                edgeDriver.manage().window().setSize(new Dimension(1980, 1080));
                edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                edgeDriver.get(url);
                Thread.sleep(10000);
                System.out.println(statusMap);
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
                log.info("Exception in Edge" + e);
            }
            try {
                log.info("Firefox inside");
                FirefoxOptions options = new FirefoxOptions();
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170";
                options.addArguments(String.format("user-agent=%s", userAgent));
               options.addArguments("--headless");
                JSONObject browserName=new JSONObject();
                JSONObject browserObject = new JSONObject();
                options.setLogLevel(FirefoxDriverLogLevel.DEBUG);
                System.setProperty("webdriver.firefox.logfile","C:\\Users\\Administrator\\Downloads\\edgedriver_win64\\da.txt");
                WebDriver firefoxDriver;

                log.info(firefoxBinary);
                    options.setBinary(firefoxBinary);
                    System.setProperty("webdriver.gecko.driver",firefoxDrivers );
                    firefoxDriver = new FirefoxDriver(options);

                HashMap statusMap = new HashMap();

                Capabilities capabilities=((RemoteWebDriver) firefoxDriver).getCapabilities();

                String version=capabilities.getBrowserVersion();
                firefoxDriver.manage().window().setPosition(new Point(0, 0));
                firefoxDriver.manage().window().fullscreen();
                firefoxDriver.manage().window().setSize(new Dimension(1980, 1080));

                firefoxDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                firefoxDriver.get(url);
                Thread.sleep(10000);
                System.out.println(statusMap);
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
                log.info("Exception in Firefox"+e);
            }
            jsonObject.put("browser",jsonArray);
            jsonObject.put("error","true");
        }catch (Exception e) {

            jsonObject.put("error","false");
            log.info("Exception in cross-browser");
        }

        try{
            String value=accessScreenShot(metrics);
            System.out.println(value);
           String str= loadCrossBrowser(jsonObject);
        }catch (Exception e){
            log.info("Exception"+e);
        }
        return "All Execution Completed";
    }

    public String loadCrossBrowser(JSONObject jsonObject){
        try{
            log.info("inside LoadCross");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            RestTemplate restTemplate=new RestTemplate();
            JSONObject authObject=new JSONObject();
            authObject.put("password",password);
            authObject.put("username",cxauser);
            authObject.put("type","native");

            HttpEntity<JSONObject> entity = new HttpEntity<>(authObject, headers);
            String url=devUrl;

        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(  url+"/authapi/auth/token", HttpMethod.POST, entity, String.class);
            log.info("Response for ActiveMonitor ====== " + responseEntity.getBody());
            JSONParser jsonParser=new JSONParser();
            JSONObject authToken= (JSONObject) jsonParser.parse(responseEntity.getBody());
           try{

               HttpHeaders headersForReport = new HttpHeaders();
               headersForReport.setContentType(MediaType.APPLICATION_JSON);
               headersForReport.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
               headersForReport.setBearerAuth(authToken.get("auth_token").toString());
               HttpEntity<JSONObject> entityForReport = new HttpEntity<>(jsonObject, headersForReport);
            ResponseEntity<String> responseEntityForReport =
                    restTemplate.exchange(  url+"/test/reports/insertOmnichannel", HttpMethod.POST, entityForReport, String.class);
            log.info("Response for CrossBrowser ====== " + responseEntityForReport.getBody());
           }
           catch(Exception e){

               log.error("Exception"+e);
            }
            return "Execution Started";
        } catch (Exception ex) {
            log.error("Exception"+ex);
            return "error";
        }
        }catch (Exception e){
            log.info("Execption in load cross Browser");
        }
        return "Success";
    }

    public String StartActiveMonitor(JSONObject metrics){
        JSONObject jsonObject=new JSONObject();
        String url=metrics.get("url").toString();
        String executionId=metrics.get("executionId").toString();
        String region=metrics.get("region").toString();
        String machine=metrics.get("machine").toString();
        jsonObject.put("url",url);
        jsonObject.put("executionId",executionId);
        jsonObject.put("region",region);
        JSONArray jsonArray=new JSONArray();
        try {
            try {
                ChromeOptions options = new ChromeOptions();
                options. addArguments("--headless");
                options.addArguments("--remote-allow-origins=*");
                JSONObject browserName=new JSONObject();
                JSONObject browserObject = new JSONObject();
                System.setProperty("webdriver.chrome.driver",chromeDriver);
                WebDriver driver = new ChromeDriver(options);
                driver.manage().window().setPosition(new Point(0, 0));
                driver.manage().window().fullscreen();
                driver.manage().window().setSize(new Dimension(1980, 1080));


                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get(url);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                long loadEventEnd = (long) Double.valueOf(js.executeScript("return window.performance.timing.loadEventEnd;").toString()).doubleValue();
                long fetchStart = (long) Double.valueOf(js.executeScript("return window.performance.timing.fetchStart;").toString()).doubleValue();
                long totalLoadTime = loadEventEnd - fetchStart;
                String instant=Instant.now().toString();
                browserObject.put("timeStamp",instant);
                browserObject.put("loadTime", totalLoadTime);
                browserName.put("chrome",browserObject);
                jsonArray.add(browserName);
                driver.quit();
            } catch (Exception e) {
                log.info("Exception in Chrome" + e);
            }

            jsonObject.put("browser",jsonArray);
            jsonObject.put("error","true");
        }catch (Exception e){

            jsonObject.put("error","false");
            log.error("Exception"+e);
        }try{
            String str= loadActiveMonitor(jsonObject,machine);
        }catch (Exception e){
            log.error("Exception"+e);
        }
        return "All Execution Completed";
    }
    public String loadActiveMonitor(JSONObject jsonObject,String machine){
        try{
            log.info("inside LoadActive");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            RestTemplate restTemplate=new RestTemplate();
            JSONObject authObject=new JSONObject();
            authObject.put("password",password);
            authObject.put("username",cxauser);
            authObject.put("type","native");
            String url=devUrl;
            HttpEntity<JSONObject> entity = new HttpEntity<>(authObject, headers);

            try {
                ResponseEntity<String> responseEntity =
                        restTemplate.exchange(  url+"/authapi/auth/token", HttpMethod.POST, entity, String.class);
                log.info("Response for ActiveMonitor ====== " + responseEntity.getBody());
                JSONParser jsonParser=new JSONParser();
                JSONObject authToken= (JSONObject) jsonParser.parse(responseEntity.getBody());
                try{

                    HttpHeaders headersForReport = new HttpHeaders();
                    headersForReport.setContentType(MediaType.APPLICATION_JSON);
                    headersForReport.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    headersForReport.setBearerAuth(authToken.get("auth_token").toString());
                    HttpEntity<JSONObject> entityForReport = new HttpEntity<>(jsonObject, headersForReport);
                    ResponseEntity<String> responseEntityForReport =
                            restTemplate.exchange(  url+"/test/reports/insertActivemonitor", HttpMethod.POST, entityForReport, String.class);
                    log.info("Response for ActiveMonitor ====== " + responseEntityForReport.getBody());
                }
                catch(Exception e){

                    log.error("Exception"+e);
                }
                return "Execution Started";
            } catch (Exception ex) {
                log.error("Exception"+ex);
                return "error";
            }
        }catch (Exception e){
            log.info("Execption in load Active Monitor");
        }
        return "Success";
    }

    public String accessScreenShot(JSONObject jsonObject){

        return "sucess";
    }
}
