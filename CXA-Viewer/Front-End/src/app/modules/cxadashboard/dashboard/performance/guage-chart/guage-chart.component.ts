import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'leap-guage-chart',
  templateUrl: './guage-chart.component.html',
  styleUrls: ['./guage-chart.component.css']
})
export class GuageChartComponent implements OnInit {
  @Input() speedIndex:any;
  
  size:number;
  thick:number;
  constructor() {
     
    //console.log("screenWidth",innerWidth)
    if (innerWidth < 500) { 
    this.size = 150;
    this.thick=10;
    } else {
      this.size = 180; 
      this.thick=20;
    }
  }
  markerConfig = {
    "0": { color: '#555', size: 8, label: '0', type: 'line' },
    "15": { color: '#555', size: 4, type: 'line' },
    "30": { color: '#555', size: 8, label: '30', type: 'line' },
    "40": { color: '#555', size: 4, type: 'line' },
    "50": { color: '#555', size: 8, label: '50', type: 'line' },
    "60": { color: '#555', size: 4, type: 'line' },
    "70": { color: '#555', size: 8, label: '70', type: 'line' },
    "85": { color: '#555', size: 4, type: 'line' },
    "100": { color: '#555', size: 8, label: '100', type: 'line' },
  }
  thresholdConfig = {
    '0': {color: 'green'},
    '3.4': {color: 'orange'},
    '5.8': {color: 'red'}
};
  ngOnInit(): void {

  }
}
