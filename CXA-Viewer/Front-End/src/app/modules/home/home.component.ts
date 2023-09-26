import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UnSubscribable } from '@components/unsub';
import { ProjectNameService } from '@services/project/project-name.service';
import { ProjectService } from '@services/project/project.service';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';
import { CxadashboardServicesService } from '../cxadashboard/services/cxadashboard-services.service';
import { Project } from '@model/data.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent extends UnSubscribable implements OnInit {
  searchBy: string;
  name: string;
  showNewProject = false;
  projectName: string;
  projects$: Observable<Project[]>;
  get projectsFilter(): (project: Project) => boolean {
    const context = this;
    return (project) => (!context.searchBy || context.searchBy === '')
      || (project && project.name.toLowerCase().includes(context.searchBy.toLowerCase()));
  }
  constructor(private projectService: ProjectService,
    private cxadashboardServicesService: CxadashboardServicesService,
    private router: Router,
    private toastr: ToastrService,
    public dialog: MatDialog,
    public projectNameService: ProjectNameService) {
    super();
    this.projects$ = projectService.projects$;
  }
  showNewProjectToggle(): void {
    this.showNewProject = !this.showNewProject
  }
  addNewProject(): void {
    this.cxadashboardServicesService.addNewProject(this.projectName).subscribe(data => {
      this.showNewProject = !this.showNewProject
      this.projectService.loadProjects();
      this.toastr.success('Project created successfully');
    }, error => {
      this.toastr.error(error.error.message)
    })
  }
  openDialog(): void {
    const dialogRef = this.dialog.open(NewProjectModal, {
      width: '500px',
      data: { name: this.name },
    });

    dialogRef.afterClosed().subscribe(result => {
      //console.log('The dialog was closed');
      this.name = result.name;
    });
  }
  ngOnInit() {
    this.projectService.loadProjects();
    this.projectNameService.projectName = ""
    this.managed(this.projects$).subscribe(projects => {
      if (projects && projects.length === 0) {
        this.toastr.warning('no projects to list, please create new!');
        this.router.navigate(['pipeline', 'new']);
      }
    });
  }

  delete(project: Project) {
    if (confirm(`Are you sure to delete project '${project.name}'`)) {
      this.projectService.removeProject(project);
    }
  }
  loadProject(project) {
    this.projectService.loadProject(project.id);
    localStorage.setItem("projectId", project.id);
    this.projectNameService.projectName = project.name
    this.router.navigate(['cxadashboard/dashboard/summary', project.id]);
  }
  redirectToCxaDashboard(project) {
    this.projectService.loadProject(project.id);
    this.projectNameService.projectName = project.name
    localStorage.setItem("projectId", project.id);
    this.router.navigate(['cxadashboard/cxa-suite', project.id]);
  }
}

export interface DialogData {
  name: string;
}

@Component({
  selector: 'new-project-modal',
  templateUrl: './new-project-modal.html'
})
export class NewProjectModal {
  constructor(
    public dialogRef: MatDialogRef<NewProjectModal>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}