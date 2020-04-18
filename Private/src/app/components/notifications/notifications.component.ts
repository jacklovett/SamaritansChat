import { Component, OnDestroy, ViewEncapsulation, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from 'src/app/services/alert.service';
import { NotificationService } from 'src/app/services/notification.service';
import { Subscription } from 'rxjs';

import { Notification } from 'src/app/components/notifications/notification';
import { ConversationRequest } from 'src/app/models/conversation.request';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notificationCount = 0;
  notifications: Notification[] = [];

  notificationsSubscription: Subscription;
  isReloadRequiredSubscription: Subscription;

  constructor(
    private router: Router,
    private alertService: AlertService,
    private notificationService: NotificationService,
  ) {
    this.notificationService.connect();
  }

  ngOnInit() {
    this.getNotifications();

    this.isReloadRequiredSubscription = this.notificationService
      .isReloadRequired()
      .subscribe((result) => {
        if (result) {
          this.getNotifications();
        }
      });

    this.notificationsSubscription = this.notificationService
      .getNotifications()
      .subscribe((notification) => {
        this.notifications.unshift(notification);
        ++this.notificationCount;
      });
  }

  public selectNotification(notification: Notification) {
    if (notification.processed) {
      return;
    }

    if (!notification.read) {
      --this.notificationCount;
      notification.read = true;
      this.notificationService.update(notification);
    }

    if (notification.type === 'NEW_USER_CONNECTED') {
      this.startConversation(notification);
    }

    if (notification.type === 'USER_DISCONNECTED') {
      this.router.navigate(['chatlog'], {
        queryParams: {
          username: notification.username,
          id: notification.id,
        },
      });
    }
  }

  private async startConversation(notification: Notification) {
    const conversationRequest: ConversationRequest = <ConversationRequest>{
      chatUser: notification.username,
    };

    try {
      const response = await this.notificationService
        .startConversation(conversationRequest)
        .toPromise();
      this.alertService.handleResponse(response);
      notification.processed = true;
      this.notificationService.update(notification);
      this.router.navigate(['chat']);
    } catch (error) {
      this.alertService.error(error);
    }
  }

  public deleteNotification(e: MouseEvent, id: number) {
    e.stopPropagation();
    this.notificationService.delete(id);
  }

  private async getNotifications() {
    try {
      this.notifications = [];
      this.notifications = await this.notificationService.get().toPromise();
      const unreadNotifications = this.notifications.filter(
        (notification) => !notification.read,
      );
      this.notificationCount = unreadNotifications.length;
    } catch (error) {
      this.alertService.error(error);
    }
  }

  ngOnDestroy() {
    this.notificationsSubscription.unsubscribe();
    this.isReloadRequiredSubscription.unsubscribe();
  }
}
