import { ChangeDetectorRef, Component, Input,  OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import * as moment from 'moment-timezone';
import { some, isEmpty } from 'lodash'
@Component({
  selector: 'activemonitor',
  templateUrl: './activemonitor.component.html',
  styleUrls: ['./activemonitor.component.scss'],

})
export class ActivemonitorComponent implements OnInit {
  @Input() top: any = "true";
  starttime: any;
  duration: any;
  projectId: any;
  linechart: any;
  latesttime: any;
  categories: any;
  loadTimeData: any;
  maxLoadTime: any;
  barchart: any;
  interval: any;
  checkActiveMonitorError = false;
  ActiveMonitorMessage = "";
  lastRun: any = null;
  url: any = null;
  lineColors = ["#1C6DD0", "#FC9918","#a8385d","#7aa3e5","#a27ea8"];
  activemonitorData: any;
  constructor(private cxadashboardServices: CxadashboardServicesService,
    private changeDetectorRefs: ChangeDetectorRef, private router: Router, private route: ActivatedRoute,
    public dialog: MatDialog) {

  }

  ngOnInit() {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    } else {
      this.getActiveMonitorData()
    }

  }

  getActiveMonitorData() {

    this.cxadashboardServices.getActiveMonitorRegionAndLoadTime(this.projectId).subscribe(data => {

      if (data === null || data === undefined || data === "" || data.length <= 0 || !some(data, isEmpty)) {
        this.checkActiveMonitorError = true;
        this.ActiveMonitorMessage = "Scan not done";
        this.changeDetectorRefs.markForCheck();
      } else {

        if (data.error !== undefined || data.error !== null || data.error !== "" || data.error == 'true') {
          if (data.error) {
            this.checkActiveMonitorError = true;
            this.ActiveMonitorMessage = "Error Occurred in Execution";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.activemonitorData = data;
            this.duration = data.duration*60 +" mins"
            this.interval = data.interval + " mins"
            this.lastRun = this.activemonitorData.timestamp ? this.setLastRun(this.activemonitorData.timestamp) : null;
            this.url = this.activemonitorData.url ? this.activemonitorData.url : null;
            this.getActiveMonitorRegionAndLoadTime(data.activeResponse);
          }
        }
      }

    })
  }


  setLastRun(timeStamp) {

    let dec = moment(timeStamp);

    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";

  }
  getActiveMonitorRegionAndLoadTime(data) {
    
    data.map(items => {
      let a = items.LoadTimeAndTimeStamp;
      let x = a.map(time => {
        let timestamp = new Date(time.timeStamp);

        let dec = moment(Math.min(timestamp.getTime()));

        this.starttime = dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";

    
      });
    })



    //console.log("this.duration-------------", data)
    // this.linechart = {
    //   chart: {
    //     theme: "fusion",
    //     xAxisName: "Minutes",
    //     yaxisName: "Time(ms)",
    //     formatNumberScale: "0",
    //     bgColor: "#f0f0f0",
    //     yAxisMaxValue: this.maxLoadTime
    //   },
    //   categories: data.map(items => {
    //     let a = items.LoadTimeAndTimeStamp;
    //     return {
    //       category: a.map(loadtime => {
    //         return {
    //           label: loadtime.Interval.toString()
    //         }
    //       })
    //     }
    //   }),
    //   dataset: data.map((items, i) => {

    //     let a = items.LoadTimeAndTimeStamp;

    //     return {
    //       seriesname: items.Region,

    //       color: this.lineColors[i % this.lineColors.length],

    //       data: a.map(loadtime => {

    //         return {

    //           value: loadtime.loadTime

    //         }

    //       })

    //     }

    //   }),

    // }

    // this.barchart = {
    //   chart: {
    //     yaxisname: "Time(ms)",
    //     xaxisname: "Region",
    //     decimals: "1",
    //     theme: "fusion",
    //     formatNumberScale: "0",
    //     bgColor: "#f0f0f0",

    //   },
    //   data: data.map((items, i) => {

    //     let a = items.LoadTimeAndTimeStamp;

    //     return {

    //       label: items.Region,

    //       value: items.AverageLoadTime,

    //       color: this.lineColors[i % this.lineColors.length]

    //     }

    //   })

    // };


    
      let barchartData: any = [];
      data.map(data => {
        let obj = {
          "name": data.Region,
          "value": data.AverageLoadTime
        }
        barchartData.push(obj);
      })
      this.barchart = barchartData

      let lineChartData: any = [];
      data.map(d => {
        let series: any = []
        d.LoadTimeAndTimeStamp.map(s => {
          let obj = {
            "name": s.Interval,
            "value": s.loadTime
          }
          series.push(obj)
        })
        let obj = {
          "name": d.Region,
          "series": series
        }
        lineChartData.push(obj)
      })
      this.linechart = lineChartData;
      //console.log("linechart----------", this.linechart)
    

  }

}
