import { Component, OnInit } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
@Component({
  selector: 'leap-cxa-suite-dialog',
  templateUrl: './cxa-suite-dialog.component.html',
  styleUrls: ['./cxa-suite-dialog.component.css']
})
export class CxaSuiteDialogComponent {

  constructor(public dialogRef: MatDialogRef<CxaSuiteDialogComponent>
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
