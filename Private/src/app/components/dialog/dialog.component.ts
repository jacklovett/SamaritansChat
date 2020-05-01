import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogProps {
  title: string;
  content: string;
  submitValue: string;
  successLabel: string;
  cancelLabel: string;
}

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss'],
})
export class DialogComponent {
  props: DialogProps;
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogProps) {
    this.props = data;
  }
}
