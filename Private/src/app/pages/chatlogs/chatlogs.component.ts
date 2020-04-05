import { Component, OnInit, ViewChild } from '@angular/core';
import { AlertService } from 'src/app/services/alert.service';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

import { ChatLog } from 'src/app/models/chat.log';
import { ChatLogService } from 'src/app/services/chatlog.service';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-chatlogs',
  templateUrl: './chatlogs.component.html',
  styleUrls: ['./chatlogs.component.scss'],
})
export class ChatLogsComponent implements OnInit {
  loading = false;

  displayedColumns: string[] = [
    'volunteer',
    'username',
    'rating',
    'startTime',
    'endTime',
    'transcript',
  ];

  chatLogs: ChatLog[] = [];
  dataSource: MatTableDataSource<ChatLog>;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private router: Router,
    private chatLogService: ChatLogService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.loadChatLogs();
  }

  search(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = searchValue.trim().toLowerCase();
  }

  private async loadChatLogs() {
    this.loading = true;
    try {
      this.chatLogs = await this.chatLogService.get().toPromise();
      setTimeout(() => {
        this.dataSource = new MatTableDataSource<ChatLog>(this.chatLogs);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    } catch (error) {
      this.alertService.error(error);
    }
    this.loading = false;
  }

  goToTranscript(id: number) {
    this.router.navigate(['transcript', id]);
  }
}
