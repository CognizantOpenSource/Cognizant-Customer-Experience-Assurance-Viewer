import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from "rxjs/operators";
@Injectable({
  providedIn: 'root'
})
export class CxadashboardServicesService {
  base_url: string = "http://localhost:9003"
  constructor(private http: HttpClient) { }


  addNewProject(projectName: string): Observable<any> {
    return this.http.post(`${this.base_url}/workbench/projects`, { "name": projectName })
  }
  getauthToken() {
    return `${this.base_url}auth/token`;
  }
  getExecutionID(): Observable<any> {
    let auth_token = this.getauthToken();
    const headers = {
      'Accept': 'text/plain',
      'Authorization': `Bearer ${auth_token}`
    }
    return this.http.get(`${this.base_url}/test/reports/executionId`,
      { headers: headers, responseType: 'text' });
  }
  insertMetricsData(data) {
    return this.http.post(`${this.base_url}/test/reports/insertMetricsData`,
      {
        "mobileRating": data.mobileRating,
        "performancesCheck": data.performancesCheck,
        "androidAppName": data.androidAppName,
        "iosAppName": data.iosAppName,
        "appId": data.appId,
        "url": data.url,
        "crossBrowser":data.crossBrowser,
        "projectId": data.projectId,
        "accessibilityCheck": data.accessibilityCheck,
        "securityCheck": data.securityCheck,
        "seoCheck":data.seoCheck,
        "uiuxValidatorCheck": data.uiuxCheck,
        "iosRegion": data.iosRegion,
        "performanceRegion": data.AllRegion,
        "validations":data.validations,
        "activeMonitorCheck":data.activeMonitorCheck,
        "region":data.region,
        "environment":data.environment,
        "interval":data.interval,

        "duration":data.duration,
        "urlAndRegion":data.urlAndRegion,

        "activeMonitorEnvir":data.activeMonitorEnvir,

        "activeMonitorUrlAndRegion":data.activeMonitorUrlAndRegion
      }, { responseType: 'text', headers: { hideLoader: 'true' } })
  }
  
  insertPerformanceData(url, exeId) {
    return this.http.post(`${this.base_url}/test/reports/insertPerformanceData`,
      { "url": url, "executionId": exeId })
  }
  getSummaryDetails(projectId) {
    return this.http.get<any>(`${this.base_url}/test/reports/getSummaryDetails?projectId=` + projectId,
      { headers: { hideLoader: 'true' } });
  }
  getPerformance(projectId) {
    return this.http.get<any>(`${this.base_url}/test/reports/getPerformances?projectId=` + projectId);
  }
  getAccessibility(projectId) {
    return this.http.get<any>(`${this.base_url}/test/reports/getAccessibilityMetricsData?projectId=` + projectId);
  }
  getSecurity(projectId) {
    return this.http.get<any>(`${this.base_url}/test/reports/getSecurityMetricsData?projectId=` + projectId);
  }
  getOmnichannel(projectId) {
    return this.http.get<any>(`${this.base_url}/test/reports/getOmnichannelPerformance?projectId=` + projectId);
  }
  validatePerformanceUrl(url) {
    return this.http.get<any>(`${this.base_url}/test/reports/validatePerformance?url=` + url);
  }
  getSeoDetails(projectId) {    return this.http.get<any>(`${this.base_url}/test/reports/getSeoDetails?projectId=` + projectId);

}
  getSecurityOwaspData(exeID) {
    return this.http.get<any>(`${this.base_url}/test/reports/getSecurityOwaspData?executionId=` + exeID);
  }
  getAllRegionsForPerformance() {
    return this.http.get<any>(`${this.base_url}/test/reports/getAllRegionsForPerformance`);
  }

  getActiveMonitorRegionAndLoadTime(projectId) {

    return this.http.get<any>(`${this.base_url}/test/reports/getActiveMonitorRegionAndLoadTime?projectId=` + projectId);

  }
 
}
