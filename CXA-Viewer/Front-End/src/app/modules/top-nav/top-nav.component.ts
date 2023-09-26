import { Component, OnInit } from '@angular/core';
import { Project } from '@model/data.model';
import { Observable } from 'rxjs';
import { ProjectService } from '@services/project/project.service';
import { AuthenticationService } from '@services/auth/authentication.service';
import { filter, take, tap, map } from 'rxjs/operators';
import { UserConfigService } from 'src/app/services/user-config.service';
import { Theme } from '@model/types.model';
import { ExternalAppService } from '@services/external-app.service';
import { UnSubscribable } from '@components/unsub';
import { ActivatedRoute, Router,NavigationEnd } from '@angular/router';
import { ProjectNameService } from '@services/project/project-name.service';

@Component({
  selector: 'app-top-nav',
  templateUrl: './top-nav.component.html',
  styleUrls: ['./top-nav.component.css']
})
export class TopNavComponent extends UnSubscribable implements OnInit {

  projects$: Observable<Project[]>;
  allLinks: Array<any> = [
    { data: 'home', name: 'Home', type: 'clr-icon', desc: 'Home', routerLink: ['/home'] },
];
  scramble:boolean=true;
  links: Array<any>;
  loggedIn = false;
  theme$: Observable<Theme>;
  showFullHeader$: Observable<boolean>;
  projectName = '';
  constructor(
    private projectService: ProjectService, public auth: AuthenticationService,
    public config: UserConfigService, private extAppService: ExternalAppService,
    private route: ActivatedRoute, private router: Router, public projectNameService: ProjectNameService) {
    super();
    router.events.forEach((event) => {
      if(event instanceof NavigationEnd) {
        if(event.url.startsWith("/scramble")){
          this.scramble=false
        }
        else{
          this.scramble=true
        }
      }
     } )  
    this.projects$ = projectService.projects$;
    this.projectNameService.itemValue.subscribe(value => {
      this.projectName = value;
    })
  }

  ngOnInit() {
    this.auth.state$.pipe(filter(auth => auth), take(1), tap(() => this.loggedIn = true)).
      subscribe(() => this.projectService.loadProjects());

    this.theme$ = this.config.userSettings$.pipe(map((it: any) => (it.dashboard && it.dashboard.theme) || Theme.default));
    this.showFullHeader$ = this.extAppService.status$.pipe(map(extApp => !extApp.loaded || !extApp.fullscreen));

    this.setLinks();
    
  }
  private setLinks() {
    this.links = [...this.allLinks.filter(l => !l.exclude)];
  }
}
