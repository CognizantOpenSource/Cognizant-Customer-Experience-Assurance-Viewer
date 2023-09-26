import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { map, filter, tap, switchMap } from 'rxjs/operators';
import { LocalStorage } from '../local-storage.service';
import { ToastrService } from 'ngx-toastr';
import * as moment from 'moment';
import { AuthRestAPIService } from './auth-rest-api.service';
import { Router, NavigationEnd } from '@angular/router';

function parseQuery(queryString): any {
  const query = {};
  const pairs: Array<string> = (queryString[0] === '?' ? queryString.substr(1) : queryString).split('&');
  pairs.map(pair => pair.split('=')).forEach(pair => {
    query[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1] || '');
  });
  return query;
}

const LOGGED_OUT = '__logout__';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private _auth: Observable<any>;
  private _authSource = new ReplaySubject<any>(1);
  touched = false;
  scramble = false;
  constructor(
    private storageService: LocalStorage,
    private toastr: ToastrService,
    private restApi: AuthRestAPIService,
    private router: Router
  ) {

    router.events.forEach((event) => {
      if (event instanceof NavigationEnd) {
        if (event.url.startsWith("/scramble")) {
          this.scramble = false
        }
        else {
          this.scramble = true
        }
      }
    })
    this._auth = this._authSource.asObservable();
    this.validateLogin();
  }

  private validateLogin() {
    const qParams = parseQuery(window.location.search);
    if (qParams.leap_token) {
      if (typeof(Storage) !== "undefined") {
        this.storageService.setItem("auth_token",qParams.leap_token);
  } else {
    // Sorry! No Web Storage support..
  }
     // this.storageService.setItem('auth_token', qParams.leap_token);
      this.loadProfile();
    } else {
      if (this.isValid()) {
        this.loadProfile()
      } else {
        this._authSource.next(false)
      };
    }
  }

  private isValid(): boolean {
    const expiresAt = this.storageService.getItem('expires_at');
    const expiry: number | null = expiresAt ? new Date(expiresAt).getTime() : null;
    
    const authToken = this.storageService.getItem('auth_token');
    return authToken && expiry && moment(expiry).isAfter(moment().add(1, 'minutes')) ? true : false;
  }

  validateToken() {
    if (!this.isValid()) {
      this._authSource.next(false);
    }
  }

  get state$(): Observable<boolean> {
    return this._auth.pipe(map(auth => auth && (auth === true || auth.email) ? true : false));
  }

  get user$(): Observable<any> {
    return this._auth.pipe(filter(auth => auth && auth.email));
  }

  get permissions$(): Observable<Array<string>> {
    return this.user$.pipe(map(user => user.account.roles.flatMap(it => it.permissions).map(it => it.id)));
  }

  login(username: string, password: string, type = 'native') {

    return this.authenticate({ type, username, password });

  }

   getLogin(username: string, password: string, type = 'native') {
    return new Promise((resolve, reject) => {
    //console.log("Inside get Login");
     this.restApi.createToken({ type, username, password }).subscribe(
      {
        next: async (res: any) => {
          //console.log("Inside Scaramble subscribe")
          if (typeof(Storage) !== "undefined") {
            this.storageService.setItem("auth_token",res.auth_token);
      } else {
        // Sorry! No Web Storage support..
      }
          // this.storageService.setItem('auth_token', res.auth_token);
          this.storageService.setItem('expires_at', res.expiresAt);
          await this.loadProfile();
          resolve(true);
        },
        error: (error) => {
          if (error.status === 401 || error.status === 404) {
            this.toastr.error('invalid email id/password');
          } else {
            //console.log('----------------------FAILED')
            this.toastr.error(error.status ? error.error.message : 'application is offline')
          }
        }
      }); 
    });
  }

  private scrambleAuthenticate(payload) {
    this.restApi.createToken(payload).subscribe(
      {
        next: async (res: any) => {
          //console.log("Inside Scaramble subscribe")
          if (typeof(Storage) !== "undefined") {
            this.storageService.setItem("auth_token",res.auth_token);
      } else {
        // Sorry! No Web Storage support..
      }
          // this.storageService.setItem('auth_token', res.auth_token);
          this.storageService.setItem('expires_at', res.expiresAt);

          await this.loadProfile();

          return res;

        },
        error: (error) => {
          if (error.status === 401 || error.status === 404) {
            this.toastr.error('invalid email id/password');
          } else {
            //console.log('----------------------FAILED')
            this.toastr.error(error.status ? error.error.message : 'application is offline')
          }
        }
      });
  }

  public logout(): Observable<any> {
    this.storageService.clearAll();
    return this.storageService.removeItem('auth_token').pipe(
      switchMap(() => this.storageService.removeItem('expires_at')),
      map(() => LOGGED_OUT), tap(it => this._authSource.next(it)));
  }

  public onSuccessAuth(res: any) {
    if (typeof(Storage) !== "undefined") {
      this.storageService.setItem("auth_token",res.auth_token);
} else {
  // Sorry! No Web Storage support..
}
    // this.storageService.setItem('auth_token', res.auth_token);
    this.storageService.setItem('expires_at', res.expiresAt);
    this.loadProfile();
  }

  private authenticate(payload) {

    this.restApi.createToken(payload).subscribe(
      {
        next: (res: any) => {if (typeof(Storage) !== "undefined") {
          this.storageService.setItem("auth_token",res.auth_token);
    } else {
      // Sorry! No Web Storage support..
    }
        // this.storageService.setItem('auth_token', res.auth_token);
          this.storageService.setItem('expires_at', res.expiresAt);
          
          this.loadProfile();

        },
        error: (error) => {
          if (error.status === 401 || error.status === 404) {
            this.toastr.error('invalid email id/password');
          } else {
            //console.log('----------------------FAILED')
            this.toastr.error(error.status ? error.error.message : 'application is offline')
          }
        }
      });
  }

  public updatePassword(userData) {
    return this.restApi.updatePassword(userData).subscribe({
      next: (res) => {
        if (res.status === 200) {
          this.toastr.success('password changed successfully');
          this.router.navigate(['/user/profile']);
        } else {
          this.toastr.error('error while updating password');
        }
      },
      error: (error) => {
        this.toastr.error(error.error.message);
      }
    });
  }

  private loadProfile() {
    this.restApi.getProfile().subscribe({
      next: (profile) => {
        this.touched = true;
        this._authSource.next(profile);
        this.storageService.setItem("user",JSON.parse(JSON.stringify(profile)).firstName)
       
      },
      error: (error) => { this.toastr.error(error.status ? error.error.message : 'application is offline') }
    });
  }

  generateAPIToken(): Observable<any> {
    return this.restApi.generateAPIToken();
  }

  checkAuthFactor(username: string, password: string, type = 'native'): Observable<any> {
    return this.restApi.createToken({ username, password, type });
  }

  verfiyAuthCode(payload) {
    return this.restApi.verfiyAuthCode(payload);
  }
  isGuestUser():boolean{

    const userStatus = this.storageService.getItem('user');

    return userStatus === 'guest';

}
}