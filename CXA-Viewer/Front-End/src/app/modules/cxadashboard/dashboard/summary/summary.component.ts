import { Component, OnInit, ChangeDetectorRef,OnDestroy } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router,NavigationEnd,RoutesRecognized } from '@angular/router';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import { Moment } from 'moment';
// import moment from 'moment-timezone';
import { interval, Subscription } from 'rxjs';
import * as moment from 'moment-timezone';
import { some, isEmpty } from 'lodash'
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { filter, pairwise } from 'rxjs/operators';


@Component({
  selector: 'leap-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.css']
})
export class SummaryComponent implements OnInit, OnDestroy {
  summary: any = {
    "speedIndex": "",
    "accessibility": "",
    "seoScore":"",
    "security": "",
    "omniChannel":"",
    "activeMonitor":""
  }
  previousUrl:any;
  currentUrl: string = "";
  summaryData: any = null;
  lastRun: any = null;
  androidData: any = null;
  iosData: any = null;
  color='primary';
  diameter="40";
  androidRating: any = null;
  ar: any = [];
  loadingflag=true;
  projectId = "";
  projectName="";
  checkAndroid: Boolean;
  androidLabel = "";
  checkIOS: Boolean = false;
  iosLabel = "";
  checkPerformance: Boolean = false;
  preformanceLabel = "";
  checkSecurity: Boolean = false;
  securityLabel = "";
  checkUiux: Boolean = false;
  checkomnichannel: Boolean = false;
  omnichannelLabel = "";
  checkactivemonitor: Boolean = false;
  activemonitorLabel = "";
  accessibilityLabel = "";
  checkAccessibility = false;
  summarySubscription: Subscription;
  summaryStatus = '';
  summarySubscribed = false;
  subscriptionInterval = 10000;
  checkOmniError=false;
  checkPerformanceError = false;
  checkActiveMonitorError=false;
  performanceMessage = "";
  barchartdata: any;
  omnichannelData: any = null;
  browserName:" ";
  checkseo:Boolean=false;
  seoLabel="";
  starttime:any;
  currenttime:any;
  endtime:any;
  time:any;

  scramble:Boolean=true;
  varwidth = '70';
  varheight = '70';
  linechartdata:any;
  constructor(private router: Router, private route: ActivatedRoute, private changeDetectorRefs: ChangeDetectorRef,
    private cxadashboardServices: CxadashboardServicesService) {
    
    //console.log("Checking Scramble Summary Com")
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
  
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
      this.projectName=params["projectName"]
      //console.log(this.route.params)
      // this.projectId ="123"
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.getSummaryData();
    
  }
  getSummaryData() {
    this.cxadashboardServices.getSummaryDetails(this.projectId).subscribe(data => {
      if(data!=null && data != undefined && data!= " "){
       
      this.summaryData = data;
      
      this.summaryStatus =
        (this.summaryData.androidRating && this.summaryData.androidRating === "InProgress")
          || ((this.summaryData.iosRating && this.summaryData.iosRating === "InProgress")
            || (this.summaryData.speedIndex && this.summaryData.speedIndex === "InProgress")
            || (this.summaryData.accessibility && this.summaryData.accessibility === "InProgress")
            || (this.summaryData.security && this.summaryData.security === "InProgress")
            || (this.summaryData.uiux && this.summaryData.uiux === "InProgress")
            || (this.summaryData.activeMonitor && this.summaryData.activeMonitor === "InProgress")
            || (this.summaryData.omniChannel && this.summaryData.omniChannel === "InProgress")
            || (this.summaryData.seoScore && this.summaryData.seoScore === 'InProgress')
          ) ? 'InProgress' : 'Completed';
      this.setSummarySubscription();
      ////console.log(this.summaryData);
    
      if (this.summaryData.speedIndex != "") {
        if (this.summaryData.speedIndex === "Error") {
          this.checkPerformance = true;
          this.preformanceLabel = "Error"
        }
        else if(this.summaryData.speedIndex === 'Scan Not Supported'){
          this.checkPerformance=true;
          this.preformanceLabel="Scan Not Supported"
        } 
        else if (this.summaryData.speedIndex === "InProgress") {
          this.checkPerformance = true;
          this.preformanceLabel = "In Progress..."
        } else {
          this.checkPerformance = false;
          this.setSpeedIndex(this.summaryData.speedIndex);
        }
      }
      if (this.summaryData.accessibility != "") {
        if (this.summaryData.accessibility === "Error") {
          this.checkAccessibility = true;
          this.accessibilityLabel = "Error"
        }
        else if(this.summaryData.accessibility === 'Scan Not Supported'){
          this.checkAccessibility=true;
          this.accessibilityLabel="Scan Not Supported"
        } 
        else if (this.summaryData.accessibility === "InProgress") {
          this.checkAccessibility = true;
          this.accessibilityLabel = "In Progress..."
        } else {
          this.checkAccessibility = false;
          this.summary.accessibility = this.summaryData.accessibility;
        }
      }
      if (this.summaryData.security != "") {
        if (this.summaryData.security === "Error") {
          this.checkSecurity = true;
          this.securityLabel = "Error"
        }
        else if(this.summaryData.security === 'Scan Not Supported'){
          this.checkSecurity=true;
          this.securityLabel="Scan Not Supported"
        } 
        else if (this.summaryData.security === "InProgress") {
          this.checkSecurity = true;
          this.securityLabel = "In Progress..."
        } else {
          this.checkSecurity = false;
          this.summary.security = this.summaryData.security;
        }
      }
    
      if (this.summaryData.omniChannel != "") {

        if (this.summaryData.omniChannel === "Error") {

          this.checkomnichannel = true;

          this.omnichannelLabel = "Error"

        }
        else if(this.summaryData.omniChannel === 'Scan Not Supported'){
          this.checkomnichannel=true;
          this.omnichannelLabel="Scan Not Supported"
        }
         else if (this.summaryData.omniChannel === "InProgress") {

          this.checkomnichannel = true;

          this.omnichannelLabel = "In Progress..."

        } else {

          this.checkomnichannel = false;

          this. setSpeedindexChartData(this.summaryData.omniChannel) 
          this.summary.omniChannel="done"
          //this.getOmnichannelData(this.summaryData.omniChannel);
        }

      }

      if (this.summaryData.seoScore != '') {

        if (this.summaryData.seoScore === 'Error') {

          this.checkseo = true;

          this.seoLabel = 'Error';

        
        } 
        else if(this.summaryData.seoScore === 'Scan Not Supported'){
          this.checkseo=true;
          this.seoLabel="Scan Not Supported"
        }
        else if (this.summaryData.seoScore === 'InProgress') {

          this.checkseo = true;

          this.seoLabel = 'In Progress...';

        } else {

          this.checkseo = false;

          this.getSeoScore(this.summaryData.seoScore)

        }

      }

      if (this.summaryData.activeMonitor != "") {

        if (this.summaryData.activeMonitor === "Error") {

          this.checkactivemonitor = true;

          this.activemonitorLabel = "Error"

        } 
        else if(this.summaryData.activeMonitor === 'Scan Not Supported'){
          this.checkactivemonitor=true;
          this.activemonitorLabel="Scan Not Supported"
        }
        else if (this.summaryData.activeMonitor === "InProgress") {

          this.checkactivemonitor = true;

          this.activemonitorLabel = "In Progress..."

        } else {

          this.checkactivemonitor = false;
          this.summary.activeMonitor="done"
         this.getActiveMonitorRegionAndLoadTime(this.summaryData.activeMonitor)
         //this.getActiveMonitorData(this.summaryData.activeMonitor)
        }

      }
      if (data.timestamp != "") {
        this.setLastRun(this.summaryData.timestamp);
      }
      this.loadingflag=false;

      this.changeDetectorRefs.markForCheck();

     }
  }
    );
  }
  setSummarySubscription() {
    if (!this.summarySubscribed && this.summaryStatus !== 'Completed') {
      this.summarySubscription = interval(this.subscriptionInterval).subscribe((val) => {
        if (this.summaryStatus === 'Completed') {
          this.summarySubscription.unsubscribe();
        } else {
          this.getSummaryData();
        }
      });
      this.summarySubscribed = true;
    }
  }
  
  setSpeedIndex(speedIndex) {
    let si = parseFloat(speedIndex.split(" ")[0])
    if (si >= 0 && si < 3.4) {
      this.summary.speedIndex = "Good"
    } else if (si >= 3.4 && si <5.8) {
      this.summary.speedIndex = "Average"
    } else {
      this.summary.speedIndex = "Slow"
    }
  }
  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    ////console.log(dec.tz('Europe/London').format('MM-DD-YYYY HH:mm:ss'));
    this.lastRun = dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }


  ngOnInit() {
   

    setInterval(() => {
      this.currenttime=new Date()
      let dec1 = moment(this.currenttime, 'HH:mm:ss').subtract(1, 'seconds');
      this.currenttime= dec1.tz('Europe/London').format('HH:mm:ss');
      // //console.log("currenttime"+this.currenttime)
    }, 1000);

  }

  ngOnDestroy(): void {
    if (this.summarySubscription) {
      this.summarySubscription.unsubscribe();
    }
  }


  
  setSpeedindexChartData(data){
    let browsers: string[] = [];
    let loadTimes: number[] = [];

    data.forEach(item => {
      browsers.push(item.Browser);
      loadTimes.push(item.loadTime);
    });
 
    let chartData = browsers.map((browser, index) => {
      let label = browser === "Safari" ? "Mac Os 13-" + browser : "Win 11-" + browser;
        return {
          "label": label,
         "value": loadTimes[index],
         "tooltext": "$value ms"        
        }
      });

    this.barchartdata={
      chart: {
        yaxisname: "Time(ms)",
        xaxisname:"OS - Browser",
        divLineColor : "#ffffff",
        divLineAlpha : "0",
        decimals: "0",
        theme: "fusion",
        bgColor:"#ffffff",
        labelFontSize: '9',
        xAxisNameFontSize: '9',
        yAxisNameFontSize: '9',
        showYAxisValues:"0",
        // showXAxisValues:"0",
        showLabels:"0",
        xaxislabel:"0",
        FontSize: '8',
        plotSpacePercent: "70",
        // barWidth: "2",
        chartLeftMargin :"10",
        chartRightMargin :"0",
        chartTopMargin : "0",
        chartBottomMargin : "0",
      },
      data:chartData
    };
  }


  

  getActiveMonitorRegionAndLoadTime(data) {
if(data!=null){
    data.map(items => {
      let a = items.LoadTimeAndTimeStamp;
      a.map(time => 
        {
          
          let dec = moment(time.timeStamp);
          this.starttime = dec.tz('Europe/London').format('HH:mm:ss');
         

          let dec1 = moment(time.timeStamp).tz('Europe/London');
          this.time= dec1.add(4, 'hours');
          this.endtime= this.time.tz('Europe/London').format('HH:mm:ss');
        
          // //console.log("currenttime"+this.starttime)
          // //console.log("currenttime"+this.endtime)
          
        });
})



    this.linechartdata={
        "chart": {
          xAxisName: "Minutes",
          yaxisName:"Time(ms)",
          divLineColor : "#ffffff",
          divLineAlpha : "0",
          decimals: "0",
          theme: "fusion",
          bgColor:"#ffffff",
          labelFontSize: '9',
          xAxisNameFontSize: '9',
          yAxisNameFontSize: '9',
          showYAxisValues:"0",
          showLabels:"0",
          xaxislabel:"0",
          FontSize: '8',
          plotSpacePercent: "70",
          chartLeftMargin :"10",
          chartRightMargin :"0",
          chartTopMargin : "0",
          chartBottomMargin : "0",
          showLegend:"0",
          drawAnchors:"0",
        },
        "categories": data.map(items=>{
          let a = items.LoadTimeAndTimeStamp;
          return{
              //  "label":loadtime.Interval.toString()
             "category": a.map(time=>{
                let timestamp = new Date(time.timeStamp);
                let hours = timestamp.getHours();
                let minutes = timestamp.getMinutes();
                let label = hours + ":" + minutes;
                return {
                  "label":label
                };
            })
            
          }  
          }),
          "dataset": data.map(items=>{
            let a = items.LoadTimeAndTimeStamp;
            return{"seriesname":items.Region,
            
            "data": a.map(loadtime=>{
               return{
                "value":loadtime.loadTime
               }
              })
            } 
          }),

      }
      // //console.log("linechart"+JSON.stringify(this.linechartdata));
  
  }
  }
  getSeoScore(seoScore){

    // let seoscore = parseFloat(seoScore.split(' ')[0]);

    let seoscore = seoScore;

    if (seoscore >= 0.90 && seoscore <= 1) {

      this.summary.seoScore = 'Good';

    } else if (seoscore > 0.50 && seoscore <= 0.89) {

      this.summary.seoScore = 'Average';

    } else {

      this.summary.seoScore = 'Poor';

    }

  }
}

