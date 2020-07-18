import { Component, OnInit, OnDestroy } from '@angular/core'
import { AlertService } from 'src/app/services/alert.service'
import { Router } from '@angular/router'

import { ChatLog } from 'src/app/models/chat.log'
import { ChatLogService } from 'src/app/services/chatlog.service'

import { ColumnDetails } from 'src/app/components/table/columnDetails'
import { Subscription } from 'rxjs'

@Component({
  selector: 'app-chatlogs',
  templateUrl: './chatlogs.component.html',
})
export class ChatLogsComponent implements OnInit, OnDestroy {
  isLoading = false

  chatLogs: ChatLog[] = []

  columnDetails: ColumnDetails[] = [
    {
      id: 'volunteer',
      title: 'Volunteer',
      value: (chatlog: ChatLog) => chatlog.volunteer,
    },
    {
      id: 'username',
      title: 'Username',
      value: (chatLog: ChatLog) => chatLog.username,
    },
    {
      id: 'rating',
      title: 'Rating',
      value: (chatLog: ChatLog) => `${chatLog.rating}`,
    },
    {
      id: 'start-time',
      title: 'Start Time',
      value: (chatLog: ChatLog) => chatLog.startTime,
      isDateTime: true,
    },
    {
      id: 'end-time',
      title: 'End Time',
      value: (chatLog: ChatLog) => chatLog.endTime,
      isDateTime: true,
    },
    {
      id: 'transcript',
      title: 'Transcript',
      value: (chatLog: ChatLog) => `${chatLog.transcriptId}`,
      iconName: 'description',
      tooltip: 'See Transcript',
      onClick: (id: number) => this.goToTranscript(id),
    },
  ]

  chatLogsSubscription: Subscription

  constructor(
    private router: Router,
    private chatLogService: ChatLogService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.loadChatLogs()
  }

  private async loadChatLogs() {
    this.isLoading = true
    this.chatLogsSubscription = this.chatLogService.get().subscribe(
      (logs) => {
        this.chatLogs = logs
      },
      (error) => {
        this.alertService.error(error)
      },
    )
    this.isLoading = false
  }

  goToTranscript = (id: number) => this.router.navigate(['transcript', id])

  ngOnDestroy() {
    this.chatLogsSubscription.unsubscribe()
  }
}
