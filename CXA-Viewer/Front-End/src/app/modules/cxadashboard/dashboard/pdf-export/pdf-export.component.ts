import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import { ProjectNameService } from '../../../../services/project/project-name.service'
import { interval, Subscription, Observable} from 'rxjs';
@Component({
  selector: 'leap-pdf-export',
  templateUrl: './pdf-export.component.html',
  styleUrls: ['./pdf-export.component.scss']
})
export class PdfExportComponent implements OnInit {
  projectId: any = null;
  projectName: String = "";
  lastRun:any="";
  summaryData: any;
  summary: any = {
    androidRating: "",
    iosRating: "",
    speedIndex: "",
    accessibility: "",
    security: "",
    uiux: "",
    omnichannel:"",
    activemonitor:"",
  }
  analysis:any = {
  }
  

  
  checkUiux=false;
  uiuxLabel=""
  subscriptions: Subscription[] = [];
  subscriptionInterval = 10000;


  mapdata :any;
  legends:any;
  constructor(private router: Router, private route: ActivatedRoute,
    private cxadashboardServices: CxadashboardServicesService,
    public projectNameService: ProjectNameService,
    private changeDetectorRefs: ChangeDetectorRef) {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.projectNameService.itemValue.subscribe(value=>{
      this.projectName=value;
    })
    this.getSummaryData();
  }
  getSummaryData() {
    this.cxadashboardServices.getSummaryDetails(this.projectId).subscribe(data => {
      this.summaryData = data;
      if (this.summaryData.androidRating != "") {
        if (this.summaryData.androidRating === "Error") {
          this.summary.androidRating = ""
        } else if (this.summaryData.androidRating === "InProgress") {
          this.summary.androidRating = ""
        } else {
          this.summary.androidRating = this.summaryData.androidRating
        }
      }
      if (this.summaryData.iosRating != "") {
        if (this.summaryData.iosRating === "Error") {
          if (this.summaryData.iosRating === "Error") {
            this.summary.iosRating = "";
          }
        } else if (this.summaryData.iosRating === "InProgress") {
          this.summary.iosRating = "";
        } else {
          this.summary.iosRating = this.summaryData.iosRating;
        }
      }
      if (this.summaryData.speedIndex != "") {
        if (this.summaryData.speedIndex === "Error") {
          this.summary.speedIndex=""
        } else if (this.summaryData.speedIndex === "InProgress") {
          this.summary.speedIndex=""
        } else {
         
          this.setSpeedIndex(this.summaryData.speedIndex);
        }
      }
      if (this.summaryData.accessibility != "") {
        if (this.summaryData.accessibility === "Error") {
         this.summary.accessibility=""
        } else if (this.summaryData.accessibility === "InProgress") {
          this.summary.accessibility=""
        } else {
          this.summary.accessibility = this.summaryData.accessibility;
        }
      }
      if (this.summaryData.security != "") {
        if (this.summaryData.security === "Error") {
          this.summary.security = ""
        } else if (this.summaryData.security === "InProgress") {
          this.summary.security = ""
        } else {
          this.summary.security = this.summaryData.security;
        }
      }
      if (this.summaryData.uiux != "") {
        if (this.summaryData.uiux === "Error") {
          this.checkUiux = true;
          this.uiuxLabel = "Error"
        } else if (this.summaryData.uiux === "InProgress") {
          this.checkUiux = true;
          this.uiuxLabel = "In Progress..."
        } else {
          this.checkUiux = false;
          this.summary.uiux = this.summaryData.uiux;
        }
      }
      if (this.summaryData.omniChannel != "") {
        if (this.summaryData.omniChannel === "Error") {
          this.checkUiux = true;
          this.uiuxLabel = "Error"
        } else if (this.summaryData.omniChannel === "InProgress") {
          this.checkUiux = true;
          this.uiuxLabel = "In Progress..."
        } else {
          this.checkUiux = false;
          this.summary.omnichannel = this.summaryData.omniChannel;
        }
      }
      if (this.summaryData.activeMonitor != "") {
        if (this.summaryData.activeMonitor === "Error") {
          this.checkUiux = true;
          this.uiuxLabel = "Error"
        } else if (this.summaryData.activeMonitor === "InProgress") {
          this.checkUiux = true;
          this.uiuxLabel = "In Progress..."
        } else {
          this.checkUiux = false;
          this.summary.activemonitor = this.summaryData.activeMonitor;
        }
      }
      if (data.timestamp != "") {
        this.setLastRun(this.summaryData.timestamp);
      }
    });
  }
  setSpeedIndex(speedIndex) {
    let si = parseFloat(speedIndex.split(" ")[0])
    if (si >= 0 && si <= 3.4) {
      this.summary.speedIndex = "Good"
    } else if (si > 3.4 && si <= 5.8) {
      this.summary.speedIndex = "Average"
    } else {
      this.summary.speedIndex = "Slow"
    }
  }
  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    this.lastRun = dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }
  
  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => {
      if (subscription) {
        subscription.unsubscribe();
      }
    });
  }
  ngOnInit() {
  }

}
