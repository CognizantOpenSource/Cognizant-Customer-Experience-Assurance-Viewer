var express = require('express');
var router = express.Router();
var config = require('../configuration.json');
let validationRoute = require('./validation.js');

var Request = require("request");
const fs = require('fs');
const { type } = require('os');


const lighthouse = require("lighthouse");
const chromeLauncher = require("chrome-launcher");
const validation = require('./validation.js');

router.post('/startLighthouseServices', (req, res) => {
    console.log("---startLighthouseServices----");
    console.log(req.body);
    let url = req.body.url;
    let executionId = req.body.executionId;
    let region = req.body.region;
    let modules = req.body.modules;
    let machine = req.body.machine;
    res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    if (url === "" || url === undefined || url === null || !validation(url)) {
        return res.status(500).json("Please Provide valid URL");
    }
    if (region === "" || region === undefined || region === null) {
        return res.status(500).json("Please Provide valid region");
    }
    if (machine === "" || machine === undefined || machine === null) {
        return res.status(500).json("Please Provide valid Machine Details");
    }
    if (modules === "" || modules === undefined || modules === null || modules.length < 1) {
        return res.status(500).json("Please Provide valid modules");
    }
    let authAPIUrl = "";
    let reportApiUrl = "";
    if (machine === "Dev") {
        authAPIUrl = config.devAuthAPIUrl;
        reportApiUrl = config.devReportAPIUrl;
    } else {
        authAPIUrl = config.demoAuthAPIUrl;
        reportApiUrl = config.demoReportAPIUrl;
    }
    let omniChannelRemoteUrl=config.omniChannelRemoteUrl;
    let AuthJsonObj = {
        "username": config.username,
        "password": config.password,
        "type": "native"
    }
    setTimeout(() => executeLighthouse(url, executionId, region, authAPIUrl, reportApiUrl, AuthJsonObj, modules,omniChannelRemoteUrl), 0);
    res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    return res.status(200).json(" Started Execution");
})


async function executeLighthouse(url, executionId, region, authAPIUrl, reportApiUrl, AuthJsonObj, modules,omniChannelRemoteUrl) {
    let resultJsonObj = {};
    let chrome; count = 0;
    for (let module of modules) {
        switch (module) {
            case "performances": {
                console.log(" Performances : ");
                try {
                    chrome = await chromeLauncher.launch({ chromeFlags: ['--headless'] });
                    const config = {
                        extends: 'lighthouse:default',
                        settings: {
                            onlyAudits: [
                                'first-contentful-paint',
                                'largest-contentful-paint',
                                'speed-index',
                                'interactive',
                                'total-blocking-time',
                                'server-response-time',
                                'cumulative-layout-shift',
                                'screenshot-thumbnails'
                            ],
                            onlyCategories: ['performance'],
                            formFactor: 'desktop',
                            throttling: {
                                rttMs: 40,
                                throughputKbps: 10240,
                                requestLatencyMs: 0,
                                downloadThroughputKbps: 0,
                                uploadThroughputKbps: 0,
                                cpuSlowdownMultiplier: 1
                            },
                            throttlingMethod: "provided",
                            screenEmulation: {
                                mobile: false,
                                width: 1350,
                                height: 940,
                                deviceScaleFactor: 1,
                                disabled: false,
                            }
                        }
                    }
                    const opts = {
                        chromeFlags: ['--headless', '--disable-gpu'],
                        //chromeFlags: ['--headless', '--no-sandbox', '--disable-gpu', '--disable-setuid-sandbox', '--disable-dev-shm-usage'],
                        port: chrome.port
                    }
                    let results = await lighthouse(url, opts, config);

                    if (results === undefined || results === null || results === "") {
                        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                        return res.status(500).json("Error in getting Performance Metrics");
                    }
                    console.log(results.lhr.audits);
                    resultJsonObj = {
                        "serverResponseTime": Math.trunc(Number(results.lhr.audits["server-response-time"].numericValue)),
                        "firstContentfulPaint": results.lhr.audits["first-contentful-paint"].displayValue,
                        "largestContentfulPaint": results.lhr.audits["largest-contentful-paint"].displayValue,
                        "speedIndex": results.lhr.audits["speed-index"].displayValue,
                        "interactive": results.lhr.audits["interactive"].displayValue,
                        "totalBlockingTime": results.lhr.audits["total-blocking-time"].displayValue,
                        "cumulativeLayoutShift": results.lhr.audits["cumulative-layout-shift"].displayValue,
                        "executionId": executionId,
                        "screenshot": results.lhr.audits["screenshot-thumbnails"].details.items,
                        "performanceURL": url,
                        "region": region,
                        "error": false
                    }
                  //  console.log(" result json object in performances");
                //console.log(resultJsonObj);
                    const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertPerformance", resultJsonObj);
                    //const omniChannel=await generatePostAPI(authAPIUrl, AuthJsonObj, omniChannelRemoteUrl, "crossBrowser", resultJsonObj);
                       console.log(postResponse);
                    //await chrome.kill();   
                } catch (error) {
                    console.log(" Error in Performances : " + error);
                    //await chrome.kill();     
                    let resultJson = {
                        "executionId": executionId,
                        "performanceURL": url,
                        "region": region,
                        "error": true
                    }
                    
                  
                    await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertPerformance", resultJson);
                   // const omniChannel=await generatePostAPI(authAPIUrl, AuthJsonObj, omniChannelRemoteUrl, "crossBrowser", resultJson);
                }
                
                
                break;
            }
            case "accessibility": {
                try {
                    console.log("Accessibility : ");
                    chrome = await chromeLauncher.launch({ chromeFlags: ['--headless'] });
                    const config = {
                        extends: 'lighthouse:default',
                        settings: {
                            onlyCategories: ['accessibility'],
                            formFactor: 'desktop',
                            throttling: {
                                method: "provided"
                            },
                            screenEmulation: {
                                mobile: false,
                                width: 1350,
                                height: 940,
                                deviceScaleFactor: 1,
                                disabled: false,
                            }
                        }
                    }
                    const opts = {
                        chromeFlags: ['--headless', '--disable-gpu'],
                        port: chrome.port
                    }
                    let results = await lighthouse(url, opts, config);
                    console.log(results);
                    if (results === undefined || results === null || results === "") {
    res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                        return res.status(500).json("Error in getting Acessibility Metrics");
                    }
                    let jsonTemp = results.lhr.audits;
                    //console.log(jsonTemp);
                    delete jsonTemp["full-page-screenshot"];
                    console.log(" result json object in accessibility");
                    console.log(jsonTemp);

                    let resultJson = {
                        "executionId": executionId,
                        "url": url,
                        "region": region,
                        "audits": jsonTemp,
                        "error": false
                    }
                    const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertAccessibility", resultJson);
                    console.log(postResponse);
                    //await chrome.kill();
                } catch (error) {
                    console.log(" Error in Accessibility : " + error);
                    //await chrome.kill();
                    let resultJson = {
                        "executionId": executionId,
                        "url": url,
                        "region": region,
                        "error": true
                    }
                    await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertAccessibility", resultJson);
                }
                break;
            }
            case "security": {
                console.log("Security : ");
                try {
                    chrome = await chromeLauncher.launch({ chromeFlags: ['--headless'] });
                    const config = {
                        extends: 'lighthouse:default',
                        settings: {
                            onlyCategories: ['best-practices'],
                            formFactor: 'desktop',
                            throttling: {
                                method: "provided"
                            },
                            screenEmulation: {
                                mobile: false,
                                width: 1350,
                                height: 940,
                                deviceScaleFactor: 1,
                                disabled: false,
                            }
                        }
                    }
                    const opts = {
                        chromeFlags: ['--headless', '--disable-gpu'],
                        //chromeFlags: ['--headless', '--no-sandbox', '--disable-gpu', '--disable-setuid-sandbox', '--disable-dev-shm-usage'],
                        port: chrome.port
                    }
                    let results = await lighthouse(url, opts, config);
                    //console.log(results);
                    if (results === undefined || results === null || results === "") {
                        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                        return res.status(500).json("Error in getting Security Metrics");
                    }
                    let jsonTemp = results.lhr.audits;

                    console.log(" result json object in security");
                    //console.log(jsonTemp["no-vulnerable-libraries"]);

                    let response=await generateSecurityResponse(url,executionId,region,jsonTemp);
                    //console.log(" response : "+response);

                    const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", response);
                    console.log(postResponse);
                    /* await Request.get({
                        url: url,
                        timeout: 120000
                    }, async (error, response, body) => {
                        if (error) {
                            console.log(" Error in Security : ");
                            console.log(error);
                            let resultJson = {
                                "executionId": executionId,
                                "url": url,
                                "region": region,
                                "audits": jsonTemp["no-vulnerable-libraries"],
                                "error": true,
                                "errorMessage": "Error in getting response headers"
                            }
                            await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", resultJson);
                        } else {
                            let responseSecurity = JSON.parse(JSON.stringify(response));
                            delete responseSecurity["body"];
                            let resultJson = {
                                "executionId": executionId,
                                "url": url,
                                "region": region,
                                "audits": jsonTemp["no-vulnerable-libraries"],
                                "error": false,
                                "securityheaders": responseSecurity["headers"]
                            }
                            console.log(" resultJson from security : ");
                            //console.log(resultJson);
                            const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", resultJson);
                            console.log(postResponse);
                        }
                    }); */

                } catch (error) {
                    console.log(" Error occured in lighthouse for security : ");
                    console.log(error);
                    let response=await generateSecurityResponse(url,executionId,region,null);
                    /* let resultJson = {
                        "executionId": executionId,
                        "url": url,
                        "region": region,
                        "error": true,
                        "errorMessage": "Error in generating data from lighthouse"
                    } */
                    await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", response);
                }
                break;
            }
            case "seo": {
                try {
                    console.log("Seo :");
                    chrome = await chromeLauncher.launch({ chromeFlags: ['--headless'] });
                    const config = {
                        extends: 'lighthouse:default',
                        settings: {
                            onlyCategories: ['seo'],
                            formFactor: 'desktop',
                            throttling: {
                                method: "provided"
                            },
                            screenEmulation: {
                                mobile: false,
                                width: 1350,
                                height: 940,
                                deviceScaleFactor: 1,
                                disabled: false,
                            }
                        }
                    }
                    const opts = {
                        chromeFlags: ['--headless', '--disable-gpu'],
                        port: chrome.port
                    }
                    let results = await lighthouse(url, opts, config);
                    //console.log(results);
                    if (results === undefined || results === null || results === "") {
                        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                        return res.status(500).json("Error in getting Seo Metrics");
                    }
                    let jsonTemp = results.lhr.audits;
                    delete jsonTemp["full-page-screenshot"];
                    //console.log(jsonTemp);
                    let resultJson = {};
                    if (results.lhr.categories.seo != undefined) {
                        let seoAudits = results.lhr.categories.seo.auditRefs;
                        let seoKeys = Object.keys(jsonTemp);
                        let auditArr = [];
                        //console.log("  seoKeys:");
                        //console.log(seoKeys);
                        for (let key of seoKeys) {
                            if (jsonTemp[key].scoreDisplayMode === "binary" || jsonTemp[key].scoreDisplayMode === 'notApplicable') {
                                let audits = seoAudits.find((item) => item.id == key);
                                let auditsRef = JSON.parse(JSON.stringify(jsonTemp[key]));
                                auditsRef["seoGroup"] = audits.group;
                                auditArr.push(auditsRef);
                            }
                        }
                        resultJson["score"] = results.lhr.categories.seo.score;
                        resultJson["audits"] = auditArr;
                    }
                    console.log(" result json object in seo");
                    //console.log(resultJson);
                    resultJson.executionId = executionId;
                    resultJson.url = url;
                    resultJson.region = region;
                    resultJson.error = false;
                    //console.log(authAPIUrl + " : " + reportApiUrl + " : " + AuthJsonObj);
                    const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSeo", resultJson);
                    console.log(postResponse);
                    //await chrome.kill();
                } catch (error) {
                    console.log(" Error in SEO : " + error);
                    resultJson.executionId = executionId;
                    resultJson.url = url;
                    resultJson.region = region;
                    resultJson.error = true;
                    await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSeo", resultJson);
                    //await chrome.kill();
                }
                break;
            }
        }
        if (count === modules.length - 1) {
            console.log("All Execution Completed");
        }
        count++;
    }

}


function generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, moduleType, resultJson) {
    return new Promise((resolve, reject) => {
        console.log(" Module : " + moduleType);
        Request.post({
            url: authAPIUrl + "auth/token",
            json: true,
            body: AuthJsonObj
        }, (error, response, body) => {
            if (error) {
                console.log(error);
                reject(error);
            } else {
                //console.log(body);
                Request.post({
                    url: reportApiUrl + moduleType,
                    json: true,
                    body: resultJson,
                    headers: {
                        'Authorization': 'Bearer ' + body.auth_token
                    }
                }, (error, response, body) => {
                    if (error) {
                        console.error(error);
                        reject(error);
                    } else {
                        console.log("response header :");
                        //console.log(body);
                        resolve(body);
                    }
                });
            }
        })
    })
}


function generateSecurityResponse(url,executionId, region, jsonLG) {
    return new Promise((resolve, reject) => {
        Request.get({
            url: url,
            timeout: 70000
        },  (error, response, body) => {
            if (error) {
                console.log(" Error in getting response headers for security : ");
                console.log(error);
                let resultJson = {
                    "executionId": executionId,
                    "url": url,
                    "region": region,
                    "audits": jsonLG["no-vulnerable-libraries"],
                    "error": true,
                    "errorMessage": "Error in getting response headers"
                }
                resolve(resultJson);
                //await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", resultJson);
            } else {
                let responseSecurity = JSON.parse(JSON.stringify(response));
                delete responseSecurity["body"];
                let resultJson ={};
                if(jsonLG===null){
                    resultJson={
                        "executionId": executionId,
                        "url": url,
                        "region": region,
                        "audits": null,
                        "error": true,
                        "securityheaders": responseSecurity["headers"],
                        "errorMessage": "Error in generating data from lighthouse"
                    }
                }else{
                    resultJson={
                        "executionId": executionId,
                        "url": url,
                        "region": region,
                        "audits": jsonLG["no-vulnerable-libraries"],
                        "error": false,
                        "securityheaders": responseSecurity["headers"]
                    }
                }          
                console.log(" resultJson from security : ");
                resolve(resultJson);
                //console.log(resultJson);
                //const postResponse = await generatePostAPI(authAPIUrl, AuthJsonObj, reportApiUrl, "insertSecurity", resultJson);
                //console.log(postResponse);
            }
        });
    });
}
module.exports = router;
