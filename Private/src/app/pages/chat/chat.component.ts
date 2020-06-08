import { Component, OnDestroy, OnInit } from '@angular/core'
import { FormBuilder, FormGroup } from '@angular/forms'
import { Subscription } from 'rxjs'

import { ChatUser } from 'src/app/models/chat.user'
import { ChatMessage } from 'src/app/models/chat.message'

import { ChatService } from 'src/app/services/chat.service'
import { AlertService } from 'src/app/services/alert.service'
import { AuthenticationService } from 'src/app/services/authentication.service'
import { RxStompService } from '@stomp/ng2-stompjs'
import { Message } from 'stompjs'

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {
  chatForm: FormGroup
  activeChat: ChatUser
  isActiveChatConnected = false
  loading = false

  chatUsers: ChatUser[] = []
  chatMessages: ChatMessage[] = []
  displayMessages: ChatMessage[] = []

  chatMessagesSubscription: Subscription

  constructor(
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {
    // inside .then() to ensure chatUsers is populated before we see if activeChat is connected
    this.loadActiveUsers().then(() => {
      const storedActiveChat: ChatUser = JSON.parse(
        localStorage.getItem('activeChat'),
      )
      if (!this.activeChat && storedActiveChat) {
        this.setActiveChat(storedActiveChat)
      }
    })
  }

  ngOnInit() {
    this.chatMessagesSubscription = this.rxStompService
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

  public async onSend() {
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

  private async loadActiveUsers() {
    this.loading = true
    try {
      this.chatUsers = []
      this.chatUsers = await this.chatService.fetchChatUsers().toPromise()
    } catch (error) {
      this.alertService.error(error)
    }
    this.loading = false
  }

  private async loadConversation(username: string) {
    this.loading = true
    try {
      this.displayMessages = await this.chatService
        .getConversationByUsername(username)
        .toPromise()
    } catch (error) {
      this.alertService.error(error)
    }
    this.loading = false
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

    const newChatUser: ChatUser = <ChatUser>{
      username: sender,
      unreadMessageCount: 0,
    }
    this.chatUsers.push(newChatUser)
  }

  private handleChatMessage(message: ChatMessage) {
    const { sender, recipient } = message
    this.chatMessages.push(message)
    if (
      this.activeChat &&
      (this.activeChat.username === sender ||
        this.activeChat.username === recipient)
    ) {
      this.displayMessages.push(message)
    } else {
      // increases number of unread messages for relevant chat
      this.chatUsers.forEach((user: ChatUser) => {
        if (sender !== user.username) {
          return
        }
        ++user.unreadMessageCount
      })
    }
  }

  private handleLeaveMessage(message: ChatMessage) {
    this.chatUsers.forEach((user, index) => {
      if (user.username === message.sender) {
        this.chatUsers.splice(index, 1)
      }
    })

    if (this.activeChat.username === message.sender) {
      this.isActiveChatConnected = false
    }
  }

  private updateUnreadMessages(username: string) {
    try {
      this.chatService.updateUnreadMessages(username)
    } catch (error) {
      console.log('Unable to update unread messages for user: ' + username)
      console.log(error)
    }
  }

  private clearForm() {
    this.chatForm.reset()
  }

  setActiveChat(chatUser: ChatUser) {
    // update unread messages of current chat before changing
    if (this.activeChat) {
      this.updateUnreadMessages(this.activeChat.username)
    }

    this.loadConversation(chatUser.username)

    const newActiveChatUser: ChatUser = <ChatUser>{
      username: chatUser.username,
      unreadMessageCount: 0,
    }

    this.activeChat = newActiveChatUser
    localStorage.setItem('activeChat', JSON.stringify(this.activeChat))
    this.isActiveChatUserConnected(this.activeChat)

    if (chatUser.unreadMessageCount > 0) {
      chatUser.unreadMessageCount = 0
      this.updateUnreadMessages(chatUser.username)
    }
  }

  private isActiveChatUserConnected(chatUser: ChatUser) {
    // try to get .includes() working. upgrade to ES6+ somehow ??
    this.isActiveChatConnected = false
    this.chatUsers.forEach((user) => {
      if (user.username === chatUser.username) {
        this.isActiveChatConnected = true
        return
      }
    })
  }

  disableForm(): boolean {
    return !this.activeChat || this.loading || !this.isActiveChatConnected
  }

  ngOnDestroy() {
    this.chatMessagesSubscription.unsubscribe()
  }
}
