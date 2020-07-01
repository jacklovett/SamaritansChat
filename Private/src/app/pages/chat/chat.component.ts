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
  loading = false

  isContactsVisible = true
  isActiveChatConnected = false

  chatForm: FormGroup
  activeChat: ChatUser

  chatUsers: ChatUser[] = []
  chatMessages: ChatMessage[] = []
  displayMessages: ChatMessage[] = []

  activeUsersSubscription: Subscription
  chatMessagesSubscription: Subscription
  stompMessagesSubscription: Subscription

  constructor(
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {
    this.loadChatMessage()
    this.loadActiveUsers()
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

  loadActiveUsers() {
    this.loading = true
    this.activeUsersSubscription = this.chatService.fetchChatUsers().subscribe(
      (users) => {
        this.chatUsers = users.map((user) => {
          return {
            username: user,
            unreadMessageCount: this.getUnreadMessageCount(user),
          }
        })

        const storedActiveChat: ChatUser = JSON.parse(
          localStorage.getItem('activeChat'),
        )
        if (!this.activeChat && storedActiveChat) {
          this.setActiveChat(storedActiveChat)
        }
      },
      (error) => {
        this.alertService.error(error)
      },
    )
    this.loading = false
  }

  private loadChatMessage() {
    this.loading = true
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
    this.loading = false
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
      this.activeChat?.username === sender ||
      this.activeChat?.username === recipient
    ) {
      this.displayMessages.push(message)
    } else {
      this.chatUsers.forEach((user: ChatUser) => {
        if (sender !== user.username) {
          return
        }
        user.unreadMessageCount = this.getUnreadMessageCount(sender)
      })
    }
  }

  private handleLeaveMessage(message: ChatMessage) {
    this.chatUsers.forEach((user, index) => {
      if (user.username === message.sender) {
        this.chatUsers.splice(index, 1)
      }
    })

    if (this.activeChat?.username === message.sender) {
      this.isActiveChatConnected = false
    }
  }

  private updateUnreadMessages(username: string) {
    this.chatService.updateUnreadMessages(username)
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
    this.isActiveChatConnected = this.isChatUserConnected(this.activeChat)

    if (chatUser.unreadMessageCount > 0) {
      chatUser.unreadMessageCount = 0
      this.updateUnreadMessages(chatUser.username)
    }
  }

  private isChatUserConnected(chatUser: ChatUser) {
    return this.chatUsers.some(
      (user: ChatUser) => user.username === chatUser.username,
    )
  }

  private getUnreadMessageCount(user: string) {
    return this.chatMessages.filter(
      (chatMessage) => chatMessage.sender === user && !chatMessage.read,
    ).length
  }

  disableForm(): boolean {
    return !this.activeChat || this.loading || !this.isActiveChatConnected
  }

  toggleContacts() {
    this.isContactsVisible = !this.isContactsVisible
  }

  ngOnDestroy() {
    this.activeUsersSubscription.unsubscribe()
    this.chatMessagesSubscription.unsubscribe()
    this.stompMessagesSubscription.unsubscribe()
  }
}
