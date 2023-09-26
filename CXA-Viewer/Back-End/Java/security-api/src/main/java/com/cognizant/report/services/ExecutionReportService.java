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

import com.cognizant.report.models.SecurityMetricsData;
import com.cognizant.report.repos.SecurityMetricsRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class ExecutionReportService {

    @Autowired
    SecurityMetricsRepository securityMetricsRepository;



    public String generateExecutionId(JSONObject metrics) {
        UUID uniqueKey = UUID.randomUUID();
        String instant = Instant.now().toString();
        Boolean securityCheck = (Boolean) metrics.get("securityCheck");
        if (securityCheck) {
            SecurityMetricsData securityMetricsData = new SecurityMetricsData();
            securityMetricsData.setExecutionId(uniqueKey.toString());
            securityMetricsData.setStatus("InProgress");
            log.info("Setting the status as InProgress");
            securityMetricsData.setTimestamp(instant);
            securityMetricsData.setProjectId(metrics.get("projectId").toString());
            securityMetricsRepository.save(securityMetricsData);
        }
        return uniqueKey.toString();
    }

    public Boolean validatePerformanceUrl(String url) {
        try {
            Response res = Jsoup.connect(url).followRedirects(false).execute();
            log.info(" Validate Performance ");
            if (res.statusCode() >= 200 && res.statusCode() <= 399) {
                return true;
            } else {
                return false;
            }
        } catch (SSLException ssl) {
            log.info("exception"+ssl);
            return true;
        } catch (Exception ex) {
            log.info("exception"+ex);
            return false;
        }
    }

    public String saveSecurityData(JSONObject responseObj){
        String executionId = responseObj.get("executionId") != null ? responseObj.get("executionId").toString() : "";;
        SecurityMetricsData securityMetricsData = securityMetricsRepository.findByExecutionId(executionId);
        log.info("Responseobj ====  " + responseObj);
        if (securityMetricsData != null) {
            if(responseObj == null || responseObj.size() == 0 || ((Boolean) responseObj.get("error") && responseObj.get("errorMessage").toString().equals("Error in getting response headers"))){
                securityMetricsData.setTimestamp(Instant.now().toString());
                securityMetricsData.setUrl(responseObj.get("url").toString());
                securityMetricsData.setStatus("InComplete");
                securityMetricsData.setError(true);
                securityMetricsData.setErrorMessage("Scan Not Supported");
                securityMetricsRepository.save(securityMetricsData);
                return "Error in security";
            }

            log.info("Response from securityheaders" + responseObj.get("securityheaders"));
            String[] insecureCommunicationArray = {
                    "A05 - The application is accessible/ hosted only in HTTP. Hence, the application handles all the sensitive information including the login credentials in plain text",
                    "Easy",
                    "Information sent via HTTP can pose a threat to privacy.  Network traffic can be sniffed by an attacker who has access to a network interface, hence an attacker over a same network can sniff the credentials and able to access the application, MITM attacks are possible.",
                    "Critical",
                    "The application should be in HTTPS (HTTP -- Secure) implemented with HSTS security header. It is recommended that, the sensitive information should be encrypted with strong ciphers and TLS version 1.2 and above.",
                    "Insecure Communication not implemented"
            };
            String[] secureAttributeArray = {
                    "A05 - Cookie does not contain the \"secure\" attribute, it might also be sent to the site during an unencrypted session",
                    "Difficult",
                    "Any information such as cookies, session tokens or user credentials that are sent to the server as clear text, may be stolen and used later for identity theft or user impersonation",
                    "Low",
                    "Due to the sensitivity of encrypted requests, it is suggested to use HTTP POST (without parameters in the URL string) when possible, in order to avoid the disclosure of URLs and parameter values to others",
                    "All cookies use secure flag"
            };
            String[] httpOnlyArray = {
                    "A05 - The application failed to include HTTP only attribute.",
                    "Difficult",
                    "Using the HttpOnly flag when generating a cookie helps mitigate the risk of client side script accessing the protected cookie. the cookie cannot be accessed through client side script.",
                    "Low",
                    "HttpOnly flag is used to help prevent cross-site scripting, since it does not allow the cookie to be accessed via a client side script such as JavaScript.",
                    "All session cookies use httpOnly flag"};
            String[] bannerDisclosureArray = {
                    "A05 - The application shows the Server information like web server and its version",
                    "Difficult",
                    "Revealing Server information helps an adversary learn about the system and form a plan of attack",
                    "Low",
                    "1. Ensure that the server information is not being disclosed in the server responses." +
                            "2. Edit the web server's configuration file to disable the disclosure of this sensitive information.",
                    "Banner Disclosure not implemented"};
            String[] xFrameOptionsArray = {
                    "A05 - In the application, X-Frame-Options Header is missing",
                    "Difficult",
                    "It can be used to indicate whether or not a browser should be allowed to render a page inside a frame or iframe. Sites can use this to avoid clickjacking attacks, by ensuring that their content is not embedded into other sites",
                    "Low",
                    "Configure your web server to include an X-Frame-Options header. Consult Web references for more information about the possible values for this header.",
                    "X-FRAME Options not implemented"};
            String[] contentSecurityPolicyHeaderArray = {
                    "A05 - In the application, the Content Security Policy Header is missing.",
                    "Difficult",
                    "Attacker can force the browser to load malicious third party resources",
                    "Low",
                    "Configure your server to send the Content-Security-Policy header",
                    "Content security policy header not implemented"
            };
            String[] xContentTypeOptionsArray = {
                    "A05 - The X-Content-Type-Options header (with nosniff value) prevents IE from ignoring the content-type of a response",
                    "Difficult",
                    "This action may prevent untrusted content (e.g. user uploaded content) from being executed on the user browser",
                    "Low",
                    "Configure your server to send the \"X-Content-Type-Options\" header with value \"nosniff\" on all outgoing requests",
                    "X-Content-Type-Options header not implemented"
            };
            String[] xXssProtectionHeaderArray = {
                    "A05 - The HTTP X-XSS-Protection response header is a feature of Internet Explorer, Chrome and Safari that stops pages from loading when they detect reflected cross-site scripting (XSS) attacks.",
                    "Difficult",
                    "Based on browser XSS protection, the end user is prone to XSS attacks leading loss of data.",
                    "Low",
                    "It is recommended to configure the server to send the \"X-XSS-Protection\" header in the response. Apply the fix throughout the application.\n" +
                            "For Example, X-XSS-Protection: 1; mode=block ",
                    "X-XSS protection header not implemented"
            };
            String[] hstsHeaderArray = {
                    "A05 - HTTP Strict Transport Security (HSTS) is a mechanism, which protects secure (HTTPS) websites from being downgraded to non-secure HTTP and cookie hijacking. This mechanism enables web servers to instruct their clients (web browsers or other user agents) to use secure HTTPS connections when interacting with the server, and never use the insecure HTTP protocol.",
                    "Difficult",
                    "HSTS automatically redirects HTTP requests to HTTPS for the target domain. A man-in-the-middle attacker attempts to intercept traffic from a victim user using an invalid certificate and hopes the user will accept the bad certificate",
                    "Low",
                    "It is recommended to include HTTP Strict Transport Security Header for the entire application." +
                            "For example, " +
                            "Strict-Transport-Security: max-age=86400." +
                            "Do not use * wildcard nor blindly return the Origin header content without any checks",
                    "HTTP Strict Transport Security Header not implemented"
            };
            String[] browserCacheWeaknessArray = {
                    "A05 - Browsers can store information for purposes of caching and history. If sensitive information is displayed to the user (such as their address, credit card details, Social Security Number, or username), then this information could be stored for purposes of caching or history, and therefore retrievable through examining the browser's cache or by simply pressing the browser's Back button",
                    "Difficult",
                    "Application does not use an appropriate browser caching policy that specifies the extent to which each web page and associated form fields should be cached and re-viewable",
                    "Low",
                    "It is recommended to, Delivering the page over HTTPS. Cache-Control: no-cache, no-store, must-revalidate Expires: 0 Pragma: no-cache",
                    "Browser cache weakness not implemented"
            };

            String data = "";
            ArrayList<JSONObject> securityChecks = new ArrayList<>();
            URL appUrl = null;


            JSONObject insecureCommunicationCheck = new JSONObject();
            JSONObject secureAttributeCheck = new JSONObject();
            JSONObject httpOnlyCheck = new JSONObject();
            JSONObject bannerDisclosureCheck = new JSONObject();
            JSONObject xFrameOptionsCheck = new JSONObject();
            JSONObject contentSecurityPolicyCheck = new JSONObject();
            JSONObject x_Content_Type_OptionsCheck = new JSONObject();
            JSONObject x_XSS_ProtectionCheck = new JSONObject();
            JSONObject httpStrictTransportSecurityCheck = new JSONObject();
            JSONObject browserCacheWeaknessCheck = new JSONObject();
            long totalPSCount = 0;
            long passCount = 0;
            long failCount = 0;

            Map<String, JSONObject> checksList = new HashMap<>();
            checksList.put("Insecure Communication", insecureCommunicationCheck);
            checksList.put("Secure Attribute", secureAttributeCheck);
            checksList.put("Http Only", httpOnlyCheck);
            checksList.put("Banner Disclosure", bannerDisclosureCheck);
            checksList.put("X-Frame Options", xFrameOptionsCheck);
            checksList.put("Content Security Policy", contentSecurityPolicyCheck);
            checksList.put("X-Content-Type-Options", x_Content_Type_OptionsCheck);
            checksList.put("X-XSS-Protection", x_XSS_ProtectionCheck);
            checksList.put("Http Strict Transport Security", httpStrictTransportSecurityCheck);
            checksList.put("Browser Cache Weakness", browserCacheWeaknessCheck);
            Set<String> set = new HashSet<>();
            Map<String, String[]> arrays = new HashMap<>();


            arrays.put("Insecure Communication", insecureCommunicationArray);
            arrays.put("Secure Attribute", secureAttributeArray);
            arrays.put("Http Only", httpOnlyArray);
            arrays.put("Banner Disclosure", bannerDisclosureArray);
            arrays.put("X-Frame Options", xFrameOptionsArray);
            arrays.put("Content Security Policy", contentSecurityPolicyHeaderArray);
            arrays.put("X-Content-Type-Options", xContentTypeOptionsArray);
            arrays.put("X-XSS-Protection", xXssProtectionHeaderArray);
            arrays.put("Http Strict Transport Security", hstsHeaderArray);
            arrays.put("Browser Cache Weakness", browserCacheWeaknessArray);
            boolean set_cookie = false;

            String words[] = null;
            JSONParser parser = new JSONParser();
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setReadTimeout(20000);
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            JSONObject jsonObject = new JSONObject();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);

            String instant = Instant.now().toString();
            org.json.simple.JSONObject passiveScan = new org.json.simple.JSONObject();
            org.json.simple.JSONObject jsVulnerability = new org.json.simple.JSONObject();
            JSONObject javaScriptVulnerable = new JSONObject();
            ArrayList<String> jsurls = new ArrayList<String>();
            if(securityMetricsData != null) {
                try {
                    log.info("Response for securityapi from lighthouse  ===== " + responseObj);
                    int totalVulnerability = 0;
                    String severityLevel = "";
                    long vulnCount = 0;
                    String library = "";
                    String urlJS = "";
                    int high = 0;
                    int medium = 0;
                    int low = 0;
                    String displayValue = "";
                    org.json.simple.JSONArray security = new org.json.simple.JSONArray();
                    org.json.simple.JSONObject severity = new org.json.simple.JSONObject();
                    if (responseObj.containsKey("details")) {
                        org.json.simple.JSONObject details = (org.json.simple.JSONObject) responseObj.get("details");
                        if (responseObj.containsKey("details")) {
                            org.json.simple.JSONArray items = (org.json.simple.JSONArray) details.get("items");
                            for (Object item : items) {
                                org.json.simple.JSONObject itemTemp = (org.json.simple.JSONObject) item;
                                vulnCount = (long) itemTemp.get("vulnCount");
                                totalVulnerability = totalVulnerability + (int) vulnCount;
                                severityLevel = itemTemp.get("highestSeverity").toString();
                                JSONObject detectedLib = (JSONObject) itemTemp.get("detectedLib");
                                log.info("DetectedLib ==  " + itemTemp.get("detectedLib"));

                                library = detectedLib.get("text").toString();
                                urlJS = detectedLib.get("url").toString();
                                org.json.simple.JSONObject securityJson = new org.json.simple.JSONObject();
                                switch (severityLevel) {
                                    case "High": {
                                        high = high + (int) vulnCount;
                                        break;
                                    }
                                    case "Medium": {
                                        medium = medium + (int) vulnCount;
                                        break;
                                    }
                                    case "Low": {
                                        low = low + (int) vulnCount;
                                        break;
                                    }
                                }
                                securityJson.put("jsLibrary", library);
                                securityJson.put("severity", severityLevel);
                                securityJson.put("issueNo", vulnCount);
                                securityJson.put("url", urlJS);
                                security.add(securityJson);
                                jsurls.add(urlJS);
                            }
                        }
                    }
                    String jsStatus = "";
                    if (jsurls.size() > 0) {
                        jsStatus = "FAIL";
                    } else {
                        jsStatus = "PASS";
                    }
                    severity.put("high", high);
                    severity.put("medium", medium);
                    severity.put("low", low);
                    securityMetricsData.setTimestamp(instant);
                    securityMetricsData.setUrl(responseObj.get("url").toString());
                    securityMetricsData.setRegion(responseObj.get("region").toString());
                    securityMetricsData.setStatus("Completed");

                    securityMetricsData.setError(false);
                    if((Boolean)responseObj.get("error") && responseObj.get("errorMessage").toString().equals("Error in generating data from lighthouse")) {
                        securityMetricsData.setTimestamp(instant);
                        securityMetricsData.setUrl(responseObj.get("url").toString());
                        securityMetricsRepository.save(securityMetricsData);
                    }
                    securityMetricsData.setJsVulnerability(jsVulnerability);
                    javaScriptVulnerable.put("Vulnerability", "Vulnerable and Outdated JS Libraries");
                    javaScriptVulnerable.put("Description", "A06 - Older version of Java Script libraries used by the application");
                    javaScriptVulnerable.put("Exploitable", "Difficult");
                    javaScriptVulnerable.put("Impact", "");
                    javaScriptVulnerable.put("Reason", "");
                    javaScriptVulnerable.put("Status", jsStatus);
                    javaScriptVulnerable.put("Severity", "Low");
                    javaScriptVulnerable.put("Recommendation", "");
                    javaScriptVulnerable.put("Guidelines", "Vulnerable and Outdated Components");
                    javaScriptVulnerable.put("Url", jsurls);

                    try {

                        appUrl = new URL(responseObj.get("url").toString());
                        log.info(" ******  appUrl   " + appUrl);
                        if (appUrl.getProtocol().equals("http")) {
                            insecureCommunicationCheck.put("Vulnerability", "Insecure Communication");
                            insecureCommunicationCheck.put("Reason", insecureCommunicationArray[5]);
                            insecureCommunicationCheck.put("Status", "FAIL");
                            totalPSCount++;
                            failCount++;
                        } else {
                            insecureCommunicationCheck.put("Vulnerability", "Insecure Communication");
                            insecureCommunicationCheck.put("Reason", insecureCommunicationArray[5]);
                            insecureCommunicationCheck.put("Status", "PASS");
                            totalPSCount++;
                            passCount++;
                        }
                        insecureCommunicationCheck.put("Description", insecureCommunicationArray[0]);
                        insecureCommunicationCheck.put("Exploitable", insecureCommunicationArray[1]);
                        insecureCommunicationCheck.put("Impact", insecureCommunicationArray[2]);
                        insecureCommunicationCheck.put("Severity", insecureCommunicationArray[3]);
                        insecureCommunicationCheck.put("Recommendation", insecureCommunicationArray[4]);
                        insecureCommunicationCheck.put("Guidelines", "Security Misconfiguration");
                        set.add("Insecure Communication");
                        securityChecks.add(insecureCommunicationCheck);


                        data = responseObj.get("securityheaders").toString();
                        log.info("***** data : ");

                        words = data.split(":");


                        for (String word : words) {
                            if (word.contains("set-cookie:") || word.contains("Set-Cookie:") || word.contains("SET-COOKIE:") ||
                                    word.contains("set cookie:") || word.contains("Set Cookie") || word.contains("SET COOKIE")) {
                                set_cookie = true;
                            }
                            if (!set.contains("Http Strict Transport Security") && word.contains("Strict-Transport-Security") ||
                                    word.contains("strict-transport-security") || word.contains("stricttransportsecurity") ||
                                    word.contains("StrictTransportSecurity")) {
                                httpStrictTransportSecurityCheck.put("Vulnerability", "HTTP Strict Transport Security");
                                httpStrictTransportSecurityCheck.put("Description", hstsHeaderArray[0]);
                                httpStrictTransportSecurityCheck.put("Exploitable", hstsHeaderArray[1]);
                                httpStrictTransportSecurityCheck.put("Impact", hstsHeaderArray[2]);
                                httpStrictTransportSecurityCheck.put("Reason", hstsHeaderArray[5]);
                                httpStrictTransportSecurityCheck.put("Status", "PASS");
                                httpStrictTransportSecurityCheck.put("Severity", hstsHeaderArray[3]);
                                httpStrictTransportSecurityCheck.put("Recommendation", hstsHeaderArray[4]);
                                httpStrictTransportSecurityCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(httpStrictTransportSecurityCheck);
                                set.add("Http Strict Transport Security");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("Browser Cache Weakness") && (word.contains("Cache-Control") ||
                                    word.contains("Cachecontrol") || word.contains("cachecontrol") ||
                                    word.contains("cache-control"))) {
                                browserCacheWeaknessCheck.put("Vulnerability", "Browser Cache Weakness");
                                browserCacheWeaknessCheck.put("Description", browserCacheWeaknessArray[0]);
                                browserCacheWeaknessCheck.put("Exploitable", browserCacheWeaknessArray[1]);
                                browserCacheWeaknessCheck.put("Impact", browserCacheWeaknessArray[2]);
                                browserCacheWeaknessCheck.put("Reason", browserCacheWeaknessArray[5]);
                                browserCacheWeaknessCheck.put("Status", "PASS");
                                browserCacheWeaknessCheck.put("Severity", browserCacheWeaknessArray[3]);
                                browserCacheWeaknessCheck.put("Recommendation", browserCacheWeaknessArray[4]);
                                browserCacheWeaknessCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(browserCacheWeaknessCheck);
                                set.add("Browser Cache Weakness");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("Banner Disclosure") && (word.contains("Server:") ||
                                    word.contains("server:") || word.contains("SERVER:"))) {
                                bannerDisclosureCheck.put("Vulnerability", "Banner Disclosure");
                                bannerDisclosureCheck.put("Description", bannerDisclosureArray[0]);
                                bannerDisclosureCheck.put("Exploitable", bannerDisclosureArray[1]);
                                bannerDisclosureCheck.put("Impact", bannerDisclosureArray[2]);
                                bannerDisclosureCheck.put("Reason", bannerDisclosureArray[5]);
                                bannerDisclosureCheck.put("Status", "FAIL");
                                bannerDisclosureCheck.put("Severity", bannerDisclosureArray[3]);
                                bannerDisclosureCheck.put("Recommendation", bannerDisclosureArray[4]);
                                bannerDisclosureCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(bannerDisclosureCheck);
                                set.add("Banner Disclosure");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("X-XSS-Protection") && (word.contains("X-XSS-Protection") || word.contains("xxssprotection") ||
                                    word.contains("x-xss-protection") || word.contains("XXSSPROTECTION"))) {
                                x_XSS_ProtectionCheck.put("Vulnerability", "X-XSS-Protection");
                                x_XSS_ProtectionCheck.put("Description", xXssProtectionHeaderArray[0]);
                                x_XSS_ProtectionCheck.put("Exploitable", xXssProtectionHeaderArray[1]);
                                x_XSS_ProtectionCheck.put("Impact", xXssProtectionHeaderArray[2]);
                                x_XSS_ProtectionCheck.put("Reason", xXssProtectionHeaderArray[5]);
                                x_XSS_ProtectionCheck.put("Status", "PASS");
                                x_XSS_ProtectionCheck.put("Severity", xXssProtectionHeaderArray[3]);
                                x_XSS_ProtectionCheck.put("Recommendation", xXssProtectionHeaderArray[4]);
                                x_XSS_ProtectionCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(x_XSS_ProtectionCheck);
                                set.add("X-XSS-Protection");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("X-Frame Options") && (word.contains("X-Frame-Options") || word.contains("xframeoptions") ||
                                    word.contains("x-frame-options") || word.contains("XFRAMEOPTIONS"))) {
                                xFrameOptionsCheck.put("Vulnerability", "X-Frame Options");
                                xFrameOptionsCheck.put("Description", xFrameOptionsArray[0]);
                                xFrameOptionsCheck.put("Exploitable", xFrameOptionsArray[1]);
                                xFrameOptionsCheck.put("Impact", xFrameOptionsArray[2]);
                                xFrameOptionsCheck.put("Reason", xFrameOptionsArray[5]);
                                xFrameOptionsCheck.put("Status", "PASS");
                                xFrameOptionsCheck.put("Severity", xFrameOptionsArray[3]);
                                xFrameOptionsCheck.put("Recommendation", xFrameOptionsArray[4]);
                                xFrameOptionsCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(xFrameOptionsCheck);
                                set.add("X-Frame Options");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("X-Content-Type-Options") && (word.contains("X-Content-Type-Options") ||
                                    word.contains("xcontenttypeoptions") || word.contains("XCONTENTTYPEOPTIONS") ||
                                    word.contains("x-content-type-options"))) {
                                x_Content_Type_OptionsCheck.put("Vulnerability", "X-Content-Type-Options");
                                x_Content_Type_OptionsCheck.put("Description", xContentTypeOptionsArray[0]);
                                x_Content_Type_OptionsCheck.put("Exploitable", xContentTypeOptionsArray[1]);
                                x_Content_Type_OptionsCheck.put("Impact", xContentTypeOptionsArray[2]);
                                x_Content_Type_OptionsCheck.put("Reason", xContentTypeOptionsArray[5]);
                                x_Content_Type_OptionsCheck.put("Status", "PASS");
                                x_Content_Type_OptionsCheck.put("Severity", xContentTypeOptionsArray[3]);
                                x_Content_Type_OptionsCheck.put("Recommendation", xContentTypeOptionsArray[4]);
                                x_Content_Type_OptionsCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(x_Content_Type_OptionsCheck);
                                set.add("X-Content-Type-Options");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("Http Only") && (word.startsWith("HttpOnly") || word.startsWith("httponly"))) {
                                httpOnlyCheck.put("Vulnerability", "Http Only");
                                httpOnlyCheck.put("Description", httpOnlyArray[0]);
                                httpOnlyCheck.put("Exploitable", httpOnlyArray[1]);
                                httpOnlyCheck.put("Impact", httpOnlyArray[2]);
                                httpOnlyCheck.put("Reason", httpOnlyArray[5]);
                                httpOnlyCheck.put("Status", "PASS");
                                httpOnlyCheck.put("Severity", httpOnlyArray[3]);
                                httpOnlyCheck.put("Recommendation", httpOnlyArray[4]);
                                httpOnlyCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(httpOnlyCheck);
                                set.add("Http Only");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("Content Security Policy") && (word.contains("Content-Security-Policy") ||
                                    word.contains("content-security-policy") || word.contains("contentsecuritypolicy") ||
                                    word.contains("ContentSecurityPolicy"))) {
                                contentSecurityPolicyCheck.put("Vulnerability", "Content Security Policy");
                                contentSecurityPolicyCheck.put("Description", contentSecurityPolicyHeaderArray[0]);
                                contentSecurityPolicyCheck.put("Exploitable", contentSecurityPolicyHeaderArray[1]);
                                contentSecurityPolicyCheck.put("Impact", contentSecurityPolicyHeaderArray[2]);
                                contentSecurityPolicyCheck.put("Reason", contentSecurityPolicyHeaderArray[5]);
                                contentSecurityPolicyCheck.put("Status", "PASS");
                                contentSecurityPolicyCheck.put("Severity", contentSecurityPolicyHeaderArray[3]);
                                contentSecurityPolicyCheck.put("Recommendation", contentSecurityPolicyHeaderArray[4]);
                                contentSecurityPolicyCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(contentSecurityPolicyCheck);
                                set.add("Content Security Policy");
                                totalPSCount++;
                                passCount++;
                            }
                            if (!set.contains("Secure Attribute") && (word.startsWith("Secure") || word.startsWith("SECURE") || word.startsWith("secure"))) {
                                secureAttributeCheck.put("Vulnerability", "Secure Attribute");
                                secureAttributeCheck.put("Description", secureAttributeArray[0]);
                                secureAttributeCheck.put("Exploitable", secureAttributeArray[1]);
                                secureAttributeCheck.put("Impact", secureAttributeArray[2]);

                                secureAttributeCheck.put("Reason", secureAttributeArray[5]);
                                secureAttributeCheck.put("Status", "PASS");
                                secureAttributeCheck.put("Severity", secureAttributeArray[3]);
                                secureAttributeCheck.put("Recommendation", contentSecurityPolicyHeaderArray[4]);
                                secureAttributeCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(secureAttributeCheck);
                                set.add("Secure Attribute");
                                totalPSCount++;
                                passCount++;
                            }
                        }

                        for (String key : checksList.keySet()) {
                            if (!set.contains(key)) {
                                String[] array = arrays.get(key);
                                JSONObject failedCheck = checksList.get(key);
                                failedCheck.put("Vulnerability", key);
                                failedCheck.put("Description", array[0]);
                                failedCheck.put("Exploitable", array[1]);
                                failedCheck.put("Impact", array[2]);
                                failedCheck.put("Reason", array[5]);
                                totalPSCount++;

                                if (key.equals("Banner Disclosure")) {
                                    failedCheck.put("Status", "PASS");
                                    passCount++;
                                } else {
                                    failedCheck.put("Status", "FAIL");
                                    failCount++;
                                }
                                failedCheck.put("Severity", array[3]);
                                failedCheck.put("Recommendation", array[4]);
                                //log.info(key);
                                failedCheck.put("Guidelines", "Security Misconfiguration");
                                securityChecks.add(failedCheck);
                            }
                        }

                        log.info("done");
                    } catch (Exception e) {
                        log.info("Exception for security ====="+e);
                        securityMetricsData.setTimestamp(instant);
                        securityMetricsData.setUrl(responseObj.get("url").toString());
                        securityMetricsData.setStatus("InComplete");
                        securityMetricsData.setError(true);
                        securityMetricsData.setErrorMessage("Scan Not Supported");
                        securityMetricsRepository.save(securityMetricsData);
                        return "error in securityheaders";
                    }
                    securityChecks.add(javaScriptVulnerable);
                    passiveScan.put("securityChecks", securityChecks);
                    passiveScan.put("totalPSCount", totalPSCount);
                    passiveScan.put("passCount", passCount);
                    passiveScan.put("failCount", failCount);
                    securityMetricsData.setPassiveScan(passiveScan);
                    securityMetricsRepository.save(securityMetricsData);
                    log.info(" Security Response Data");
                    return "Security Data added to DB";
                } catch (Exception ex) {
                    log.error("Error ", ex);
                    log.info("Response for security contains error");
                    securityMetricsData.setTimestamp(instant);
                    securityMetricsData.setUrl(responseObj.get("url").toString());
                    securityMetricsData.setStatus("InComplete");
                    securityMetricsData.setErrorMessage("Scan Not Supported");
                    securityMetricsData.setError(true);
                    securityMetricsRepository.save(securityMetricsData);
                }
            }
        }

        return "security data added to DB";
    }

    public SecurityMetricsData getSecurity(String id) {
        SecurityMetricsData securityMetrics = securityMetricsRepository.findTopByProjectIdOrderByTimestampDesc(id);
        return securityMetrics;
    }


    public JSONArray getSecurityOwasp(String executionId) {
        SecurityMetricsData securityMetrics = securityMetricsRepository.findByExecutionId(executionId);

        JSONArray owaspGuidelines = new JSONArray();
        if (securityMetrics != null) {
            JSONObject passiceScan = securityMetrics.getPassiveScan();
            ArrayList securityChecks = (ArrayList) passiceScan.get("securityChecks");
            int brokenAccessControl = 0;
            int cryptographicFailures = 0;
            int injection = 0;
            int insecureDesign = 0;
            int securityMisconfiguration = 0;
            int vulnerableOutdated = 0;
            int identificationAuthentication = 0;
            int softwareDataIntegrity = 0;
            int securityLogMonitor = 0;
            int ssrf = 0;

            int smPassCount = 0;
            int smFailCount = 0;
            int voPassCount = 0;
            int voFailCount = 0;
            for (Object check : securityChecks) {
                HashMap checks = (HashMap) check;
                String guidelines = checks.get("Guidelines").toString();
                String status = checks.get("Status").toString();
                if (guidelines != null) {
                    switch (guidelines) {
                        case "Broken Access Control": {
                            brokenAccessControl++;
                            break;
                        }
                        case "Cryptographic Failures": {
                            cryptographicFailures++;
                            break;
                        }
                        case "Injection": {
                            injection++;
                            break;
                        }
                        case "Insecure Design": {
                            insecureDesign++;
                            break;
                        }
                        case "Security Misconfiguration": {
                            securityMisconfiguration++;
                            if (status.equals("PASS")) {
                                smPassCount++;
                            } else {
                                smFailCount++;
                            }
                            break;
                        }
                        case "Vulnerable and Outdated Components": {
                            vulnerableOutdated++;
                            if (status.equals("PASS")) {
                                voPassCount++;
                            } else {
                                voFailCount++;
                            }
                            break;
                        }
                        case "Identification and Authentication": {
                            identificationAuthentication++;
                            break;
                        }
                        case "Software and Data Integrity": {
                            softwareDataIntegrity++;
                            break;
                        }
                        case "Security Logging and Monitoring": {
                            securityLogMonitor++;
                            break;
                        }
                        case "Server Side Request Forgery": {
                            ssrf++;
                            break;
                        }
                    }
                }
            }

            JSONObject brokenAccessControlJsonObject = new JSONObject();
            JSONObject cryptographicFailuresJsonObject = new JSONObject();
            JSONObject injectionJsonObject = new JSONObject();
            JSONObject insecureDesignJsonObject = new JSONObject();
            JSONObject securityMisconfigurationJsonObject = new JSONObject();
            JSONObject vulnerableOutdatedJsonObject = new JSONObject();
            JSONObject identificationAuthenticationJsonObject = new JSONObject();
            JSONObject softwareDataIntegrityJsonObject = new JSONObject();
            JSONObject securityLogMonitorJsonObject = new JSONObject();
            JSONObject ssrfJsonObject = new JSONObject();

            //ArrayList<JSONObject> jsonObjects = null;
            brokenAccessControlJsonObject.put("id", "A01");
            brokenAccessControlJsonObject.put("name", "Broken Access Control");
            brokenAccessControlJsonObject.put("value", brokenAccessControl);

            cryptographicFailuresJsonObject.put("id", "A02");
            cryptographicFailuresJsonObject.put("name", "Cryptographic Failures");
            cryptographicFailuresJsonObject.put("value", cryptographicFailures);

            injectionJsonObject.put("id", "A03");
            injectionJsonObject.put("name", "Injection");
            injectionJsonObject.put("value", injection);


            insecureDesignJsonObject.put("id", "A04");
            insecureDesignJsonObject.put("name", "Insecure Design");
            insecureDesignJsonObject.put("value", insecureDesign);

            securityMisconfigurationJsonObject.put("id", "A05");
            securityMisconfigurationJsonObject.put("name", "Security Misconfiguration");
            securityMisconfigurationJsonObject.put("value", securityMisconfiguration);
            securityMisconfigurationJsonObject.put("pass", smPassCount);
            securityMisconfigurationJsonObject.put("fail", smFailCount);

            vulnerableOutdatedJsonObject.put("id", "A06");
            vulnerableOutdatedJsonObject.put("name", "Vulnerable and Outdated Components");
            vulnerableOutdatedJsonObject.put("value", vulnerableOutdated);
            vulnerableOutdatedJsonObject.put("pass", voPassCount);
            vulnerableOutdatedJsonObject.put("fail", voFailCount);

            identificationAuthenticationJsonObject.put("id", "A07");
            identificationAuthenticationJsonObject.put("name", "Identification and Authentication");
            identificationAuthenticationJsonObject.put("value", identificationAuthentication);


            softwareDataIntegrityJsonObject.put("id", "A08");
            softwareDataIntegrityJsonObject.put("name", "Software and Data Integrity");
            softwareDataIntegrityJsonObject.put("value", softwareDataIntegrity);


            securityLogMonitorJsonObject.put("id", "A09");
            securityLogMonitorJsonObject.put("name", "Security Logging and Monitoring");
            securityLogMonitorJsonObject.put("value", securityLogMonitor);


            ssrfJsonObject.put("id", "A10");
            ssrfJsonObject.put("name", "Server Side Request Forgery");
            ssrfJsonObject.put("value", ssrf);

            owaspGuidelines.add(brokenAccessControlJsonObject);
            owaspGuidelines.add(cryptographicFailuresJsonObject);
            owaspGuidelines.add(injectionJsonObject);
            owaspGuidelines.add(insecureDesignJsonObject);
            owaspGuidelines.add(securityMisconfigurationJsonObject);
            owaspGuidelines.add(vulnerableOutdatedJsonObject);
            owaspGuidelines.add(identificationAuthenticationJsonObject);
            owaspGuidelines.add(softwareDataIntegrityJsonObject);
            owaspGuidelines.add(securityLogMonitorJsonObject);
            owaspGuidelines.add(ssrfJsonObject);
        }
        return owaspGuidelines;
    }

}
