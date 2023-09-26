import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { tap, take } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { AuthRestAPIService } from './auth-rest-api.service';
import { LocalStorage } from '@services/local-storage.service';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private authService: AuthenticationService,
    private toastr: ToastrService, private restApi: AuthRestAPIService, private storageService: LocalStorage) { }
  private _authSource = new ReplaySubject<any>(1);
  touched = false;
  
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    
this.authService.validateToken();
return this.authService.state$.pipe(take(1), tap(auth => {
  if (!auth) {
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  }
})); 

  }


}