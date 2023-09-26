import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { Project } from '@model/data.model';
import { WorkBenchRestAPIService } from '../workbench-rest-api.service';
import { tap } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { ModuleProxyService } from '@services/project/module-proxy.service';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private _projectSource = new ReplaySubject<Project>(1);
  private _projectsSource = new ReplaySubject<Project[]>(1);
  private _projects$ = this._projectsSource.asObservable();

  constructor(
    private restApi: WorkBenchRestAPIService, private moduleProxy: ModuleProxyService,
    private toastr: ToastrService) {
  }

  public loadProjects() {
    this.restApi.getProjects().subscribe({
      next: (projects) => this._projectsSource.next(projects),
      error: () => this._projectsSource.next([])
    });
  }

  public loadProject(id: any) {
    this.restApi.getProject(id).subscribe(project => this._projectSource.next(project));
  }

  public get projects$(): Observable<Project[]> {
    return this._projects$;
  }

  removeProject(project: Project) {
    this.restApi.deleteProject(project).pipe(tap(p => {
      this.moduleProxy.deleteProject(project).subscribe({
        next: () => console.log,
        error: () => console.error
      });
    })).subscribe({
      next: () => {
        this.toastr.success('project deleted successfully');
        this.loadProjects();
      },
      error: () => {
        this.toastr.error('error while deleting project')
      }
    });
  }
}
