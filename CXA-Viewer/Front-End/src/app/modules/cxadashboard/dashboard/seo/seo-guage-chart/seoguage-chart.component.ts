import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'leap-seo-guage-chart',
  templateUrl: './seoguage-chart.component.html',
  styleUrls: ['./seoguage-chart.component.css']
})
export class SeoGuageChartComponent implements OnInit {
  @Input() score:any;
  size:number;
  thick:number;
  constructor() {  if (innerWidth < 500) { 
    this.size = 100;
    this.thick=10;
    } else {
      this.size = 200; 
      this.thick=20;
    }}
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
    '0': {color: 'red'},
    '50': {color: 'orange'},
    '90': {color: 'green'}
};
  ngOnInit(): void {

  }
}
