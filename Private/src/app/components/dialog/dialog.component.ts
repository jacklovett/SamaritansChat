import { Component, Inject } from '@angular/core'
import { MAT_DIALOG_DATA } from '@angular/material/dialog'

export interface DialogProps {
  title: string
  content: string
  successLabel: string
  cancelLabel: string
}

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
})
export class DialogComponent {
  props: DialogProps
  SUBMIT_VALUE = 'CONFIRMED'
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogProps) {
    this.props = data
  }
}
