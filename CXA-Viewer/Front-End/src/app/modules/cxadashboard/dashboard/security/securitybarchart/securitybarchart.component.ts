import { Component, Input, OnChanges, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, SimpleChanges } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { Color, ScaleType } from '@swimlane/ngx-charts';

@Component({
  selector: 'leap-securitybarchart',
  templateUrl: './securitybarchart.component.html',
  styleUrls: ['./securitybarchart.component.css']
})
export class SecuritybarchartComponent {
  @Input() securitBardata: any = [];
  multi: any[];
  view: any[] = [500, 300];

  // options
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  showXAxisLabel: boolean = true;
  xAxisLabel: string = '';
  showYAxisLabel: boolean = true;
  yAxisLabel: string = '';
  animations: boolean = true;
  
  showLegendPosition:any = 'below'
  showLegendTitle:any=''

  // colorScheme = {
  //   domain: ['green', 'red', '#AAAAAA']
  // };
  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#a30000']
  };
  // constructor() {
  //   Object.assign(this, { multi });
  // }

  onSelect(event) {
    //console.log(event);
  }
}
