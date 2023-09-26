import { Component, Input, OnChanges,OnInit,ChangeDetectionStrategy,ChangeDetectorRef } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { Color, ScaleType } from '@swimlane/ngx-charts';

@Component({
  selector: 'leap-piechart',
  templateUrl: './piechart.component.html',
  styleUrls: ['./piechart.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PiechartComponent implements OnChanges {
  @Input() piedata: any[];
  pieChartData:any[]=[];
  single: any[];
  view: [number,number];

  // options
  gradient: boolean = false;
  showLegend: boolean = false;
  showLabels: boolean = true;
  isDoughnut: boolean = false;
  legendPosition: string = 'left';

  // colorScheme = {
  //   domain: ['#d2222d', '#FFBF00', '#B19CD8'],['#5d62b5', '#29c3be', '#f2726f']
  // };

  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#d2222d', '#FFBF00', '#B19CD8']
  };

  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if (innerWidth < 800) {
      const decreasedWidth = innerWidth /1.35;
    const decreasedHeight = 300 * (decreasedWidth / 500); 
    this.view = [decreasedWidth, decreasedHeight];
    } else {
      this.view = [500, 300]; 
    }
  }
  ngOnChanges() {
    this.pieChartData=this.piedata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }
  onSelect(data): void {
  }

  onActivate(data): void {
  }

  onDeactivate(data): void {
  }

}
