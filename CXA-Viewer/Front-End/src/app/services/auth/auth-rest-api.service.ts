import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { API as AuthAPI } from './auth-api';

@Injectable({
  providedIn: 'root'
})
export class AuthRestAPIService {
  api: AuthAPI;
  constructor(private http: HttpClient) {
    this.api = new AuthAPI(environment.api.auth);
  }
  createToken(payload: any) {
    //console.log("create token restapi")
    return this.http.post(this.api.authToken, payload);
  }
  getProfile() {
    return this.http.get(this.api.profile);
  }
  verfiyAuthCode(payload: any): Observable<any> {
    return this.http.post(this.api.authToken, payload);
  }
  updatePassword(params: any): Observable<any> {
    let reqObj={"oldPassword":params.oldPassword,
    "newPassword":params.newPassword}
    return this.http.post(this.api.updatePassword(params), reqObj);
  }
  generateAPIToken() {
    return this.http.post(this.api.APIToken, null);
  }
}
