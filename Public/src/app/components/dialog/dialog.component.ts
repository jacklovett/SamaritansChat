import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent {
  modalTitle: string;
  modalContent: string;
  modalSubmitMessage: string;
  modalSubmitValue: string;
  modalCancel: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    this.modalTitle = data.title;
    this.modalContent = data.content;
    this.modalSubmitMessage = data.submitMessage;
    this.modalSubmitValue = data.submitValue;
    this.modalCancel = data.cancel;
  }
}
