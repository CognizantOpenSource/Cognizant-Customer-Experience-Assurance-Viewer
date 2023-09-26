import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog} from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router, ActivatedRoute } from '@angular/router';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { interval, Subscription } from 'rxjs';
import * as moment from 'moment';


export interface PeriodicElement {
  Guideline: string;
  Description: string;
  Category: string;
  Status: string;
  NumberofOccurences: number;
}

@Component({
  selector: 'leap-accessibility',
  templateUrl: './accessibility.component.html',
  styleUrls: ['./accessibility.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})

export class AccessibilityComponent implements OnInit, OnDestroy {
  @Input() pageSize: any = 3;
  @Input() top: any = "true";
  accessibilityDataIndex: any = null;
  imageObject: any = [];
  dtguideline: string;
  dtdesc: string;
  dtcategory: string;
  dtstatus: string;
  dtnoofoccurance: string;
  totalviolationval = "";
  filterValues: any = {};
  filterSelectObj: any[] = [];

  columnsToDisplay: string[] = ['Category','Guideline','Title', 'Description', 'Status', 'NumberofOccurences', 'DisabilityType'];

  projectId = "";
  dataSource;
  multi: any = [
    {
      name: "",
      series: []
    },
  ];
  piedata: any;
  checkPieChart = false;
  columndata: any = [
    {
      name: "",
      value: ""
    }
  ];
  tabledata: any = []
  accessibilityMessage = "";
  checkaccessibilityError = false;
  checknodeselector = false;
  rtTooltip = "The First Contentful Paint (FCP): metric measures the time from when the page starts loading to when any part of the page's content is rendered on the screen.&#13;The Largest Contentful Paint (LCP): metric reports the render time of the largest image or text block visible within the viewport, relative to when the page first started loading.&#13;Time To Interactive(TTI): measures how long it takes a page to become fully interactive.";
  speedIndexTooltip = "Speed Index: measures how quickly content is visually displayed during page load.";
  accessibilityData: any = null;
  lastRun: any = null;
  url: any = null;
  expandedElement: PeriodicElement | null;
  accessibilitySubscription: Subscription;
  accessibilityStatus = '';
  accessibilitySubscribed = false;
  subscriptionInterval = 10000;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  constructor(private cxadashboardServices: CxadashboardServicesService,
    private changeDetectorRefs: ChangeDetectorRef, private router: Router, private route: ActivatedRoute,
    public dialog: MatDialog) {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    this.filterSelectObj = [
      {
        name: 'Status',
        columnProp: 'Status',
        options: []
      }
    ];
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.getAccessibilityData();
  }
  getAccessibilityData() {
    this.cxadashboardServices.getAccessibility(this.projectId).subscribe(data => {
      if (data === null || data === undefined || data === "") {
        this.checkaccessibilityError = true;
        this.accessibilityMessage = "Scan not done";
        this.changeDetectorRefs.markForCheck();
      } else {
        this.accessibilityData = data;
        this.accessibilityStatus = this.accessibilityData.status ? this.accessibilityData.status : ''
        this.setaccessibilitySubscription();
        this.lastRun = this.accessibilityData.timestamp ? this.setLastRun(this.accessibilityData.timestamp) : null;
        this.url = this.accessibilityData.url ? this.accessibilityData.url : null;
        if (data.error != undefined || data.error != null || data.error != "" || data.error == 'true') {
          if (data.error) {
            this.checkaccessibilityError = true;
            this.accessibilityMessage = "Error Occurred in Execution";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.accessibilityDataIndex = this.accessibilityData.length;
            this.totalviolationval = JSON.stringify(data.totalViolation);
            this.setResponseTimeChart(data);
            this.setResponsePieChart(data);
            this.setResponseColumnChart(data);
            this.setResponseTableData(data);
            this.dataSource.filterPredicate = this.createFilter();
            this.filterSelectObj.filter((o: any) => {
              o.options = this.getFilterObject(this.tabledata, o.columnProp);
            });
            this.changeDetectorRefs.markForCheck();
          }
        }
      }
    });
  }
  setaccessibilitySubscription() {
    if (!this.accessibilitySubscribed && !(this.accessibilityStatus === 'Completed'
      || this.accessibilityStatus === 'InComplete')) {
      this.accessibilitySubscription = interval(this.subscriptionInterval).subscribe((val) => {
        if (this.accessibilityStatus === 'Completed' || this.accessibilityStatus === 'InComplete') {
          this.accessibilitySubscription.unsubscribe();
        } else {
          this.getAccessibilityData();
        }
      });
      this.accessibilitySubscribed = true;
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
  ngOnInit() {
  }
  getFilterObject(fullObj, key) {
    const uniqChk: String[] = [];
    fullObj.filter(obj => {
      if (!uniqChk.includes(obj[key])) {
        uniqChk.push(obj[key]);
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
  resetFilters(input) {
    this.filterValues = {};
    this.filterSelectObj.forEach((value: any, key) => {
      value.modelValue = '';
    });
    this.dataSource.filter = '';
    input.value = '';
  }
  setResponseTimeChart(accessibilityData) {

    let seriesAPassVal = accessibilityData.guidelineObject.aPassguideline;

    let seriesAFailVal = accessibilityData.guidelineObject.aFailGuideline;

    let seriesANA = accessibilityData.guidelineObject.aNAGuideline;

    let seriesAAPassVal = accessibilityData.guidelineObject.aaPassguideline;

    let seriesAAFailVal = accessibilityData.guidelineObject.aaFailGuideline;

    let seriesAANA = accessibilityData.guidelineObject.aaNAGuideline;

    let seriesAMan=accessibilityData.guidelineObject.manualGuidelinesForLevelA==null?14:accessibilityData.guidelineObject.manualGuidelinesForLevelA.length;

    let seriesAAMan=accessibilityData.guidelineObject.manualGuidelinesForLevelAA==null ? 15:accessibilityData.guidelineObject.manualGuidelinesForLevelAA.length;

    let category: any[] = []

    let passData: any[] = [];

    let failData: any[] = [];

    let naData: any[] = [];

    let manData: any[] = [];

    category.push({ "label": "A" }, { "label": "AA" })

    passData.push({ "value": seriesAPassVal + "" }, { "value": seriesAAPassVal + "" })

    failData.push({ "value": seriesAFailVal + "" }, { "value": seriesAAFailVal + "" })

    naData.push({ "value": seriesANA + "" }, { "value": seriesAANA + "" })

    manData.push({ "value": seriesAMan + "" }, { "value": seriesAAMan + "" })

    var obj = [

      {

        name: "A",

        series: [

          {

            name: "Pass",

            value: seriesAPassVal

          },

          {

            name: "Fail",

            value: seriesAFailVal

          },

          {

            name: "Not Available",

            value: seriesANA

          },

          {

            name: "Manual Intervention Needed",

            value: seriesAMan

          }

        ],

      },

      {

        name: "AA",

        series: [

          {

            name: "Pass",

            value: seriesAAPassVal

          },

          {

            name: "Fail",

            value: seriesAAFailVal

          },

          {

            name: "Not Available",

            value: seriesAANA

          },

          {

            name: "Manual Intervention Needed",

            value: seriesAAMan

          }

        ],

      }

    ];

   

    this.multi = obj;

    this.changeDetectorRefs.markForCheck();



  }

  setResponsePieChart(accessibilityData) {
    let highval = accessibilityData.guidelineObject.highSeverityGuideline;
    let mediumval = accessibilityData.guidelineObject.mediumSeverityGuideline;
    let lowval = accessibilityData.guidelineObject.lowSeverityGuideline;
    if (highval === 0 && mediumval == 0 && lowval == 0) {
      this.checkPieChart = true;
      this.changeDetectorRefs.markForCheck();
    } else {
      var piearray = [
        {
          name: "High",
          value: highval
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
     
 
        this.piedata = piearray;
    
      this.changeDetectorRefs.markForCheck();
    }
  }
  
  setResponseColumnChart(accessibilityData) {
    let visualImpairmentval = accessibilityData.disabilityType.visualImpairment;
    let mobilityval = accessibilityData.disabilityType.mobility;
    let auditoryval = accessibilityData.disabilityType.auditory;
    let cognitiveval = accessibilityData.disabilityType.cognitive;
    var columnarray = [
      {
        name: "Visual Impairment",
        value: visualImpairmentval
      },
      {
        name: "Mobility",
        value: mobilityval
      },
      {
        name: "Auditory",
        value: auditoryval
      },
      {
        name: "Cognitive",
        value: cognitiveval
      }
    ];
    this.columndata = columnarray;
    this.changeDetectorRefs.markForCheck();
  }
  setResponseTableData(accessibilityData) {
    let obj;
    accessibilityData.audits.forEach((element, index) => {
      let occurrence = "";
      if (element["occurrence"] == 0) {
        occurrence = 'NA';
      } else {
        occurrence = element["occurrence"];
      }
      if (element.node == undefined || element.node == 'undefined') {
        this.checknodeselector = true;
      }
      else {
        this.checknodeselector = false;
      }
      obj = {
        Guideline: element.guideline,
        Title:element.title,
        Description: element.description,
        Category: element.category,
        Status: element.status,
        NumberofOccurences: occurrence,
        node: element.node,
        checknodeselector: this.checknodeselector,
        Issues: [],
        DisabilityType: element.disabilityType
      };
      if (!this.checknodeselector) {
        let List: any = [];
        let obj1: any = [];
        let resultset: any = [];
        let Issue: any;
        let resultsetobj;
        for (let i = 0; i < element.node.selector.length; i++) {
          obj.Issues.push({
            selector: element.node.selector[i],
            label: element.node.label[i],
            source: element.node.path[i],
            element: element.node.element[i]
          });
        }
      }
      this.tabledata.push(obj);
    });

    this.dataSource = new MatTableDataSource(this.tabledata);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }
  filterChange(filter, event) {
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
  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }
  ngOnDestroy(): void {
    if (this.accessibilitySubscription) {
      this.accessibilitySubscription.unsubscribe();
    }
  }
}

