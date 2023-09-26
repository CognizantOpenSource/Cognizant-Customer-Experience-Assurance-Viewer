import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '@services/auth/authentication.service';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { UnSubscribable } from '@components/unsub';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent extends UnSubscribable implements OnInit, AfterViewInit, OnDestroy {
  newSession: boolean = false;
  form: any = {
    type: 'local',
    username: '',
    //password: '',
    rememberMe: false
  };
  returnUrl = '';
  failedAttempt = 0;
  authCode: string = '';
  sessionId: string = '';
  openModal: boolean = false;
  countDown: number | string;
  countDownInterval: any;
  enableResendCode: boolean = false;

  constructor(
    private authService: AuthenticationService, private router: Router, private route: ActivatedRoute,
    private toastr: ToastrService) {
    super();
    if (this.authService.touched) {
      window.location.reload();
    } else {
      this.newSession = true;
    }
  }

  get prod() {
    return environment.production;
  }

  ngOnInit() {
    this.managed(this.route.queryParams)
      .subscribe(params => this.returnUrl = params['returnUrl'] || '/home'); ///cxadashboard/dashboard/summary
    this.managed(this.authService.user$).subscribe(this.success.bind(this));
  }

  ngAfterViewInit() {
  }
  password(password: string) { }
  passwordInput: string=''; 
  login() { 
    this.password(this.passwordInput);  
    this.authCode = '';
    if (!this.form.username || !this.passwordInput) {
      this.toastr.warning('please provide valid inputs');
      return;
    }
    this.authService.checkAuthFactor(this.form.username, this.passwordInput).subscribe({
      next: (res) => {
        if (res.sessionId) {
          this.sessionId = res.sessionId;
          this.openModal = true;
          this.startCountDown();
        } else {
          this.authService.login(this.form.username, this.passwordInput);
        }
      },
      error: error => {
        this.toastr.error(error.status ? error.error.message : 'application is offline');
      }
    });
  }

  validateAuthCode() {
    if (this.sessionId != '') {
      const payload = {
        type: 'native',
        username: this.form.username,
        password: this.passwordInput,
        sessionId: this.sessionId,
        otp: this.authCode
      }
      this.authService.verfiyAuthCode(payload).subscribe(res => {
        this.authService.onSuccessAuth(res);
      }, error => {
        this.toastr.error(error.status ? error.error.message : 'Problem in verifying the auth code');
      });
    } else {
      this.toastr.error('Auth Code expires, please try resend and verify');
    }
  }

  success(user: any) {
    this.toastr.success(`logged in as '${user.name}'`);
    this.router.navigateByUrl(this.returnUrl);
  }

  failure(resp: any) {
    this.failedAttempt++;
    this.toastr.error('login error', `${resp.error}`);
  }

  onModalClose() {
    this.openModal = false;
    clearInterval(this.countDownInterval);
  }

  resendAuthCode() {
    this.login();
    this.startCountDown();
  }

  private startCountDown() {
    this.countDown = 180;
    this.enableResendCode = false;
    clearInterval(this.countDownInterval);
    this.countDownInterval = setInterval(() => {
      this.countDown = +this.countDown - 1;
      if (this.countDown <= 0) {
        this.sessionId = '';
        this.enableResendCode = true;
        clearInterval(this.countDownInterval);
        this.countDown = 'Please try resending code';
      }
    }, 1000);
  }

  override ngOnDestroy() {
    clearInterval(this.countDownInterval);
  }

}
