import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CallbackPipe } from '../pipes/callback.pipe';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ClarityModule } from '@clr/angular';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { ContainerComponent } from 'src/app/components/container/container.component';
import { MaterialModule } from './material.module'
import { IconComponent } from '@components/icon/icon.component';

@NgModule({
  declarations: [
    CallbackPipe,
    ContainerComponent,
    IconComponent
  ],
  imports: [
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    ClarityModule,
    CommonModule,
    RouterModule,
    MaterialModule,
  ],
  exports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ClarityModule,
    CallbackPipe,
    ContainerComponent,
    MaterialModule,
    IconComponent
  ],
  providers: [CallbackPipe]
})
export class SharedModule { }
