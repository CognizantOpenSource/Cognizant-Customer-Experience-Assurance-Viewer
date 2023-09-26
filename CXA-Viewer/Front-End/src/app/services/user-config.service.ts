import { Injectable } from '@angular/core';
import { map, take, filter, tap } from 'rxjs/operators';
import { AuthenticationService } from '@services/auth/authentication.service';
import { Theme } from '@model/types.model';
import { Observable, ReplaySubject } from 'rxjs';
import { WorkBenchRestAPIService } from './workbench-rest-api.service';

@Injectable({
  providedIn: 'root'
})
export class UserConfigService {

  private _userSource = new ReplaySubject<any>(1);
  private _user$ = this._userSource.asObservable();

  constructor(
    private auth: AuthenticationService,
    private api: WorkBenchRestAPIService
  ) { };

  get userSettings$(): Observable<any> {
    return this._user$.pipe(filter(it => !!it));
  }

  get theme$() {
    return this.userSettings$.pipe(map((it: any) => (it.dashboard && it.dashboard.theme) || Theme.default));
  }

  hasAccessEnabled(matcher) {
    return this.auth.permissions$.pipe(take(1), map(ps => ps && ps.some(p => p.match(matcher))));
  }

  setTheme(theme: Theme) {
    this.getUserConfig().subscribe(settings => {
      settings.dashboard = settings.dashboard || {} as any;
      settings.dashboard.theme = theme;
      this.setDashBoardConfig(settings.dashboard).subscribe();
      this._userSource.next(settings);
    });
  }

  getUserConfig(): Observable<any> {
    return this.userSettings$.pipe(take(1));
  }

  setDashBoardConfig(config: any) {
    return this.api.saveDashBoardConfig(config).pipe(tap(it => this._userSource.next(it)));
  }

}
