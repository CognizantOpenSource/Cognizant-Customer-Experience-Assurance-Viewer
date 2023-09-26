import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpInterceptor, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocalStorage } from '../local-storage.service';
import { finalize } from 'rxjs/operators';
import { LoaderService } from '@services/loader.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  requestCount = 0;
  constructor(private localStorage: LocalStorage, private loaderService: LoaderService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const hideLoader = req.headers.has('hideLoader');
    if (req.headers.has('hideLoader')) {
      //console.log("hide loader...", req);
      const headers = req.headers.delete('hideLoader');
      req = req.clone({ headers });
    } else {
      this.requestCount++;
      this.loaderService.show();
    }
    let token = this.localStorage.getItem('auth_token');
    return next.handle(token ? this.auth(req, token) : req).pipe(
      finalize(() => {
        if (!hideLoader) {
          this.requestCount--;
        }
        if (!this.requestCount) {
          this.loaderService.hide();
        }
      }))
  }
  private auth(req: HttpRequest<any>, token: string): HttpRequest<any> {
    return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
}

