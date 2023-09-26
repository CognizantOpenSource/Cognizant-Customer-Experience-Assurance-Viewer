import { ChangeDetectorRef, Component, Inject, OnInit, OnDestroy, Input } from '@angular/core';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { interval, Subscription } from 'rxjs';
import * as moment from 'moment';
@Component({
  selector: 'leap-performance',
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class PerformanceComponent implements OnInit, OnDestroy {
  @Input() top: any = "true"
  speedIndex: any = null;
  imageObject: any = [];
  projectId = "";
  multi: any = [
    {
      name: "",
      series: []
    },
  ];
  performanceMessage = "";
  checkPerformanceError = false;
  rtTooltip = "The First Contentful Paint (FCP): metric measures the time from when the page starts loading to when any part of the page's content is rendered on the screen.&#13;The Largest Contentful Paint (LCP): metric reports the render time of the largest image or text block visible within the viewport, relative to when the page first started loading.&#13;Time To Interactive(TTI): measures how long it takes a page to become fully interactive.";
  speedIndexTooltip = "Speed Index: measures how quickly content is visually displayed during page load.";
  perfomanceData: any = null;
  lastRun: any = null;
  url: any = null;
  performanceSubscription: Subscription;
  performanceStatus = '';
  performanceSubscribed = false;
  subscriptionInterval = 10000;
  constructor(private cxadashboardServices: CxadashboardServicesService,
    private changeDetectorRefs: ChangeDetectorRef, private router: Router, private route: ActivatedRoute,
    public dialog: MatDialog) {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.getPerformanceData();
  }
  setPerformanceSubscription() {
    if (!this.performanceSubscribed && !(this.performanceStatus === 'Completed'
      || this.performanceStatus === 'InComplete')) {
      this.performanceSubscription = interval(this.subscriptionInterval).subscribe((val) => {
        if (this.performanceStatus === 'Completed' || this.performanceStatus === 'InComplete') {
          this.performanceSubscription.unsubscribe();
        } else {
          this.getPerformanceData();
        }
      });
      this.performanceSubscribed = true;
    }
  }
  getPerformanceData() {
    this.cxadashboardServices.getPerformance(this.projectId).subscribe(data => {
      if (data === null || data === undefined || data === "") {
        this.performanceMessage = "Scan not done";
        this.checkPerformanceError = true;
        this.changeDetectorRefs.markForCheck();
      } else {
        this.perfomanceData = data;
        this.performanceStatus = this.perfomanceData.status ? this.perfomanceData.status : ''
        this.setPerformanceSubscription();
        this.lastRun = this.perfomanceData.timestamp ? this.setLastRun(this.perfomanceData.timestamp) : null;
        this.url = this.perfomanceData.url ? this.perfomanceData.url : null;
        if (data.error != undefined || data.error != null || data.error != "") {
          if (data.error) {
            this.checkPerformanceError = true;
            this.performanceMessage = "Error Occurred in Execution";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.speedIndex = parseFloat(this.perfomanceData.speedIndex.split(" ")[0]);
            this.setImageObject(this.perfomanceData.screenshot);
            this.setResponseTimeChart(data);
            this.changeDetectorRefs.markForCheck();
          }
        }
      }
    });
  }
  setImageObject(images) {
    images.map((item) => {
      let newImage = {};
      newImage["image"] = item.data
      newImage["thumbImage"] = item.data
      newImage["title"] = item.timing + "ms"
      this.imageObject.push(newImage)
    })

  }
  ngOnInit() {

  }


  setResponseTimeChart(perfomanceData) {


    let interactive = Number(perfomanceData.serverResponseTime.replace("s", ""));
    interactive = Math.trunc(interactive);
    let lcp = Number((perfomanceData.largestContentfulPaint.replace("s", "")).replace(/ /g, ""));
    lcp = Math.trunc(lcp * 1000);
    let obj = {
      name: "Time to first byte",
      value: interactive
    }
    this.multi[0].series.push(obj);
    obj = {
      name: "First Contentful Paint",
      value: Math.trunc(Number(perfomanceData.firstContentfulPaint.replace("s", "")) * 1000)
    }
    this.multi[0].series.push(obj);
    let obj2 = {
      name: "Largest Contentful Paint",
      value: lcp
    }
    this.multi[0].series.push(obj2);

    const newObj = this.multi;
    this.multi = JSON.parse(JSON.stringify(newObj));
    this.changeDetectorRefs.markForCheck();

  }
  onInfoClick() {
    //console.log("info click")
    this.dialog.open(SpeedIndexModal, {
    });
  }
  onRTInfoClick() {
    //console.log("info click")
    this.dialog.open(ResponceTimeModal, {
    });
  }
  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }
  ngOnDestroy(): void {
    if (this.performanceSubscription) {
      this.performanceSubscription.unsubscribe();
    }
  }
}

export class SpeedIndexModal {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}

export class ResponceTimeModal {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}