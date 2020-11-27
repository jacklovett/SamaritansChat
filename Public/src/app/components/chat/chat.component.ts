import { Component, OnInit, OnDestroy } from '@angular/core'
import { Subscription } from 'rxjs'
import { FormBuilder, FormGroup } from '@angular/forms'

import { ChatService } from 'src/app/services/chat.service'
import { AlertService } from 'src/app/services/alert.service'
import { ChatMessage } from 'src/app/models/chat.message'
import { AuthenticationService } from 'src/app/services/authentication.service'
import { ActivatedRoute } from '@angular/router'
import { RxStompService } from '@stomp/ng2-stompjs'
import { Message } from 'stompjs'

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {
  displayChat = true
  samaritansUsername = 'Sam'
  volunteerConnected = true
  conversationInProgress = false
  chatMessages: ChatMessage[] = []
  chatForm: FormGroup
  loading = false

  private displaySubscription: Subscription
  private stompMessagesSubscription: Subscription
  private isVolunteerConnectedSubscription: Subscription

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {
    this.conversationInProgress = JSON.parse(
      localStorage.getItem('conversationInProgress'),
    )

    this.loadConversation()
    this.isVolunteerActive()
  }

  ngOnInit() {
    this.stompMessagesSubscription = this.rxStompService
      .watch(`/topic/${this.username}`)
      .subscribe((message: Message) => {
        this.handleMessage(JSON.parse(message.body))
      })

    this.displaySubscription = this.route.queryParamMap.subscribe((params) => {
      const display = params.get('display')
      this.displayChat = display !== 'hide'
    })

    this.connectToChat()

    this.chatForm = this.formBuilder.group({
      message: [''],
    })
  }

  get username() {
    return this.authenticationService.getUsername()
  }

  get controls() {
    return this.chatForm.controls
  }

  private connectToChat = () => {
    const message: ChatMessage = <ChatMessage>{
      sender: this.username,
      type: 'JOIN',
    }

    this.chatService.publishMessage('send.addActiveUser', message)
  }

  onSend() {
    // return if message is empty
    if (!this.controls.message.value) {
      return
    }

    const message: ChatMessage = <ChatMessage>{
      sender: this.username,
      recipient: this.samaritansUsername,
      content: this.controls.message.value,
      type: 'CHAT',
    }

    this.chatService.publishMessage('send.message', message)
    this.clearForm()
  }

  private async isVolunteerActive() {
    this.isVolunteerConnectedSubscription = this.chatService
      .isVolunteerActive()
      .subscribe(
        (response) => {
          this.volunteerConnected = response.success
        },
        (error) => {
          console.log(error)
        },
      )
  }

  private async loadConversation() {
    this.loading = true
    this.chatService.fetchChatMessages(this.username).subscribe(
      (messages) => {
        this.chatMessages = messages
        this.loading = false
      },
      (error) => {
        this.alertService.error(error)
        this.loading = false
      },
    )
  }

  private handleMessage = (message: ChatMessage) => {
    const { type } = message

    switch (type) {
      case 'CHAT':
        this.handleChatMessage(message)
        break
      case 'JOIN':
        this.handleJoinMessage(message)
        break
      case 'LEAVE':
        this.handleLeaveMessage(message)
        break
      default:
        throw Error(`Unknown message type ${type}`)
    }
  }

  private handleChatMessage(message: ChatMessage) {
    if (
      message.sender === this.username ||
      message.recipient === this.username
    ) {
      this.chatMessages.push(message)
    }
  }

  private handleJoinMessage(message: ChatMessage) {
    if (message.sender !== this.samaritansUsername) {
      return
    }

    this.volunteerConnected = true
    this.conversationInProgress = true
    localStorage.setItem('conversationInProgress', JSON.stringify(true))
    this.alertService.success(`${this.samaritansUsername} Connected!`)
  }

  private handleLeaveMessage(message: ChatMessage) {
    if (message.sender !== this.samaritansUsername) {
      return
    }
    this.volunteerConnected = false
    this.alertService.error(`${this.samaritansUsername} Disconnected!`)
  }

  private clearForm() {
    this.chatForm.reset()
  }

  ngOnDestroy() {
    this.stompMessagesSubscription.unsubscribe()
    this.displaySubscription.unsubscribe()
    this.isVolunteerConnectedSubscription.unsubscribe()
  }
}
