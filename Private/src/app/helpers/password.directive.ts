import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[appPassword]',
})
export class PasswordDirective {
  showIcon =
    '<mat-icon class="material-icons material-icons-btn md-14">visibility</mat-icon>';
  hideIcon =
    '<mat-icon class="material-icons material-icons-btn md-14">visibility_off</mat-icon>';

  private showPassword = false;

  constructor(private element: ElementRef) {
    this.setup();
  }

  setup() {
    const parent = this.element.nativeElement.parentNode;
    const span = document.createElement('span');
    span.innerHTML = this.showIcon;
    span.addEventListener('click', () => {
      this.toggle(span);
    });
    parent.appendChild(span);
  }

  toggle(span: HTMLElement) {
    this.showPassword = !this.showPassword;
    if (this.showPassword) {
      this.element.nativeElement.setAttribute('type', 'text');
      span.innerHTML = this.hideIcon;
    } else {
      this.element.nativeElement.setAttribute('type', 'password');
      span.innerHTML = this.showIcon;
    }
  }
}
