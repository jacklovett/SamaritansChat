import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core'
import { FormBuilder, FormGroup } from '@angular/forms'
import { Subscription } from 'rxjs'

import { RxStompService } from '@stomp/ng2-stompjs'
import { Message } from 'stompjs'

import { ChatUser } from 'src/app/models/chat.user'
import { ChatMessage } from 'src/app/models/chat.message'

import { ChatService } from 'src/app/services/chat.service'
import { AlertService } from 'src/app/services/alert.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

import { ContactsComponent } from 'src/app/components/contacts/contacts.component'

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild(ContactsComponent)
  private contacts: ContactsComponent

  isLoading = false

  isContactsVisible = true
  isActiveChatConnected = false

  chatForm: FormGroup
  activeChat: ChatUser

  chatMessages: ChatMessage[] = []
  displayMessages: ChatMessage[] = []

  chatMessagesSubscription: Subscription
  stompMessagesSubscription: Subscription

  constructor(
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {
    this.loadChatMessages()
  }

  ngOnInit() {
    this.stompMessagesSubscription = this.rxStompService
      .watch(`/topic/${this.currentUsername}`)
      .subscribe((message: Message) => {
        this.onMessageRecieved(JSON.parse(message.body))
      })

    this.chatForm = this.formBuilder.group({
      message: [''],
    })
  }

  get currentUsername() {
    return this.authenticationService.getUserDetailsFromJWT().username
  }

  get controls() {
    return this.chatForm.controls
  }

  async onSend() {
    if (!this.controls.message.value) {
      return
    }

    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      recipient: this.activeChat.username,
      content: this.controls.message.value,
      type: 'CHAT',
    }

    this.chatService.publishMessage('send.message', message)
    this.clearForm()
  }

  private loadChatMessages() {
    this.isLoading = true
    this.chatMessagesSubscription = this.chatService
      .fetchChatMessages(this.currentUsername)
      .subscribe(
        (messages) => {
          this.chatMessages = messages
        },
        (error) => {
          this.alertService.error(error)
        },
      )
    this.isLoading = false
  }

  private loadConversation(username: string) {
    this.displayMessages = this.chatMessages.filter(
      (message) =>
        message.recipient === username || message.sender === username,
    )
  }

  private onMessageRecieved(message: ChatMessage) {
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
        throw Error(`Unknown message type: ${type}`)
    }
  }

  private handleJoinMessage(message: ChatMessage) {
    const { sender } = message

    if (sender === 'Sam') {
      return
    }

    this.contacts.addContact(sender)
  }

  private handleChatMessage(message: ChatMessage) {
    const { sender, recipient } = message
    this.chatMessages.push(message)
    if (
      this.activeChat?.username === sender ||
      this.activeChat?.username === recipient
    ) {
      this.displayMessages.push(message)
    } else {
      this.contacts.updateUnreadMessageCount(sender)
    }
  }

  private handleLeaveMessage(message: ChatMessage) {
    const { sender } = message
    this.contacts.removeContact(sender)

    if (this.activeChat?.username === sender) {
      this.isActiveChatConnected = false
    }
  }

  private clearForm() {
    this.chatForm.reset()
  }

  onSetActiveChat(chatUser: ChatUser) {
    const { username } = chatUser
    this.activeChat = chatUser
    this.loadConversation(username)
    this.isActiveChatConnected = this.contacts.isChatUserConnected(username)
  }

  disableForm(): boolean {
    return !this.activeChat || this.isLoading || !this.isActiveChatConnected
  }

  toggleContacts() {
    this.isContactsVisible = !this.isContactsVisible
  }

  ngOnDestroy() {
    this.chatMessagesSubscription.unsubscribe()
    this.stompMessagesSubscription.unsubscribe()
  }
}
