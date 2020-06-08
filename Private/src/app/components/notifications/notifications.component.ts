import { Component, OnDestroy, ViewEncapsulation, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { Subscription } from 'rxjs'
import { RxStompService } from '@stomp/ng2-stompjs'
import { Message } from 'stompjs'

import { Notification } from 'src/app/components/notifications/notification'
import { ConversationRequest } from 'src/app/models/conversation.request'
import { AlertService } from 'src/app/services/alert.service'
import { NotificationService } from 'src/app/services/notification.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notificationCount = 0
  notifications: Notification[] = []

  notificationsSubscription: Subscription
  isReloadRequiredSubscription: Subscription

  constructor(
    private router: Router,
    private alertService: AlertService,
    private rxStompService: RxStompService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
  ) {}

  ngOnInit() {
    this.getNotifications()

    this.isReloadRequiredSubscription = this.notificationService
      .isReloadRequired()
      .subscribe((result) => {
        if (result) {
          this.getNotifications()
        }
      })

    this.notificationsSubscription = this.rxStompService
      .watch(`/topic/notifications.${this.currentUsername}`)
      .subscribe((message: Message) => {
        this.onNotificationRecieved(message)
      })
  }

  get currentUsername() {
    return this.authenticationService.getUserDetailsFromJWT().username
  }

  public selectNotification(notification: Notification) {
    const { id, username, processed, read, type } = notification
    if (processed) {
      return
    }

    if (!read) {
      --this.notificationCount
      notification.read = true
      this.notificationService.update(notification)
    }

    if (type === 'NEW_USER_CONNECTED') {
      this.startConversation(notification)
    }

    if (type === 'USER_DISCONNECTED') {
      this.router.navigate(['chatlog'], {
        queryParams: {
          username,
          id,
        },
      })
    }
  }

  public deleteNotification(e: MouseEvent, id: number) {
    e.stopPropagation()
    this.notificationService.delete(id)
  }

  private onNotificationRecieved(message: Message) {
    const notification = JSON.parse(message.body)
    this.notifications.unshift(notification)
    ++this.notificationCount
  }

  private async startConversation(notification: Notification) {
    const conversationRequest: ConversationRequest = <ConversationRequest>{
      chatUser: notification.username,
    }

    try {
      const response = await this.notificationService
        .startConversation(conversationRequest)
        .toPromise()
      this.alertService.handleResponse(response)
      notification.processed = true
      this.notificationService.update(notification)
      this.router.navigate(['chat'])
    } catch (error) {
      this.alertService.error(error)
    }
  }

  private async getNotifications() {
    try {
      this.notifications = await this.notificationService.get().toPromise()
      const unreadNotifications = this.notifications.filter(
        (notification) => !notification.read,
      )
      this.notificationCount = unreadNotifications.length
    } catch (error) {
      this.alertService.error(error)
    }
  }

  ngOnDestroy() {
    this.notificationsSubscription.unsubscribe()
    this.isReloadRequiredSubscription.unsubscribe()
  }
}
