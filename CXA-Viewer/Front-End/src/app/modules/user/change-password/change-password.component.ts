import { Component, OnInit } from '@angular/core';
import { UnSubscribable } from '@components/unsub';
import { AuthenticationService } from '@services/auth/authentication.service';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';

@Component({
  selector: 'app-changePassword',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent extends UnSubscribable implements OnInit {

  user: any;
  form: any = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  returnUrl = '';
  failedAttempt = 0;
  constructor(
    private authService: AuthenticationService, private toastr: ToastrService, private location: Location) {
    super();
  }
  get validForm() {
    return this.form.oldPassword && this.form.newPassword && this.form.confirmPassword
      && this.form.newPassword === this.form.confirmPassword;
  }
  ngOnInit() {
    this.managed(this.authService.user$).subscribe(user => this.user = user);
  }
  changePassword() {
    if (!this.form.oldPassword || (!this.form.newPassword || this.form.newPassword !== this.form.confirmPassword)) {
      this.toastr.warning('please provide valid inputs');
      return;
    }
    this.authService.updatePassword({
      newPassword: this.form.newPassword,
      oldPassword: this.form.oldPassword
    });
  }
  close() {
    this.location.back();
  }
}
