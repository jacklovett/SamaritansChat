import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatLogsComponent } from './chatlogs.component';

describe('ChatLogsComponent', () => {
  let component: ChatLogsComponent;
  let fixture: ComponentFixture<ChatLogsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChatLogsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatLogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
