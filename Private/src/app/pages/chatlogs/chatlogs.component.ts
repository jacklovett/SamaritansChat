import { Component, OnInit } from '@angular/core';
import { AlertService } from 'src/app/services/alert.service';
import { Router } from '@angular/router';

import { ChatLog } from 'src/app/models/chat.log';
import { ChatLogService } from 'src/app/services/chatlog.service';

import { ColumnDetails } from 'src/app/components/table/columnDetails';

@Component({
  selector: 'app-chatlogs',
  templateUrl: './chatlogs.component.html',
  styleUrls: ['./chatlogs.component.scss'],
})
export class ChatLogsComponent implements OnInit {
  loading = false;

  chatLogs: ChatLog[] = [];

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
  ];

  constructor(
    private router: Router,
    private chatLogService: ChatLogService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.loadChatLogs();
  }

  private async loadChatLogs() {
    this.loading = true;
    try {
      this.chatLogs = await this.chatLogService.get().toPromise();
    } catch (error) {
      this.alertService.error(error);
    }
    this.loading = false;
  }

  goToTranscript = (id: number) => this.router.navigate(['transcript', id]);
}
