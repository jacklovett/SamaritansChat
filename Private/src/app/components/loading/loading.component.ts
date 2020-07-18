import { Component, OnInit, Input } from '@angular/core'

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
})
export class LoadingComponent implements OnInit {
  @Input() isLoading: boolean
  @Input() isCenter: boolean
  constructor() {}

  ngOnInit(): void {}
}
