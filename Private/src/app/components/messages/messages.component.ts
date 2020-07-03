import { Component, Input, OnInit } from '@angular/core'
import { ChatMessage } from 'src/app/models/chat.message'

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss'],
})
export class MessagesComponent implements OnInit {
  @Input() username: string
  @Input() isLoading: boolean
  @Input() isDisconnected: boolean
  @Input() messages: ChatMessage[]
  constructor() {}

  ngOnInit() {}
}
