import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, timer } from 'rxjs';
import { AlertService } from 'src/app/services/alert.service';
import { Alert } from 'src/app/models/alert';

@Component({
  selector: 'app-alert',
  templateUrl: 'alert.component.html',
  styleUrls: ['./alert.component.scss'],
})
export class AlertComponent implements OnInit, OnDestroy {
  source = timer(1000, 2000);

  alert: Alert;
  private alertSubscription: Subscription;

  constructor(private alertService: AlertService) {}

  ngOnInit() {
    this.alertSubscription = this.alertService
      .getMessage()
      .subscribe((alert: Alert) => {
        this.alert = alert;
        if (alert?.type === 'success') {
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
    this.alertSubscription.unsubscribe();
  }
}
