import { Component, OnDestroy } from '@angular/core'
import { FormBuilder, FormGroup } from '@angular/forms'
import { Router, ActivatedRoute } from '@angular/router'

import { AlertService } from 'src/app/services/alert.service'
import { ChatService } from 'src/app/services/chat.service'
import { ChatLogService } from 'src/app/services/chatlog.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

import { ChatMessage } from 'src/app/models/chat.message'
import { ChatLog, ratings } from 'src/app/models/chat.log'
import { Subscription } from 'rxjs'

import { NotificationService } from 'src/app/services/notification.service'

@Component({
  selector: 'app-chatlog',
  templateUrl: './chatlog.component.html',
  styleUrls: ['./chatlog.component.scss'],
})
export class ChatLogComponent implements OnDestroy {
  loading = false
  displayChat = false
  rating = 0

  username = ''

  chatLogForm: FormGroup
  chatMessages: ChatMessage[] = []

  notificationId: string

  chatMessagesSubscription: Subscription
  notificationSubscription: Subscription

  chatRatings = ratings

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private chatLogService: ChatLogService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
  ) {
    this.chatLogForm = this.formBuilder.group({
      volunteer: [{ value: this.volunteer, disabled: true }],
      username: [{ value: this.username, disabled: true }],
      rating: [this.rating],
      notes: [''],
    })

    this.notificationSubscription = this.route.queryParamMap.subscribe(
      (params) => {
        this.username = params.get('username')
        this.notificationId = params.get('id')
        this.populate(this.username)
      },
    )
  }

  populate(username: string) {
    this.controls.username.setValue(username)
    this.loadConversation(username)
  }

  get volunteer() {
    return this.authenticationService.getUserDetailsFromJWT().username
  }

  get controls() {
    return this.chatLogForm.controls
  }

  private loadConversation(username: string) {
    this.loading = true
    this.chatMessagesSubscription = this.chatService
      .fetchChatMessages(username)
      .subscribe(
        (messages) => {
          this.chatMessages = messages
        },
        (error) => {
          this.alertService.error(error)
        },
      )
    this.loading = false
  }

  onSubmit() {
    this.loading = true
    const chatLog: ChatLog = <ChatLog>{
      volunteer: this.controls.volunteer.value,
      username: this.controls.username.value,
      notes: this.controls.notes.value,
      rating: this.controls.rating.value,
    }

    this.chatLogService.save(chatLog).subscribe(
      (response) => {
        this.alertService.handleResponse(response)
        if (this.notificationId && response.success) {
          this.notificationService.update({
            id: +this.notificationId,
            read: true,
            processed: true,
          })
          this.router.navigate(['chatlogs'])
        }
      },
      (error) => {
        this.alertService.error(error)
      },
    )
    this.loading = false
  }

  toggleConversation() {
    this.displayChat = !this.displayChat
  }

  ngOnDestroy() {
    this.notificationSubscription.unsubscribe()
    this.chatMessagesSubscription.unsubscribe()
  }
}
