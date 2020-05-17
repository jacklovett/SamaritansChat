import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { FormBuilder, FormGroup } from '@angular/forms';

import { ChatService } from 'src/app/services/chat.service';
import { AlertService } from 'src/app/services/alert.service';
import { ChatMessage } from 'src/app/models/chat.message';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {
  displayChat = true;
  samaritansUsername = 'Sam';
  volunteerConnected = true;
  conversationInProgress = false;
  chatMessages: ChatMessage[] = [];
  chatForm: FormGroup;
  loading = false;

  private displaySubscription: Subscription;
  private chatMessagesSubscription: Subscription;
  private connectedVolunteerSubscription: Subscription;
  private disconnectedVolunteerSubscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private chatService: ChatService,
    private authenticationService: AuthenticationService,
  ) {
    this.conversationInProgress = JSON.parse(
      localStorage.getItem('conversationInProgress'),
    );

    this.loadConversation();

    this.isVolunteerActive();

    this.chatMessagesSubscription = this.chatService
      .getChatMessages()
      .subscribe((msg) => {
        this.handleMessage(msg);
      });

    this.connectedVolunteerSubscription = this.chatService
      .getVolunteer()
      .subscribe((user) => {
        this.conversationInProgress = true;
        localStorage.setItem('conversationInProgress', JSON.stringify(true));
        this.volunteerConnected = true;
      });

    this.disconnectedVolunteerSubscription = this.chatService
      .getDisconnectedVolunteer()
      .subscribe(() => {
        this.volunteerConnected = false;
      });
  }

  ngOnInit() {
    this.chatService.connect();
    this.displaySubscription = this.route.queryParamMap.subscribe((params) => {
      const display = params.get('display');
      this.displayChat = display !== 'hide';
    });

    this.chatForm = this.formBuilder.group({
      message: [''],
    });
  }

  get username() {
    return this.authenticationService.getUsername();
  }

  get controls() {
    return this.chatForm.controls;
  }

  public async onSend() {
    // return if message is empty
    if (!this.controls.message.value) {
      return;
    }

    const message: ChatMessage = <ChatMessage>{
      sender: this.username,
      recipient: this.samaritansUsername,
      content: this.controls.message.value,
      type: 'CHAT',
    };

    try {
      this.chatService.send(message);
    } catch (error) {
      this.alertService.error(error);
    }

    this.clearForm();
  }

  private async isVolunteerActive() {
    try {
      if (this.conversationInProgress) {
        const response = await this.chatService.isVolunteerActive().toPromise();
        this.volunteerConnected = response.success;
      }
    } catch (error) {
      console.log(error);
    }
  }

  private async loadConversation() {
    this.loading = true;
    try {
      this.chatMessages = [];
      this.chatMessages = await this.chatService.getConversation().toPromise();
    } catch (error) {
      this.alertService.error(error);
    }
    this.loading = false;
  }

  private handleMessage(message: any) {
    this.chatMessages.push(message);
  }

  private clearForm() {
    this.chatForm.reset();
  }

  ngOnDestroy() {
    this.chatMessagesSubscription.unsubscribe();
    this.displaySubscription.unsubscribe();
    this.connectedVolunteerSubscription.unsubscribe();
    this.disconnectedVolunteerSubscription.unsubscribe();
  }
}
