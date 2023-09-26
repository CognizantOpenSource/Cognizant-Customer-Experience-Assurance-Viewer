import { ChangeDetectorRef, Component, Inject, Input, OnChanges, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog, } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router, ActivatedRoute } from '@angular/router';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import * as moment from 'moment';
import { interval, Subscription } from 'rxjs';


export interface PeriodicElement {
  javascriptlib: string;
  severity: string;
  noofsuchissue: number;
  url: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  { javascriptlib: 'Highcharts@8.2.0', severity: 'Medium', noofsuchissue: 3, url: 'https://snyk.io/vuln/npm:highcharts?lh=8.2.0&utm_source=lighthouse&utm_medium=ref&utm_campaign=audit' },
  { javascriptlib: 'Highcharts@6.2.0', severity: 'Low', noofsuchissue: 1, url: 'https://snyk.io/vuln/npm:highcharts?lh=8.2.0&utm_source=lighthouse&utm_medium=ref&utm_campaign=audit' },
];

@Component({
  selector: 'leap-security',
  templateUrl: './security.component.html',
  styleUrls: ['./security.component.scss']
})
export class SecurityComponent implements OnInit, OnDestroy {
  @Input() pageSize: any = 3;
  @Input() top: any = "true";
  speedIndex: any = null;
  imageObject: any = [];

  dtguideline: string;
  dtdesc: string;
  dtcategory: string;
  dtstatus: string;
  dtnoofoccurance: string;
  filterValues = {};
  filterSelectObj: any[] = [];
  vulnerabilityFilterSelectObj = [
    {
      name: 'Severity',
      columnProp: 'severity',
      options: [],
      modelValue: ''
    }
  ];
  passiveScanFilterSelectObj = [
    {
      name: 'Status',
      columnProp: 'Status',
      options: [],
      modelValue: ''
    }
  ];
  displayedColumns: string[];
  passiveScanDisplayedColumn: string[] = ['VulnerabilityCheck', 'Description', 'Status', 'Severity', 'Exploitable', 'Impact', 'Recommendation'];
  passiveScanTableHeadings: any = {
    VulnerabilityCheck: "Vulnerability Check",
    Description: 'Description',
    Status: "Status",
    Severity: 'Severity',
    Exploitable: 'Exploitable',
    Impact: 'Impact',
    Recommendation: 'Recommendation'
  };
  jsVulnerabilityDisplayedColumn: string[] = ['javascriptlib', 'severity', 'noofsuchissue', 'url'];
  jsVulnerabilityTableHeadings: any = {
    javascriptlib: 'JavaScript Library',
    severity: 'Severity',
    noofsuchissue: 'No of such Instances',
    url: 'Additional Details'
  };
  securityFilterSelectObj = [
    { name: 'JS Vulnerability', value: 'jsVulnerability' },
    { name: 'Passive Scan', value: 'passiveScan' }
  ];
  selectedSecurityFilter: string = 'passiveScan';
  selectedTableHeadings: any = {};
  tableData: any[] = [];


  projectId = "";
  totaljsvulnerabilityval = "";
  multi: any = [
    {
      name: "",
      series: []
    },
  ];
  securitypiedata: any;
  passiveScanPieData: any = [];
  securitytabledata: any = []
  barchartdata: any;
  legendData: any = []
  dataSource;
  SecurityMessage = "";
  checkSecurityError = false;
  rtTooltip = "The First Contentful Paint (FCP): metric measures the time from when the page starts loading to when any part of the page's content is rendered on the screen.&#13;The Largest Contentful Paint (LCP): metric reports the render time of the largest image or text block visible within the viewport, relative to when the page first started loading.&#13;Time To Interactive(TTI): measures how long it takes a page to become fully interactive.";
  speedIndexTooltip = "Speed Index: measures how quickly content is visually displayed during page load.";
  securityData: any = null;
  lastRun: any = null;
  url: any = null;
  securitySubscription: Subscription;
  securityStatus = '';
  securitySubscribed = false;
  subscriptionInterval = 10000;

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;

  constructor(private cxadashboardServices: CxadashboardServicesService,
    private changeDetectorRefs: ChangeDetectorRef, private router: Router, private route: ActivatedRoute,
    public dialog: MatDialog) { }

  ngOnInit() {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.getSecurityData();
    this.selectedSecurityFilter = 'passiveScan';
    this.displayedColumns = (this.passiveScanDisplayedColumn);
    this.selectedTableHeadings = (this.passiveScanTableHeadings);
  }
  getSecurityData() {
    this.cxadashboardServices.getSecurity(this.projectId).subscribe(data => {
      if (data === null || data === undefined || data === "") {
        this.checkSecurityError = true;
        this.SecurityMessage = "Scan not done";
        this.changeDetectorRefs.markForCheck();
      } else {

        this.securityData = data;
        this.securityStatus = this.securityData.Status ? this.securityData.Status : ''
        if (data.error != undefined || data.error != null || data.error != "" || data.error == 'true') {
          if (data.error) {
            this.checkSecurityError = true;
            this.SecurityMessage = "Scan Not Supported";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.tableData = (data);
            this.lastRun = this.securityData.timestamp ? this.setLastRun(this.securityData.timestamp) : null;
            this.url = this.securityData.url ? this.securityData.url : null;
            this.totaljsvulnerabilityval = JSON.stringify(data.jsVulnerability.totalVulnerability);
            this.setResponsePieChart(data);
            this.getOwaspData(data.executionId);
            this.setPassiveScanPieChart(data);
            this.setPassiveScanChartData(data);
            this.filterSelectObj = (this.passiveScanFilterSelectObj);
            this.changeDetectorRefs.markForCheck();
          }
        }
      }
    });
  }
  getOwaspData(executionId) {
    this.cxadashboardServices.getSecurityOwaspData(executionId).subscribe(data => {
      if (data === null || data === undefined || data === "") {
      } else {
        let category: any[] = []
        let passData: any[] = [];
        let failData: any[] = [];
        var obj: any[] = []
        data.map(item => {
          let label = { "label": item.id }
          category.push(label);
          let pass = {
            value: item.pass && (item.pass > 0) ? item.pass + "" : ""
          }
          passData.push(pass)
          let fail = { "value": item.fail && (item.fail > 0) ? item.fail + "" : "" }
          failData.push(fail)
          let series = {
            name: item.id,
            series: [{
              name: "Pass",
              value: item.pass && (item.pass > 0) ? item.pass : 0
            }, {
              name: "Fail",
              value: item.fail && (item.fail > 0) ? item.fail : 0
            },
            ]
          };
          this.legendData.push(item.id + " - " + item.name)
          obj.push(series)
        })
       
      
          this.barchartdata = obj
        
        this.changeDetectorRefs.markForCheck();
      }
    })
  }
  setSecuritySubscription() {
    if (!this.securitySubscribed && !(this.securityStatus === 'Completed'
      || this.securityStatus === 'InComplete')) {
      this.securitySubscription = interval(this.subscriptionInterval).subscribe((val) => {
        if (this.securityStatus === 'Completed' || this.securityStatus === 'InComplete') {
          this.securitySubscription.unsubscribe();
        } else {
          this.getSecurityData();
        }
      });
      this.securitySubscribed = true;
    }
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
  setPassiveScanChartData(securityData) {
    this.securitytabledata = [];
    securityData.passiveScan.securityChecks.forEach((element, index) => {
      const obj = {
        VulnerabilityCheck: element.Vulnerability,
        Description: element.Description,
        Status: element.Status,
        Severity: element.Severity,
        Exploitable: element.Exploitable,
        Impact: element.Impact,
        Recommendation: element.Recommendation,
        url: element.Url ? element.Url : ""
      };
      this.securitytabledata.push(obj);
    });
    this.dataSource = new MatTableDataSource(this.securitytabledata);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.createFilter();
    this.passiveScanFilterSelectObj.filter((o: any) => {
      o.options = this.getFilterObject(this.securitytabledata, o.columnProp);
    });
  }
  securityFilterChange(event) {
    if (event.target.value.trim() === 'jsVulnerability') {
      this.displayedColumns = (this.jsVulnerabilityDisplayedColumn);
      this.selectedTableHeadings = (this.jsVulnerabilityTableHeadings);
      this.setResponseTableData(this.tableData);
      this.filterSelectObj = (this.vulnerabilityFilterSelectObj);
      this.resetFilters();
    } else if (event.target.value.trim() === 'passiveScan') {
      this.displayedColumns = (this.passiveScanDisplayedColumn);
      this.selectedTableHeadings = (this.passiveScanTableHeadings);
      this.setPassiveScanChartData(this.tableData);
      this.filterSelectObj = (this.passiveScanFilterSelectObj);
      this.resetFilters();
    }
  }
  setPassiveScanPieChart(securityData) {
    let highval = securityData.passiveScan.totalPS;
    let mediumval = securityData.passiveScan.passCount;
    let lowval = securityData.passiveScan.failCount;
    var passivepiearray = [
      {
        name: "Pass count",
        value: mediumval
      },
      {
        name: "Fail Count",
        value: lowval
      }
    ];
    this.passiveScanPieData = passivepiearray
  }
  setResponsePieChart(securityData) {
    let highval = 0;
    let mediumval = 0;
    let lowval = 0;
    securityData.passiveScan.securityChecks.map(item => {
      if (item.Status == "FAIL") {
        if (item.Severity == "Critical") {
          highval++
        }
        if (item.Severity == "Medium") {
          mediumval++
        }
        if (item.Severity == "Low") {
          lowval++
        }
      }
    });
    var securitypiearray = [
      {
        name: "Critical",
        value: highval
      },
      {
        name: "High",
        value: 0
      },
      {
        name: "Med",
        value: mediumval
      },
      {
        name: "Low",
        value: lowval
      }
    ];
  
 this.securitypiedata = securitypiearray 
    this.changeDetectorRefs.markForCheck();

  }
  setResponseTableData(securityData) {
    this.securitytabledata = [];
    securityData.jsVulnerability.security.forEach((element, index) => {
      const obj = {
        javascriptlib: element.jsLibrary,
        severity: element.severity,
        noofsuchissue: element.issueNo,
        url: element.url,
      };
      this.securitytabledata.push(obj);
    });
    this.dataSource = new MatTableDataSource(this.securitytabledata);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate = this.createFilter();
    this.vulnerabilityFilterSelectObj.filter((o: any) => {
      o.options = this.getFilterObject(this.securitytabledata, o.columnProp);
    });
  }
  getFilterObject(fullObj, key) {
    const uniqChk: any[] = [];
    fullObj.filter((obj: any) => {
      if (!uniqChk.includes(obj[key].toUpperCase())) {
        uniqChk.push(obj[key].toUpperCase());
      }
      return obj;
    });
    return uniqChk;
  }
  createFilter() {
    let filterFunction = function (data: any, filter: string): boolean {
      let searchTerms = JSON.parse(filter);
      let isFilterSet = false;
      for (const col in searchTerms) {
        if (searchTerms[col].toString() !== '') {
          isFilterSet = true;
        } else {
          delete searchTerms[col];
        }
      }


      let nameSearch = () => {
        let found = false;
        if (isFilterSet) {
          for (const col in searchTerms) {
            searchTerms[col]
              .trim()
              .toLowerCase()
              .split(' ')
              .forEach(word => {
                if (
                  data[col]
                    .toString()
                    .toLowerCase()
                    .indexOf(word) != -1 &&
                  isFilterSet
                ) {
                  found = true;
                }
              });
          }
          return found;
        } else {
          return true;
        }
      };
      return nameSearch();
    };
    return filterFunction;
  }

  resetFilters() {
    this.filterValues = {};
    this.filterSelectObj.filter(o => {
      o.modelValue = '';
    });
    this.dataSource.filter = '';
  }
  filterChange(filter, event) {
    this.filterValues = {};
    this.filterValues[
      filter.columnProp
    ] = event.target.value.trim().toLowerCase();
    this.dataSource.filter = JSON.stringify(this.filterValues);
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }

  }
  setResponseTimeChart(perfomanceData) {
    let obj = {
      name: "First Contentful Paint",
      value: Math.trunc(Number(perfomanceData.firstContentfulPaint.replace("s", "")) * 1000)
    }
    this.multi[0].series.push(obj);

    let interactive = Number(perfomanceData.interactive.replace("s", ""));
    interactive = Math.trunc(interactive * 1000);
    let lcp = Number((perfomanceData.largestContentfulPaint.replace("s", "")).replace(/ /g, ""));
    lcp = Math.trunc(lcp * 1000);
    obj = {
      name: "Time Taken For Interactive",
      value: interactive
    }
    let obj2 = {
      name: "Largest Contentful Paint",
      value: lcp
    }
    if (interactive > lcp) {
      this.multi[0].series.push(obj2);
      this.multi[0].series.push(obj);
    } else {
      this.multi[0].series.push(obj);
      this.multi[0].series.push(obj2);
    }
    const newObj = this.multi;
    this.multi = JSON.parse(JSON.stringify(newObj));
    this.changeDetectorRefs.markForCheck();

  }

  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }
  ngOnDestroy(): void {
    if (this.securitySubscription) {
      this.securitySubscription.unsubscribe();
    }
  }
}

