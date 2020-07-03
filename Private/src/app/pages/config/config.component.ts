import { Component, OnInit, OnDestroy } from '@angular/core'
import { FormBuilder, FormGroup, FormControl } from '@angular/forms'

import { ChatConfig } from 'src/app/models/chat.config'
import { AvailableTimes } from 'src/app/models/available.times'

import { AlertService } from 'src/app/services/alert.service'
import { ConfigService } from 'src/app/services/config.service'
import { ValidationService } from 'src/app/services/validation.service'
import { Subscription } from 'rxjs'

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss'],
})
export class ConfigComponent implements OnInit, OnDestroy {
  configForm: FormGroup
  chatConfig: ChatConfig

  isLoading = false
  submitted = false

  availableTimes: AvailableTimes[] = [
    { value: 0, viewValue: '00:00' },
    { value: 1, viewValue: '01:00' },
    { value: 2, viewValue: '02:00' },
    { value: 3, viewValue: '03:00' },
    { value: 4, viewValue: '04:00' },
    { value: 5, viewValue: '05:00' },
    { value: 6, viewValue: '06:00' },
    { value: 7, viewValue: '07:00' },
    { value: 8, viewValue: '08:00' },
    { value: 9, viewValue: '09:00' },
    { value: 10, viewValue: '10:00' },
    { value: 11, viewValue: '11:00' },
    { value: 12, viewValue: '12:00' },
    { value: 13, viewValue: '13:00' },
    { value: 14, viewValue: '14:00' },
    { value: 15, viewValue: '15:00' },
    { value: 16, viewValue: '16:00' },
    { value: 17, viewValue: '17:00' },
    { value: 18, viewValue: '18:00' },
    { value: 19, viewValue: '19:00' },
    { value: 20, viewValue: '20:00' },
    { value: 21, viewValue: '21:00' },
    { value: 22, viewValue: '22:00' },
    { value: 23, viewValue: '23:00' },
    { value: 24, viewValue: '23:59' },
  ]

  configSubscription: Subscription

  constructor(
    private formBuilder: FormBuilder,
    private configService: ConfigService,
    private alertService: AlertService,
    private validationService: ValidationService,
  ) {}

  ngOnInit() {
    this.configForm = this.formBuilder.group(
      {
        isTimeRestricted: false,
        availableFrom: [{ value: '', disabled: false }],
        availableUntil: [{ value: '', disabled: false }],
      },
      {
        validator: this.validationService.chatAvailabilityValidators(
          'availableFrom',
          'availableUntil',
        ),
      },
    )

    this.loadConfig()
  }

  // convenience getter for easy access to form fields
  get controls() {
    return this.configForm.controls
  }

  getErrorMessage(formControl: FormControl) {
    return this.validationService.getErrorMessage(formControl)
  }

  private populateForm(config: ChatConfig) {
    this.controls.isTimeRestricted.setValue(config.timeRestricted)
    this.controls.availableFrom.setValue(config.availableFrom)
    this.controls.availableUntil.setValue(config.availableUntil)
  }

  private async loadConfig() {
    this.isLoading = true
    this.configSubscription = this.configService.get().subscribe(
      (config) => {
        this.chatConfig = config
        this.populateForm(this.chatConfig)
        this.toggleTimeRestrictions()
      },
      (error) => {
        this.alertService.error(error)
      },
    )
    this.isLoading = false
  }

  onSubmit() {
    this.submitted = true

    if (!this.configForm.valid) {
      return
    }

    this.isLoading = true

    const config: ChatConfig = <ChatConfig>{
      timeRestricted: this.controls.isTimeRestricted.value,
      availableFrom: this.controls.availableFrom.value,
      availableUntil: this.controls.availableUntil.value,
    }

    this.configService.update(config).subscribe(
      (response) => {
        this.alertService.handleResponse(response)
      },
      (error) => {
        this.alertService.error(error)
      },
    )

    this.isLoading = false
  }

  toggleTimeRestrictions() {
    if (this.controls.isTimeRestricted.value) {
      this.controls.availableFrom.enable()
      this.controls.availableUntil.enable()
    } else {
      this.controls.availableFrom.disable()
      this.controls.availableUntil.disable()
    }
  }

  ngOnDestroy() {
    this.configSubscription.unsubscribe()
  }
}
