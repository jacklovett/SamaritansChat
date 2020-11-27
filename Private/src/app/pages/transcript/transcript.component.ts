import { Component, OnInit, OnDestroy } from '@angular/core'

import { AlertService } from 'src/app/services/alert.service'
import { ChatLogService } from 'src/app/services/chatlog.service'

import { ChatMessage } from 'src/app/models/chat.message'
import { Transcript } from 'src/app/models/transcript'
import { ActivatedRoute, Router } from '@angular/router'
import { Subscription } from 'rxjs'
import { Rating, ratings } from 'src/app/models/chat.log'

@Component({
  selector: 'app-transcript',
  templateUrl: './transcript.component.html',
  styleUrls: ['./transcript.component.scss'],
})
export class TranscriptComponent implements OnInit, OnDestroy {
  isLoading = false

  transcriptId: number
  transcript: Transcript

  username: string
  volunteer: string
  rating: Rating
  notes: string

  chatMessages: ChatMessage[] = []

  transcriptSubscription: Subscription
  transcriptIdSubscription: Subscription

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private chatLogService: ChatLogService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.transcriptIdSubscription = this.route.params.subscribe((params) => {
      this.transcriptId = +params['id']
    })

    this.loadTranscript(this.transcriptId)
  }

  private loadTranscript(id: number) {
    this.isLoading = true
    this.transcriptSubscription = this.chatLogService
      .getTranscriptById(id)
      .subscribe(
        (transcript) => {
          this.transcript = transcript
          this.populateComponent(transcript)
          this.isLoading = false
        },
        (error) => {
          this.alertService.error(error)
          this.isLoading = false
        },
      )
  }

  populateComponent(transcript: Transcript) {
    this.volunteer = transcript.volunteer
    this.username = transcript.username
    this.notes = transcript.notes
    this.chatMessages = JSON.parse(transcript.conversation)
    this.rating = ratings.find((rating) => rating.id === transcript.rating)
  }

  returnToChatLogs() {
    this.router.navigate(['chatlogs'])
  }

  ngOnDestroy() {
    this.transcriptSubscription.unsubscribe()
    this.transcriptIdSubscription.unsubscribe()
  }
}
