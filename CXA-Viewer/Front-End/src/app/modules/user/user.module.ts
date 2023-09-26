import { UserProfileComponent } from './user-profile/user-profile.component';
import { NgModule } from '@angular/core';
import { UserRoutingModule } from './user-routing.module';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared.module';
import { ClarityModule } from '@clr/angular';
@NgModule({
  declarations: [
    UserProfileComponent,
    ChangePasswordComponent
  ],
  imports: [
    UserRoutingModule,
    FormsModule,
    SharedModule,
    ClarityModule
  ]
})
export class UserModule { }
