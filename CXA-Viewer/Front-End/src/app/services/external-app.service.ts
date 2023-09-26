import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ExternalApp {
  url: string;
  token: string;
  tokenType: string;
  tokenParam?: string;
  tokenTypeParam?: string;
  [key: string]: string;
}

export interface ExternalAppStatus {
  app: ExternalApp | undefined;
  loaded: boolean;
  fullscreen?: boolean;
}
@Injectable({
  providedIn: 'root'
})
export class ExternalAppService {

  private _stateSource = new BehaviorSubject<ExternalAppStatus>({
    app: undefined,
    loaded: false,
    fullscreen: false
  });
  private _state$ = this._stateSource.asObservable();

  constructor() { }

  get status$(): Observable<ExternalAppStatus> {
    return this._state$;
  }
  mount(app: ExternalApp, fullscreen = false) {
    this._stateSource.next({ app, loaded: true, fullscreen });
  }
  unmount(app: ExternalApp) {
    this._stateSource.next({ app, loaded: false });
  }
}
