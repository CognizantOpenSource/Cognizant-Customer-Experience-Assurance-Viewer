import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CxadashboardComponent } from './cxadashboard.component';
import { CxadashboardSidenavComponent } from './cxadashboard-sidenav/cxadashboard-sidenav.component';
import { CxadashboardRoutingModule } from './cxadashboard-routing.module';
import { CxaSuiteComponent } from './cxa-suite/cxa-suite.component';
import { SummaryComponent } from './dashboard/summary/summary.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { TopNavComponent } from './top-nav/top-nav.component';
import { CxaSuiteDialogComponent } from './cxa-suite-dialog/cxa-suite-dialog.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MaterialModule } from '../material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PerformanceComponent } from './dashboard/performance/performance.component';
import { AccessibilityComponent } from './dashboard/accessibility/accessibility.component';
import { SecurityComponent } from './dashboard/security/security.component';


import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { ImageSliderComponent } from './dashboard/performance/image-slider/image-slider.component';
import { NgxGaugeModule } from 'ngx-gauge';
import { GuageChartComponent } from './dashboard/performance/guage-chart/guage-chart.component';
import { HorizondalBarChartComponent } from './dashboard/performance/horizondal-bar-chart/horizondal-bar-chart.component';
import { PiechartComponent } from './dashboard/accessibility/piechart/piechart.component';
import { BarchartComponent } from './dashboard/accessibility/barchart/barchart.component';
import { SecuritypiechartComponent } from './dashboard/security/securitypiechart/securitypiechart.component';
import { SecuritybarchartComponent } from './dashboard/security/securitybarchart/securitybarchart.component';
import { PdfExportComponent } from './dashboard/pdf-export/pdf-export.component';
import { OmnichannelComponent } from './dashboard/omnichannel/omnichannel.component';
import {SeoComponent} from './dashboard/seo/seo.component';
import {SeoGuageChartComponent}from './dashboard/seo/seo-guage-chart/seoguage-chart.component';
import { OmniBarchartComponent } from './dashboard/omnichannel/omni-barchart/omni-barchart.component';
import { StackedChartComponent } from './dashboard/seo/stacked-chart/stacked-chart.component';
import { BarChartActiveComponent } from './dashboard/activemonitor/bar-chart-active/bar-chart-active.component';
import { OmniStackedBarChartComponent } from './dashboard/omnichannel/omni-stacked-bar-chart/omni-stacked-bar-chart.component';
import { ActiveLineChartComponent } from './dashboard/activemonitor/active-line-chart/active-line-chart.component';
import {ActivemonitorComponent} from './dashboard/activemonitor/activemonitor.component';

@NgModule({
  declarations: [
    CxadashboardComponent,
    CxadashboardSidenavComponent,
    CxaSuiteComponent,
    SummaryComponent,
    TopNavComponent,
    CxaSuiteDialogComponent,
    PerformanceComponent,
    AccessibilityComponent,
    SecurityComponent,
    ImageSliderComponent,
    GuageChartComponent,
    HorizondalBarChartComponent,
    PiechartComponent,
    BarchartComponent,
    SecuritypiechartComponent,
    SecuritybarchartComponent,
    PdfExportComponent,
  
    OmnichannelComponent,
    ActivemonitorComponent,
    OmniStackedBarChartComponent,
    SeoComponent,
   
    SeoGuageChartComponent,
    OmniBarchartComponent,
    StackedChartComponent,
    BarChartActiveComponent,
    ActiveLineChartComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    CxadashboardRoutingModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,

    NgxGaugeModule,
    NgxChartsModule

  ],
  entryComponents: [
    CxaSuiteDialogComponent,
  ],
  exports: [
    MaterialModule
  ]
})
export class CxadashboardModule { }
