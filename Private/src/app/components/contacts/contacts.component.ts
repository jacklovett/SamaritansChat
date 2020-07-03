import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter,
} from '@angular/core'
import { Subscription } from 'rxjs'
import { AlertService } from 'src/app/services/alert.service'
import { ChatUser } from 'src/app/models/chat.user'
import { ChatService } from 'src/app/services/chat.service'
import { ChatMessage } from 'src/app/models/chat.message'

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss'],
})
export class ContactsComponent implements OnInit, OnDestroy {
  @Input() activeChat: ChatUser
  @Input() chatMessages: ChatMessage[]
  @Output() activeChatSet = new EventEmitter<ChatUser>()

  loading = false
  chatUsers: ChatUser[] = []

  activeUsersSubscription: Subscription

  constructor(
    private chatService: ChatService,
    private alertService: AlertService,
  ) {
    this.loadActiveUsers()
  }

  ngOnInit(): void {}

  loadActiveUsers() {
    this.loading = true
    this.activeUsersSubscription = this.chatService.fetchChatUsers().subscribe(
      (users) => {
        this.chatUsers = users.map((username) => {
          return {
            username,
            unreadMessageCount: this.getUnreadMessageCount(username),
          }
        })

        const storedActiveChat = this.getStoredActiveChat()
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

  addContact(username: string) {
    this.chatUsers.push({
      username,
      unreadMessageCount: 0,
    })
  }

  removeContact(sender: string) {
    this.chatUsers.forEach((user, index) => {
      if (user.username === sender) {
        this.chatUsers.splice(index, 1)
      }
    })
  }

  updateUnreadMessageCount(sender: string) {
    this.chatUsers.forEach((user: ChatUser) => {
      if (sender !== user.username) {
        return
      }
      user.unreadMessageCount = this.getUnreadMessageCount(sender)
    })
  }

  setActiveChat(chatUser: ChatUser) {
    // update unread messages of current chat before changing
    if (this.activeChat) {
      this.updateUnreadMessages(this.activeChat.username)
    }

    if (chatUser.unreadMessageCount > 0) {
      chatUser.unreadMessageCount = 0
      this.updateUnreadMessages(chatUser.username)
    }

    const newActiveChatUser: ChatUser = <ChatUser>{
      username: chatUser.username,
      unreadMessageCount: 0,
    }

    localStorage.setItem('activeChat', JSON.stringify(newActiveChatUser))

    this.activeChatSet.emit(newActiveChatUser)
  }

  isChatUserConnected(username: string) {
    return this.chatUsers.some((user: ChatUser) => user.username === username)
  }

  private updateUnreadMessages(username: string) {
    this.chatService.updateUnreadMessages(username)
  }

  private getUnreadMessageCount(user: string) {
    return this.chatMessages.filter(
      (chatMessage) => chatMessage.sender === user && !chatMessage.read,
    ).length
  }

  private getStoredActiveChat(): ChatUser {
    return JSON.parse(localStorage.getItem('activeChat'))
  }

  ngOnDestroy() {
    this.activeUsersSubscription.unsubscribe()
  }
}
