import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CxaSuiteComponent } from './cxa-suite/cxa-suite.component';
import { CxadashboardComponent } from './cxadashboard.component';
import { SummaryComponent } from './dashboard/summary/summary.component';
import { PerformanceComponent } from './dashboard/performance/performance.component';
import { AccessibilityComponent } from './dashboard/accessibility/accessibility.component';
import { SecurityComponent } from './dashboard/security/security.component';
import { PdfExportComponent } from './dashboard/pdf-export/pdf-export.component';
import { TrendingComponent } from './trending/trending.component';
import { OmnichannelComponent } from './dashboard/omnichannel/omnichannel.component';
import {ActivemonitorComponent} from './dashboard/activemonitor/activemonitor.component';

import { SeoComponent } from './dashboard/seo/seo.component';
const routes: Routes = [
  {
      path: '', component: CxadashboardComponent,
      children: [
        {
          path: '',
          redirectTo: 'cxa-suite',
          pathMatch: 'full'
        },
        {
          path: 'cxa-suite/:id', component: CxaSuiteComponent
        },
        {
          path: 'trending', component: TrendingComponent
        },
        // },{
        //   path: 'activemonitors',
        //   children:[{
        //     path: '',
        //     redirectTo: 'activemonitor/:id',
        //     pathMatch: 'full'
        //   },
        //   {
        //     path: 'activemonitor/:id', component: ActivemonitorComponent
        //   },{
        //     path: 'execution/:id', component: ExecutionComponent
        //   }]
        // },
        {
          path: 'dashboard',
          children: [
            {
              path: '',
              redirectTo: 'summary/:id',
              pathMatch: 'full'
            },
            {
              path: 'summary/:id', component: SummaryComponent
            },
            {
              path: 'performance/:id', component: PerformanceComponent
            },
            {
              path: 'accessibility/:id', component: AccessibilityComponent
            },
            {
              path: 'security/:id', component: SecurityComponent
            },
            {
              path: 'download-report/:id', component: PdfExportComponent
            },
            {
              path: 'omnichannel/:id', component: OmnichannelComponent
            }, {
              path: 'activemonitor/:id', component:ActivemonitorComponent
            },
            {
              path:'seo/:id',component:SeoComponent
            },
          ]
        }
      ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})

export class CxadashboardRoutingModule { }
