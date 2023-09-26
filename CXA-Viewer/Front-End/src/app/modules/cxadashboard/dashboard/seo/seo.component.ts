import { ChangeDetectorRef, Component, Inject, OnInit, OnDestroy, Input, ViewChild } from '@angular/core';
import { CxadashboardServicesService } from '../../services/cxadashboard-services.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { interval, Subscription } from 'rxjs';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import * as moment from 'moment';

export interface PeriodicElement {
  Category: String;
  GuideLine: String;
  Description: String;
  Status: String;
  NoofFailedElements: String;


}
const ELEMENT_DATA: PeriodicElement[] = [];

@Component({
  selector: 'app-seo',
  templateUrl: './seo.component.html',
  styleUrls: ['./seo.component.css']
})

export class SeoComponent implements OnInit {

  @Input() pageSize: any = 3;
  @Input() top: any = "true";
  tabledata: any = [];
  score: any;
  imageObject: any = [];
  projectId = "";
  multi: any = [
    {
      name: "",
      series: []
    },
  ];
  seoMessage = "";
  checkSeoError = false;
  rtTooltip = "The First Contentful Paint (FCP): metric measures the time from when the page starts loading to when any part of the page's content is rendered on the screen.&#13;The Largest Contentful Paint (LCP): metric reports the render time of the largest image or text block visible within the viewport, relative to when the page first started loading.&#13;Time To Interactive(TTI): measures how long it takes a page to become fully interactive.";
  speedIndexTooltip = "Speed Index: measures how quickly content is visually displayed during page load.";
  seoData: any = null;
  lastRun: any = null;
  url: any = null;
  seoSubscription: Subscription;
  seoStatus = '';
  seoSubscribed = false;
  subscriptionInterval = 10000;
  bardata: any;
  filterValues: any = {};

  filterSelectObj: any[] = [];
  expandedElement: PeriodicElement | null
  columnsToDisplay = ['Category', 'GuideLine', 'Description', 'Status', 'NoofFailedElements'];

  dataSource;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  constructor(private cxadashboardServices: CxadashboardServicesService,
    private changeDetectorRefs: ChangeDetectorRef, private router: Router, private route: ActivatedRoute,
    public dialog: MatDialog) {
    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }
    this.getSeoData();
    this.filterSelectObj = [

      {

        name: 'Status',

        columnProp: 'Status',

        options: []

      }

    ];
  }

  setSeoSubscription() {
    if (!this.seoSubscribed && !(this.seoStatus === 'Completed'
      || this.seoStatus === 'InComplete')) {
      this.seoSubscription = interval(this.subscriptionInterval).subscribe((val) => {
        if (this.seoStatus === 'Completed' || this.seoStatus === 'InComplete') {
          this.seoSubscription.unsubscribe();
        } else {
          this.getSeoData();
        }
      });
      this.seoSubscribed = true;
    }
  }
  getSeoData() {
    this.cxadashboardServices.getSeoDetails(this.projectId).subscribe(data => {
      if (data === null || data === undefined || data === "") {
        this.seoMessage = "Scan not done";
        this.checkSeoError = true;
        this.changeDetectorRefs.markForCheck();
      } else {
        this.seoData = data;
        this.seoStatus = this.seoData.status ? this.seoData.status : ''
        this.setSeoSubscription();
        this.Seostackedbarchart(data);
        this.setTableData(data)
        this.lastRun = this.seoData.timestamp ? this.setLastRun(this.seoData.timestamp) : null;
        this.url = this.seoData.url ? this.seoData.url : null;
        if (data.error != undefined || data.error != null || data.error != "") {
          if (data.error) {
            this.checkSeoError = true;
            this.seoMessage = "Error Occurred in Execution";
            this.changeDetectorRefs.markForCheck();
          } else {
            this.score = parseFloat(this.seoData.score) * 100;

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

  Seostackedbarchart(data) {
  
    
      this.bardata = [{
        name: "Pass",
        value: data.passScore
      },
      {
        name: "Fail",
        value: data.failScore
      },
      {
        name: "Not Applicable",
        value: data.naScore
      }
      ]
    
  }



  setTableData(data) {

    let element;
    //console.log("DATA" + JSON.stringify(data));
    let b = data.audits.forEach(item => {
      let occurrence = "";
      if (item["occurrence"] == 0) {
        occurrence = 'NA';
      } else {
        occurrence = item["occurrence"];
      }

      element = {
        Category: item.seoGroup,
        GuideLine: item.id,
        Description: item.description,
        Status: item.status,
        NoofFailedElements: occurrence,
      }
      this.tabledata.push(element)

    })
    this.dataSource = new MatTableDataSource(this.tabledata);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    //console.log(b);
    //console.log(JSON.stringify(b));
  }
  ngOnInit() {

  }

  onInfoClick() {
    //console.log("info click")
    this.dialog.open(SpeedIndexModal, {
    });
  }

  setLastRun(timeStamp) {
    let dec = moment(timeStamp);
    return dec.tz('Europe/London').format('DD-MMM-YYYY HH:mm:ss') + " BST";
  }
  ngOnDestroy(): void {
    if (this.seoSubscription) {
      this.seoSubscription.unsubscribe();
    }
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
}

export class SpeedIndexModal {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}


