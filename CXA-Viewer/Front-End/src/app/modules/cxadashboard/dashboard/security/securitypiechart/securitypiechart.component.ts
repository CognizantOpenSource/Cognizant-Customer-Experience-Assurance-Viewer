import { Component, Input, OnChanges, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'leap-securitypiechart',
  templateUrl: './securitypiechart.component.html',
  styleUrls: ['./securitypiechart.component.css']
})
export class SecuritypiechartComponent implements OnChanges {
  @Input() securitypiedata: any = [];
  pieChartData: any[] = [];
  single: any[];
  view: any[] = [360, 300];

  // options
  gradient: boolean = false;
  showLegend: boolean = false;
  showLabels: boolean = true;
  isDoughnut: boolean = false;

  // colorScheme = {
  //   domain: ['#d2222d', 'orange', '#ffdf00','#CA8606']
  // }
  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#d2222d', 'orange', '#ffdf00','#CA8606']
  };

  constructor(private changeDetectorRefs: ChangeDetectorRef) {
  }
  ngOnChanges() {
    this.pieChartData = this.securitypiedata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }
  onSelect(data): void {
    //console.log('Item clicked', JSON.parse(JSON.stringify(data)));
  }

  onActivate(data): void {
    //console.log('Activate', JSON.parse(JSON.stringify(data)));
  }

  onDeactivate(data): void {
    //console.log('Deactivate', JSON.parse(JSON.stringify(data)));
  }

}
