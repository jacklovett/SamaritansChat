import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, timer } from 'rxjs';

import { Alert } from 'src/app/models/alert';

import { AlertService } from 'src/app/services/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: 'alert.component.html',
  styleUrls: ['./alert.component.scss'],
})
export class AlertComponent implements OnInit, OnDestroy {
  private subscription: Subscription;
  alert: Alert;
  source = timer(1000, 2000);

  constructor(private alertService: AlertService) {}

  ngOnInit() {
    this.subscription = this.alertService
      .getMessage()
      .subscribe((alert: Alert) => {
        this.alert = alert;
        if (alert && alert.type === 'success') {
          const subscribe = this.source.subscribe();
          setTimeout(() => {
            subscribe.unsubscribe();
            this.close();
          }, 10000);
        }
      });
  }

  ngOnDestroy() {
    this.close();
  }

  close() {
    this.alertService.close();
    this.subscription.unsubscribe();
  }
}
