import { Component, OnDestroy, OnInit } from '@angular/core'
import { FormBuilder, FormGroup } from '@angular/forms'
import { Subscription } from 'rxjs'

import { ChatUser } from 'src/app/models/chat.user'
import { ChatMessage } from 'src/app/models/chat.message'

import { ChatService } from 'src/app/services/chat.service'
import { AlertService } from 'src/app/services/alert.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

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

  activeChatUsersSubscription: Subscription
  disconnectedChatUsersSubscription: Subscription
  chatMessagesSubscription: Subscription

  constructor(
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private authenticationService: AuthenticationService,
  ) {
    this.activeChatUsersSubscription = this.chatService
      .getChatUsers()
      .subscribe((user) => {
        // maybe reload entire list from backend instead of relying on list state
        this.chatUsers.push(user)
      })

    this.disconnectedChatUsersSubscription = this.chatService
      .getDisconnectedChatUsers()
      .subscribe((disconnectedUser) => {
        this.chatUsers.forEach((user, index) => {
          if (user.username === disconnectedUser) {
            this.chatUsers.splice(index, 1)
          }
        })

        if (this.activeChat.username === disconnectedUser) {
          this.isActiveChatConnected = false
        }
      })

    this.chatMessagesSubscription = this.chatService
      .getChatMessages()
      .subscribe((msg) => {
        this.handleNewMessage(msg)
      })

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
    this.chatService.connect()

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

    try {
      this.chatService.send(message)
    } catch (error) {
      this.alertService.error(error)
    }

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

  private handleNewMessage(message: ChatMessage) {
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
      for (let i = 0; i < this.chatUsers.length; i++) {
        if (sender === this.chatUsers[i].username) {
          ++this.chatUsers[i].unreadMessageCount
        }
      }
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
    this.activeChatUsersSubscription.unsubscribe()
    this.disconnectedChatUsersSubscription.unsubscribe()
    this.chatMessagesSubscription.unsubscribe()
  }
}
