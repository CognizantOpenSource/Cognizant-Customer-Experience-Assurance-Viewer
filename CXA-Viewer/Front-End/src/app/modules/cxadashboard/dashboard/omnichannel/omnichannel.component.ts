import { ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import { MatDialog} from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import * as moment from 'moment';
import { some, isEmpty } from 'lodash'

@Component({
  selector: 'Omnichannel',
  templateUrl: './omnichannel.component.html',
  styleUrls: ['./omnichannel.component.scss'],

})

export class OmnichannelComponent implements OnInit {
  @Input() top: any = "true";
  myChart: any;
  projectId = "";
  checkOmnichannelError = false;
  OmnichannelMessage = "";
  barchartdata: any;
  omnichannelData: any = null;
  browserName: " ";
  screenshotPath = {};
  ImageSafari: any;
  securityStatus = '';
  version = {};
  lastRun: any = null;
  url: any = null;
  browsers: string[] = [];
  showhttp = false;
  httpcodevalues: any = [];
  categorys:any=[];
  httpResponseCodes=new Set();
  filteredData: any;
  httpcodebarchartdata: any;
  httpcodebarchartdataValue:any=[];
  httpLastdataValue=new Map();
  stackedBarChartData:any=[];

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
      this.getOmnichannelData();
    }


  }


  getOmnichannelData() {

    this.cxadashboardServices.getOmnichannel(this.projectId).subscribe(data => {

      if (data === null || data === undefined || data === "" || data.length <= 0 || !some(data, isEmpty)) {
        this.checkOmnichannelError = true;
        this.OmnichannelMessage = "Scan not done";
        this.changeDetectorRefs.markForCheck();
      } else {
        this.omnichannelData = data;
        if (data.error !== undefined || data.error !== null || data.error !== "" || data.error == 'true') {
          if (data.error) {
            this.checkOmnichannelError = true;
            this.OmnichannelMessage = "Error Occurred in Execution";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.omnichannelData = data;
            this.lastRun = this.omnichannelData.timestamp ? this.setLastRun(this.omnichannelData.timestamp) : null;
            this.url = this.omnichannelData.url ? this.omnichannelData.url : null;
            this.setSpeedindexChartData(data.omniResponse);
          }
        }
      }

    })
  }
  setLastRun(timeStamp) {

    let dec = moment(timeStamp);
    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";

  }
  setSpeedindexChartData(data) {

    let loadTimes: number[] = [];

    let browsersc = data.map(item => item.screenShotPath);


    data.forEach(item => {
      this.browsers.push(item.Browser);
      loadTimes.push(item.loadTime);
    });
    const BrowserIsSafari = data.some(browser => browser.Browser == "Safari");

    const getsafari = document.getElementById("safari-div");

    if (BrowserIsSafari && getsafari) {

      getsafari.style.display = "Block";

    }
    let chartData = this.browsers.map((browser, index) => {
      let label = browser === "Safari" ? "MacOs - " + browser : "Win - " + browser;
      return {
        label: label,
        value: loadTimes[index],
        tooltext: "$value ms"
      }
    });


    
   
   
      let barchartData: any = [];
      chartData.map(data => {
        let obj = {
          name: data.label,
          value: data.value
        }
        barchartData.push(obj);
      })
      this.barchartdata = barchartData
    
    this.changeDetectorRefs.markForCheck();
    let httpcodebarchartdata = data.map((response) => {

      {

        if(response.httpCode!='null'){
        let valuesss= Object.entries(response.httpCode).map(

          (([label, value])=>(!this.httpResponseCodes.has(label)?this.httpLastdataValue.set(label,[value]):(this.httpLastdataValue.set(label,[...this.httpLastdataValue.get(label),value])),this.httpResponseCodes.add(label)))
          
        );
        this.categorys.push({label:response.Browser})
        
        //console.log(this.categorys,this.httpResponseCodes);

        if (this.httpResponseCodes.size > 0) {

          this.showhttp = true;
          

        }
      
      let obj=
        {
          name:response.Browser,
          series:Object.entries(response.httpCode).map(([seriesname,values])=>{return {name:seriesname,value:values}})
        }
      
      this.stackedBarChartData.push(obj)
    }
      this.filteredData = this.httpcodevalues.filter((item) => item !== null);




        return this.filteredData;

      }

      

    });
   
    let val:any=[]
    let values=this.httpLastdataValue.forEach(function(this:any,value, key) {(
      val=value.map((v)=>{return {value:v}}),
      this.httpcodebarchartdataValue.push({seriesname:key,data:val})
      
    )}.bind(this))
    //console.log(this.httpcodebarchartdata)
    data.map(item => {
      this.screenshotPath[item.Browser] = item.screenShotPath;
    })
    data.map(item => {
      this.version[item.Browser] = item.version;
    });

  }

}

