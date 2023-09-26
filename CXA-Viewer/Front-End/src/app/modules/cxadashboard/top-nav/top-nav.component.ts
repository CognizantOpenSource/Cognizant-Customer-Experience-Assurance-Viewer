import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'leap-top-nav',
  templateUrl: './top-nav.component.html',
  styleUrls: ['./top-nav.component.css']
})
export class TopNavComponent implements OnInit {
  router: string;
  projectId:any;
  constructor(private route: Router) {
    this.router = route.url;
  }

  ngOnInit() {
    this.projectId = localStorage.getItem("projectId");
    // this.projectId = "123";
    
    if (this.projectId === "" || this.projectId === null) {
      this.route.navigate(["../home"]);
    }
  }
  onDashboardClick() {
    this.route.navigate(['/cxadashboard/dashboard/summary', this.projectId])
  }
  hasDashboard() {
    return this.route.url.includes('/cxadashboard/dashboard');
  }
  // hasActiveMonitor(){
  //   return this.route.url.includes('/cxadashboard/activemonitors');
  // }
  // onActiveMonitor() {
  //   this.route.navigate(['/cxadashboard/activemonitors/activemonitor', this.projectId])
  // }
}
