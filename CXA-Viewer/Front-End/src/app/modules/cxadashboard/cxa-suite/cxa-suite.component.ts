import { Component,ChangeDetectorRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { CxadashboardServicesService } from '../services/cxadashboard-services.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CxaSuiteDialogComponent } from '../cxa-suite-dialog/cxa-suite-dialog.component';
import { LoaderService } from '@services/loader.service';
import { MatSelect } from '@angular/material/select';
import { MatOption } from '@angular/material/core';
@Component({
  selector: 'leap-cxa-suite',
  templateUrl: './cxa-suite.component.html',
  styleUrls: ['./cxa-suite.component.css']
})
export class CxaSuiteComponent implements OnInit {
  @ViewChild('select') select: MatSelect;
  @ViewChild('selectuiux') selectuiux: MatSelect;
  allSelected = false;

  isLinear = false;
  ConfigurationFirstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  performanceFormGroup: FormGroup;
  accessibilityFormGroup: FormGroup;
  securityFormGroup: FormGroup;
  activeMonitorFormGroup:FormGroup;
  crossBrowserFormGroup:FormGroup;
  SeoCheckFormGroup:FormGroup;


  IntervalLists:any[]=['10','15','20'];

 DurationLists:any[]=['1','2','3','4'];  selectedInterval:any;

  selectedDuration:any;
  editable = false;
  regions:any[] = [{}];

  activeregions: any[] = [{}];
  selectedEnvironment:any

  showPerformance = false;
  showAccesibility = false;
  showSecurity = false;
  showCrossBrowser = false;
  showActiveMonitor=false;
  showSeoCheck=false;

  selectedRegion:any;
  executionID: string = "";
  attributesList = [];
  projectId = "";
  iosRegions: any[] = [];
  AllRegion: any[] = []
  ActiveMonitorValidationLists:string[]=['EU','US'];
  selectedUIUXValidations: any[] = [];
  selectedActiveValidations:any[]=[];
  allSelecteduiux=false
  selectedActiveMonitorValidation:any[]=[];
  
  selectedOptions: string[] = [];
  constructor(private _formBuilder: FormBuilder,
    private cxadashboardServices: CxadashboardServicesService,
    private router: Router, private route: ActivatedRoute,
    private toastr: ToastrService, public dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private loaderService: LoaderService) {
  }
  
 
  ngOnInit() {
    this.ConfigurationFirstFormGroup = this._formBuilder.group({
      configuration: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
    

      this.performanceFormGroup = this._formBuilder.group({
  
        url: ['', Validators.required],
  
        browser: ['Chrome', Validators.required],
  
        environment:['', Validators.required],
        urlenvironment: [''],
        regionenvironment: ['']
  
      });

      this.performanceFormGroup.get('environment')?.valueChanges.subscribe(value => {
      if(value=='Cloud'){
        this.performanceFormGroup.get('urlenvironment')?.enable();
        this.performanceFormGroup.get('urlenvironment')?.setValidators([Validators.required]);
        this.performanceFormGroup.get('regionenvironment')?.enable();
        this.performanceFormGroup.get('regionenvironment')?.setValidators([Validators.required]);
       }  else {
        this.performanceFormGroup.get('urlenvironment')?.disable()
        this.performanceFormGroup.get('urlenvironment')?.clearValidators();
        this.performanceFormGroup.get('regionenvironment')?.disable()
        this.performanceFormGroup.get('regionenvironment')?.clearValidators();
        }});
      this.accessibilityFormGroup = this._formBuilder.group({
  
        url: ['', Validators.required],
  
        browser: ['Chrome', Validators.required],
  
        environment:['', Validators.required],
  
        // urlenvironment: ['', this.validateIfCloudSelected.bind(this.accessibilityFormGroup)],
        // regionenvironment: ['', this.validateIfCloudSelected.bind(this.accessibilityFormGroup)]
        urlenvironment: [''],
        regionenvironment: ['']
  
      });
      this.accessibilityFormGroup.get('environment')?.valueChanges.subscribe(value => {
        if(value=='Cloud'){
          this.accessibilityFormGroup.get('urlenvironment')?.enable();
          this.accessibilityFormGroup.get('urlenvironment')?.setValidators([Validators.required]);
          this.accessibilityFormGroup.get('regionenvironment')?.enable();
          this.accessibilityFormGroup.get('regionenvironment')?.setValidators([Validators.required]);
         }  else {
          this.accessibilityFormGroup.get('urlenvironment')?.disable()
          this.accessibilityFormGroup.get('urlenvironment')?.clearValidators();
          this.accessibilityFormGroup.get('regionenvironment')?.disable()
          this.accessibilityFormGroup.get('regionenvironment')?.clearValidators();
          }});
    
    this.securityFormGroup = this._formBuilder.group({
      url: ['', Validators.required],
      browser: ['Chrome', Validators.required],
      
  
      environment:['', Validators.required],
  
      urlenvironment: [''],
      regionenvironment: ['']
    
    });
    this.securityFormGroup.get('environment')?.valueChanges.subscribe(value => {
      if(value=='Cloud'){
        this.securityFormGroup.get('urlenvironment')?.enable();
        this.securityFormGroup.get('urlenvironment')?.setValidators([Validators.required]);
        this.securityFormGroup.get('regionenvironment')?.enable();
        this.securityFormGroup.get('regionenvironment')?.setValidators([Validators.required]);
       }  else {
        this.securityFormGroup.get('urlenvironment')?.disable()
        this.securityFormGroup.get('urlenvironment')?.clearValidators();
        this.securityFormGroup.get('regionenvironment')?.disable()
        this.securityFormGroup.get('regionenvironment')?.clearValidators();
        }});
    
    this.activeMonitorFormGroup = this._formBuilder.group({
      url: ['', Validators.required],

      browser: ['Chrome', Validators.required],

      activeMonitorenvironment:['', Validators.required],
      

      activeMonitorurl0:[''],

      activeMonitorregion0:[''],
      interval:['',Validators.required],

      duration:['',Validators.required]
    });
    this.activeMonitorFormGroup.get('activeMonitorenvironment')?.valueChanges.subscribe(value => {
      if(value=='Cloud'|| value=="Saucelabs"){
        this.activeMonitorFormGroup.get('activeMonitorurl0')?.enable();
        this.activeMonitorFormGroup.get('activeMonitorurl0')?.setValidators([Validators.required]);
        this.activeMonitorFormGroup.get('activeMonitorregion0')?.enable();
        this.activeMonitorFormGroup.get('activeMonitorregion0')?.setValidators([Validators.required]);
       }  else {
        this.activeMonitorFormGroup.get('activeMonitorurl0')?.disable()
        this.activeMonitorFormGroup.get('activeMonitorurl0')?.clearValidators();
        this.activeMonitorFormGroup.get('activeMonitorregion0')?.disable()
        this.activeMonitorFormGroup.get('activeMonitorregion0')?.clearValidators();
        }});
        this.crossBrowserFormGroup = this._formBuilder.group({      

          url: ['', Validators.required],
          
                browser: ['Chrome', Validators.required],
             //   AllRegion: ['', Validators.required]
             
          environment:['', Validators.required],
      
          urlenvironment: [''],
          regionenvironment: ['']
          
              });
              this.crossBrowserFormGroup.get('environment')?.valueChanges.subscribe(value => {
                if(value=='Cloud'){
                  this.crossBrowserFormGroup.get('urlenvironment')?.enable();
                  this.crossBrowserFormGroup.get('urlenvironment')?.setValidators([Validators.required]);
                  this.crossBrowserFormGroup.get('regionenvironment')?.enable();
                  this.crossBrowserFormGroup.get('regionenvironment')?.setValidators([Validators.required]);
                 }  else {
                  this.crossBrowserFormGroup.get('urlenvironment')?.disable()
                  this.crossBrowserFormGroup.get('urlenvironment')?.clearValidators();
                  this.crossBrowserFormGroup.get('regionenvironment')?.disable()
                  this.crossBrowserFormGroup.get('regionenvironment')?.clearValidators();
                  }});
    this.SeoCheckFormGroup = this._formBuilder.group({      

      url: ['', Validators.required],
      
            browser: ['Chrome', Validators.required],
         //   AllRegion: ['', Validators.required]
         
      environment:['', Validators.required],
  
      urlenvironment: [''],
      regionenvironment: ['']
      
          });
          this.SeoCheckFormGroup.get('environment')?.valueChanges.subscribe(value => {
            if(value=='Cloud'){
              this.SeoCheckFormGroup.get('urlenvironment')?.enable();
              this.SeoCheckFormGroup.get('urlenvironment')?.setValidators([Validators.required]);
              this.SeoCheckFormGroup.get('regionenvironment')?.enable();
              this.SeoCheckFormGroup.get('regionenvironment')?.setValidators([Validators.required]);
             }  else {
              this.SeoCheckFormGroup.get('urlenvironment')?.disable()
              this.SeoCheckFormGroup.get('urlenvironment')?.clearValidators();
              this.SeoCheckFormGroup.get('regionenvironment')?.disable()
              this.SeoCheckFormGroup.get('regionenvironment')?.clearValidators();
              }});

    let params = this.route.params.subscribe(params => {
      this.projectId = params['id'];
    });
    if (this.projectId === "" || this.projectId === null || this.projectId === undefined) {
      this.router.navigate(["../home"]);
    }

  }

  ngAfterContentChecked(): void {
    this.cdr.detectChanges();
  }

  // validateIfCloudSelected(values){
   
  //   if(values?.get('environment')?.value == 'Cloud' ){

  //     return Validators.required
  //   }
  //   else{
  //   return null
  //   }
  // }
  // validateIfActiveCloudSelected(values:FormGroup){
  //   if(values?.get('activeMonitorenvironment')?.value == 'Cloud' || values?.get('activeMonitorenvironment')?.value == 'Saucelabs'){
  //     return Validators.required
  //   }
  //   else{
  //     return null
  //   }
  // }
  validateConfiguration() {
    if (this.ConfigurationFirstFormGroup.valid && (this.showPerformance  || this.showAccesibility || this.showSecurity || this.showActiveMonitor ||this.showSeoCheck || this.showCrossBrowser)) {
      return false;
    } else {
      return true;
    }
  }
 
  goForward(stepper: MatStepper) {
    stepper.next();
  }
 
  performaceChecked(event) {
    if (event["checked"] == false) {
      this.showPerformance = false;
    }
    else if (event["checked"] == true) {
      this.showPerformance = true;
    }
  }
  accessibilityChecked(event) {
    if (event["checked"] == false) {
      this.showAccesibility = false;
    }
    else if (event["checked"] == true) {
      this.showAccesibility = true;
    }
  }
  securityChecked(event) {
    if (event["checked"] == false) {
      this.showSecurity = false;
    }
    else if (event["checked"] == true) {
      this.showSecurity = true;
    }
  }
  activemonitorBoxChange(event) {
    if (event["checked"] == false) {
      this.showActiveMonitor = false;
    }
    else if (event["checked"] == true) {
      this.showActiveMonitor = true;
    }

  }
  crossBrowserBoxChange(event) {
    if (event["checked"] == false) {
      this.showCrossBrowser = false;
    }
    else if (event["checked"] == true) {
      this.showCrossBrowser = true;
    }

  }
  showSeoCheckBoxChange(event){

    if (event["checked"] == false) {

      this.showSeoCheck = false;}

    else if (event["checked"] == true) {      this.showSeoCheck = true;}}

  onSubmit() {
    this.loaderService.show();
    this.submitSuite("data");
  }
  submitSuite(executionId: string) {
    let isSuccess = false;
    let newArra: String[] = [];
    let data = {
      "performancesCheck": false,
      "url": "",
      "projectId": "",
      "accessibilityCheck": false,
      "securityCheck": false,
      "seoCheck":false,
      "AllRegion": "",
      "crossBrowser":false,
      "activeMonitorCheck":false,
      "region":newArra,
      "environment":"",

      "urlAndRegion":{    

        "environmentUrl":"",

        "environmentRegion":"",}

    ,
    "interval":"",

      "duration":"",

    "activeMonitorEnvir":"",

    "activeMonitorUrlAndRegion":[{    

      "activeMonitorUrl":"",

      "activeMonitorRegion":"",}

  ]
      // executionId
    }
    data.projectId = this.projectId;
    if (this.showPerformance) {
      data.performancesCheck = true;
      if (data.url == "") {
        data.url = this.performanceFormGroup.value.url;
      }
      if (data.AllRegion == "") {
        data.AllRegion = this.performanceFormGroup.value.AllRegion;
      }
      data.environment= this.performanceFormGroup.value.environment;

      data.urlAndRegion = {

        environmentUrl: this.performanceFormGroup.value['urlenvironment'],

        environmentRegion: this.performanceFormGroup.value['regionenvironment']

      }
      // this.cxadashboardServices.insertPerformanceData(this.performanceFormGroup.value.url, this.executionID).subscribe(data => {
      //   isSuccess = true;
      // })
    }
    if (this.showAccesibility) {
      data.accessibilityCheck = true;
      if (data.url == "") {
        data.url = this.accessibilityFormGroup.value.url;
      }
    if (data.AllRegion == "") {

     data.AllRegion = this.accessibilityFormGroup.value.AllRegion;
        
      }
      data.environment= this.accessibilityFormGroup.value.environment;

      data.urlAndRegion = {

        environmentUrl: this.accessibilityFormGroup.value['urlenvironment'],

        environmentRegion: this.accessibilityFormGroup.value['regionenvironment']

      }
    }
    if (this.showSecurity) {
      data.securityCheck = true;
      if (data.url == "") {
        data.url = this.securityFormGroup.value.url;
      }
      if (data.AllRegion == "") {

        data.AllRegion = this.securityFormGroup.value.AllRegion;
           
         }
         data.environment= this.securityFormGroup.value.environment;
         data.urlAndRegion = {

          environmentUrl: this.securityFormGroup.value['urlenvironment'],
  
          environmentRegion: this.securityFormGroup.value['regionenvironment']
  
        }
    }
    
    if(this.showActiveMonitor){
      data.activeMonitorCheck = true;
      if (data.url == "") {
        data.url = this.activeMonitorFormGroup.value.url;
      }
      
      data.region = this.ActiveMonitorValidationLists
      data.activeMonitorEnvir= this.activeMonitorFormGroup.value.activeMonitorenvironment;

      data.activeMonitorUrlAndRegion = this.activeregions.map((_, i) => ({

        activeMonitorUrl: this.activeMonitorFormGroup.value['activeMonitorurl' + i],

        activeMonitorRegion: this.activeMonitorFormGroup.value['activeMonitorregion' + i]

      }));
      if (data.interval == "") {

        data.interval = this.activeMonitorFormGroup.value.interval;
  
        }
  
        if (data.duration == "") {
  
        data.duration = this.activeMonitorFormGroup.value.duration;
  
        }

    }
    if (this.showCrossBrowser) {
      data.crossBrowser = true;

      if (data.url == "") {

        data.url = this.crossBrowserFormGroup.value.url;

      }      data.environment= this.crossBrowserFormGroup.value.environment;
      if (data.AllRegion == "") {

        data.AllRegion = this.crossBrowserFormGroup.value.AllRegion;
           
         }
         data.urlAndRegion = {

          environmentUrl: this.crossBrowserFormGroup.value['urlenvironment'],
  
          environmentRegion: this.crossBrowserFormGroup.value['regionenvironment']
  
        }

    }
    if (this.showSeoCheck) {
      data.seoCheck = true;

      if (data.url == "") {

        data.url = this.SeoCheckFormGroup.value.url;

      }
      if (data.AllRegion == "") {

        data.AllRegion = this.SeoCheckFormGroup.value.AllRegion;
           
         }
         data.environment= this.SeoCheckFormGroup.value.environment;
         data.urlAndRegion = {

          environmentUrl: this.SeoCheckFormGroup.value['urlenvironment'],
  
          environmentRegion: this.SeoCheckFormGroup.value['regionenvironment']
  
        }

    }
    
    //console.log(data);
    this.cxadashboardServices.insertMetricsData(data).subscribe(data => {

    })
    this.toastr.success('Execution started succesfully');
    //localStorage.setItem("projectId",this.projectId);
    setTimeout(() => {
      this.loaderService.hide();
      this.router.navigate(["/cxadashboard/dashboard/summary", this.projectId])
    }, 5000);
  }

  // addField(selectedForm) {

  //   const selectedFormGroup = selectedForm === 'performance' ? this.performanceFormGroup :(selectedForm === 'security' ? this.securityFormGroup : (selectedForm === 'seoCheck'? this.SeoCheckFormGroup: this.accessibilityFormGroup));
  //     // const i = this.regions.length;


  //     const urlenvironment = 'urlenvironment';

  //     const regionenvironment = 'regionenvironment';

  //     selectedFormGroup.addControl(urlenvironment, this._formBuilder.control('', Validators.required));

  //     selectedFormGroup.addControl(regionenvironment, this._formBuilder.control('', Validators.required));

  //     //console.log("selectedFormGroup"+selectedFormGroup.value)


  // }




  addActiveMonitorField() {

   

    if (this.activeMonitorFormGroup?.get('activeMonitorenvironment')?.value === 'Cloud' || this.activeMonitorFormGroup?.get('activeMonitorenvironment')?.value === 'Saucelabs') {

      const i = this.activeregions.length;

      const activeMonitorurl = 'activeMonitorurl' + i;

      const activeMonitorregion = 'activeMonitorregion' + i;

      this.activeMonitorFormGroup.addControl(activeMonitorurl, this._formBuilder.control('', Validators.required));

      this.activeMonitorFormGroup.addControl(activeMonitorregion, this._formBuilder.control('', Validators.required));

     

    }




    this.activeregions.push({ activeMonitorurl: '', activeMonitorregion: '' });

  }

  validateData() {
    if (this.showPerformance) {
      return this.performanceFormGroup.invalid;
    }
    if (this.showAccesibility) {
      return this.accessibilityFormGroup.invalid;
    }
    if (this.showSecurity) {
      return this.securityFormGroup.invalid;
    }
    if(this.showActiveMonitor){
      return this.activeMonitorFormGroup.invalid;
    }
    if(this.showCrossBrowser){
      return this.crossBrowserFormGroup.invalid;
    }
    if (this.showSeoCheck) {

      return this.SeoCheckFormGroup.invalid;

    }
    else {
      return false
    }

  }
  validateUrl(stepper, selectedForm) {
    const selectedFormGroup = selectedForm === 'performance' ? this.performanceFormGroup :(selectedForm === 'security' ? this.securityFormGroup :  (selectedForm === 'seoCheck'? this.SeoCheckFormGroup: (selectedForm === 'crossBrowser'? this.crossBrowserFormGroup: this.accessibilityFormGroup)));
    if(selectedForm==='activemonitor'){
      let selectedsFormGroup=this.activeMonitorFormGroup
      this.cxadashboardServices.validatePerformanceUrl(selectedsFormGroup.value.url).subscribe(data => {

        if (data.Message === "invalid Url") {
          this.toastr.error('URL is Invalid');
        } else {this.toastr.success('URL validated succesfully');
        this.activeMonitorFormGroup.patchValue({
          url: selectedsFormGroup.value.url,
        });
        stepper.next();}
      }, error => {
        this.toastr.error('Invalid URL');
      })
    }
    else{
    this.cxadashboardServices.validatePerformanceUrl(selectedFormGroup.value.url).subscribe(data => {
      const environment = this.selectedEnvironment;
      
      const region = this.selectedRegion;
      if (data.Message === "invalid Url") {
        this.toastr.error('URL is Invalid');
      } else {
        this.toastr.success('URL validated succesfully');
       
        this.performanceFormGroup.patchValue({

          url: selectedFormGroup.value.url,

          ['urlenvironment']: selectedFormGroup.value['urlenvironment'],

          ['regionenvironment']: selectedFormGroup.value['regionenvironment' ]

        });

//console.log("selectedFormGroup.value['urlenvironment']",selectedFormGroup.value['urlenvironment'],"Perf")


        this.accessibilityFormGroup.patchValue({

          url: selectedFormGroup.value.url,

            ['urlenvironment']: selectedFormGroup.value['urlenvironment'],

            ['regionenvironment']: selectedFormGroup.value['regionenvironment']

       

        });

        this.securityFormGroup.patchValue({
          url: selectedFormGroup.value.url,

          ['urlenvironment']: selectedFormGroup.value['urlenvironment'],

          ['regionenvironment']: selectedFormGroup.value['regionenvironment']

     
        })
     
        this.activeMonitorFormGroup.patchValue({
          url: selectedFormGroup.value.url,
        });
        this.crossBrowserFormGroup.patchValue({

          url: selectedFormGroup.value.url,
          
          ['urlenvironment']: selectedFormGroup.value['urlenvironment'],

          ['regionenvironment']: selectedFormGroup.value['regionenvironment']

        });
        //console.log("selectedFormGroup.value['urlenvironment']",selectedFormGroup.value['urlenvironment'],"active")
        this.SeoCheckFormGroup.patchValue({

          url: selectedFormGroup.value.url,
          
          ['urlenvironment']: selectedFormGroup.value['urlenvironment'],

          ['regionenvironment']: selectedFormGroup.value['regionenvironment']

        });
      
        stepper.next();
      }
    }, error => {
      this.toastr.error('Invalid URL');
    }
    )
    
  }}


//   optionActiveClick() {
//     let newStatus = true;
//     this.selectedActiveValidations = [];
//     this.select.options.forEach((item: MatOption) => {
//       if (!item.selected) {
//         newStatus = false;
//       }
//       else {
//         this.selectedActiveValidations.push(item.value);
//       }
//     });
//     this.allSelected = newStatus;
//   }
// toggleAllSelectionActive() {
//     if (this.allSelected) {
//       this.selectedActiveValidations = [];
//       this.select.options.forEach((item: MatOption) => {
//         item.select();
//         this.selectedActiveValidations.push(item.value);
//       });
//     } else {
//       this.select.options.forEach((item: MatOption) => item.deselect());
//       this.selectedActiveValidations = [];
//     }

//   }

// optionClick() {

//   let newuiuxStatus = true;

//   this.selectedUIUXValidations = [];

//   this.selectuiux.options.forEach((item: MatOption) => {

//     if(item.value==="CasesensitiveError" || item.value==='spellError'){

//       item.disabled=true;

//     }

//     else{

//       item.disabled=false;

//       if (!item.selected) {

//         newuiuxStatus = false;

//       }

//       else {

//         this.selectedUIUXValidations.push(item.value);

//       }

//     }

   

//   });

//   this.allSelecteduiux = newuiuxStatus;

// }




}