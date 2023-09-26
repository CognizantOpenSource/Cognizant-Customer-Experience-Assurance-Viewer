import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Project } from '@model/data.model';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { WorkBenchAPI } from './workbench-api';

@Injectable({
  providedIn: 'root'
})
export class WorkBenchRestAPIService {

  api: WorkBenchAPI;
  constructor(private http: HttpClient) {
    this.api = new WorkBenchAPI(environment.api.workbench);
  }

  getProject(id: string): Observable<Project> {
    return this.http.get(this.api.getProject(id)).pipe(map(Project.parse));
  }

  deleteProject(project: Project) {
    return this.http.delete(this.api.getProject(project.id), { responseType: 'text' });
  }

  getProjects(): Observable<any> {
    return this.http.get(this.api.projects);
  }

  saveDashBoardConfig(config: any) {
    return this.http.put(this.api.userDashboardConfig, config);
  }
}
