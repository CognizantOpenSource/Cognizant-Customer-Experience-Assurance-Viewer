import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { filter, map, mergeMap } from 'rxjs/operators';
import { Observable, ReplaySubject } from 'rxjs';
import { Theme } from '@model/types.model';
import { UserConfigService } from '@services/user-config.service';
import { ExternalAppService } from '@services/external-app.service';
import { LocalStorage } from '@services/local-storage.service';
import { AuthRestAPIService } from '@services/auth/auth-rest-api.service';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  scramble:Boolean=true;
  theme$: Observable<Theme>;
  private _authSource = new ReplaySubject<any>(1);
  touched = false;
  url:String=""
  constructor(
    private location: Location, private router: Router, private activatedRoute: ActivatedRoute,
    private titleService: Title, private toastr: ToastrService, private restApi: AuthRestAPIService,private storageService: LocalStorage,private config: UserConfigService, private extAppService: ExternalAppService) { 

      if(event instanceof NavigationEnd) {
        if(event.url.startsWith("/scramble")){
          //console.log(event.url)
          this.scramble=false
          this.url=event.url
        }
        else{
          this.scramble=true
        }
      }
    }

  ngOnInit() {
    this.updateTitle();
    this.theme$ = this.config.theme$;
    }
  get showFullHeader$() {
    return this.extAppService.status$.pipe(map((extApp: any) => !extApp.loaded || !extApp.fullscreen));
  }
  onBack($event) {
    this.location.back();
  }
  updateTitle() {

    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
          map(() => {
      let route = this.activatedRoute;
            while (route.firstChild) { route = route.firstChild; }
            return route;
          }),
          filter((route) => route.outlet === 'primary'),
          mergeMap((route) => route.data),
          map((data: any) =>
            data.title || this.router.url.split(/[?/=&]/).reduce((acc, next) => {
              if (acc && next) { acc += ' '; }
              return acc + toTitleCase(next);
            }))
        ).subscribe((data) => {
          const projectName=this.router.url.split("/")[5];
          let title;
          if(this.router.url.startsWith('/scramble')){
              title =  this.titleService.setTitle(`CxA -  Dashboard`);
          }
          else{
              title = this.titleService.setTitle(`CxA - ${data}`);
          }
    
    
    
        });}
}
function toTitleCase(str) {
  return str.replace(/\w\S*/g, (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
  );
}